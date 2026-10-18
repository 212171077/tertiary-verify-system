package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.entities.*;
import com.tertiary.verify.data_model.model.entities.Role;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import com.tertiary.verify.data_model.model.entities.enums.ERole;
import com.tertiary.verify.data_model.model.entities.lookup.*;
import com.tertiary.verify.data_model.model.repositories.*;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.mail.MailSender;
import com.tertiary.verify.service.lookup.CourseService;
import com.tertiary.verify.service.lookup.InstitutionTypeService;
import com.tertiary.verify.service.lookup.RejectReasonService;
import com.tertiary.verify.utils.AppUtility;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;
import javax.persistence.NonUniqueResultException;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InstitutionService extends AbstractService {
  private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionService.class);
  @Autowired private InstitutionRepository repository;
  @Autowired private AddressService addressService;
  @Autowired private InstitutionCourseService institutionCourseService;
  @Autowired private RoleRepository roleRepository;
  @Autowired private UserRoleRepository userRoleRepository;
  @Autowired private MailSender mailSender;
  @Autowired private InstCourseAddressService instCourseAddressService;
  @Autowired private UserRepository userRepository;
  @Autowired private TaskRepository taskRepository;
  @Autowired private InstitutionTypeService institutionTypeService;
  @Autowired private CourseService courseService;
  @Autowired private InstitutionRejectReasonService institutionRejectReasonService;
  @Autowired private RejectReasonService rejectReasonService;

  @Autowired private TertiaryVerifyService tertiaryVerifyService;

  @CacheEvict(value = "institutions", allEntries = true)
  public void saveInstitution(Institution institution) {
    if (institution.getId() == null) {
      institution.setDeleted(false);
      institution.setCreateDate(new Date());
    } else {
      institution.setLastUpdateDate(new Date());
    }
    repository.save(institution);
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public void createOrUpdateInstitution(
      RegisterInstitutionRequest request, ApprovalStatusEnum status) {
    LOGGER.info("Processing kafka message, Register Institution Request: {}", request);
    Institution institution = null;
    User user = null;
    if (request.getId() != null) {
      institution = findById(request.getId());
    }

    boolean createTask = false;
    if (institution == null) {
      user = userRepository.findByIdAndDeleted(request.getCreatorUserID(), false);
      createTask = true;
      institution = new Institution();

      institution.setCreatorUser(user);
      institution.setQrCode(
          "TV".concat(request.getAccreditationNumber().concat("".concat(getUniqueCode()))));
    }

    if (institution.getStatus() == null
        || institution.getStatus() != ApprovalStatusEnum.RegistrationInProgress) {
      institution.setStatus(status);
    }

    if (!isEndDateValid(request.getAccreditationStartDate(), request.getAccreditationEndDate())) {
      throw new ValidationException(
          "Invalid end date. The end date cannot be later than the start date and the end date cannot be earlier than today's date");
    }

    institution.setInstitutionName(request.getInstitutionName().trim());
    institution.setKnownAs(
        request.getKnownAs() == null || request.getKnownAs().equalsIgnoreCase("null")
            ? ""
            : request.getKnownAs());
    institution.setContactNumber(request.getContactNumber());
    institution.setAlternativeContactNumber(request.getAlternativeContactNumber());
    institution.setEmail(request.getEmail());
    institution.setAccreditationStartDate(request.getAccreditationStartDate());
    institution.setAccreditationEndDate(request.getAccreditationEndDate());
    institution.setAccreditationNumber(request.getAccreditationNumber());
    institution.setImage(
        request.getImage() == null || request.getImage().trim().isEmpty()
            ? institution.getImage()
            : request.getImage());
    institution.setApplyLink(
        (request.getApplyLink() == null || request.getApplyLink().equalsIgnoreCase("null"))
            ? ""
            : request.getApplyLink());
    institution.setInstitutionType(
        institutionTypeService.findByDescription(request.getInstitutionType()));

    saveInstitution(institution);

    if (createTask) {
      createInstitutionRegTask(
          user, Institution.class.getName(), institution.getId(), institution.getUuid());
    }
  }

  public boolean isEndDateValid(Date startDate, Date endDate) {
    if (startDate != null && endDate != null) {
      Date currentDate = new Date();
      // Check if endDate is not greater than startDate
      if (endDate.after(startDate)) {
        // Check if endDate is not less than current date
        return !endDate.before(currentDate); // End date is valid
      }
      return false; // End date is invalid
    } else {
      return true;
    }
  }

  private void createInstitutionRegTask(User user, String className, Long classId, String uuid) {
    Task task = new Task();
    task.setDescription("Complete Institution Registration");
    task.setUrl("#/pages/manage-institution?uuid=".concat(uuid));
    task.setUser(user);
    task.setCompleted(false);
    task.setImage("institution-reg.png");
    task.setCreateDate(new Date());
    task.setLastUpdateDate(new Date());
    task.setDeleted(false);
    task.setClassId(classId);
    task.setClassName(className);
    task.setObjective("This task pertains to finalizing your institution registration");
    task.setDeadline(AppUtility.addDaysToDate(new Date(), 15));
    task.setCreatedByUser(user);
    taskRepository.save(task);
    sendTaskCreationEmail(user);
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public void deleteInstitution(Institution institution) {
    institution.setDeleted(true);
    repository.save(institution);
  }

  @Cacheable("institutions")
  public List<Institution> findAllInstitution() {
    return repository.findByDeleted(false);
  }

  @CacheEvict(value = {"institutions", "institution_course"}, allEntries = true)
  public ResponseEntity<?> saveInstitutionCourse(InstitutionCourseRequest request) {

    Institution institution = findInstitutionById(request.getInstitutionId());
    if (institution == null) {
      LOGGER.error("Invalid institution ID :{} ", request.getInstitutionId());
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid institution ID", null));
    }

    Course course = courseService.findById(request.getCourseId());
    if (course == null) {
      LOGGER.error("Invalid course ID :{} ", request.getCourseId());
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid course ID", null));
    }

    List<Address> addressList = addressService.findByIds(request.getAddressIdList());
    if (addressList.size() != request.getAddressIdList().size()) {
      LOGGER.error(
          "Invalid address ID(s) Address List Size:{},  Request address List Size:{}",
          addressList.size(),
          request.getAddressIdList().size());
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid address ID(s)", null));
    }

    try {
      com.tertiary.verify.data_model.model.entities.InstitutionCourse instCourse =
          tertiaryVerifyService.findByInstitutionAndCourse(institution, course);

      if (instCourse != null) {
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Course already exist", null));
      }
    } catch (NonUniqueResultException e) {
      return ResponseEntity.badRequest()
          .body(
              new MessageResponse(
                  false,
                  "You have duplicate records for the selected course, please rectify",
                  null));
    }

    com.tertiary.verify.data_model.model.entities.InstitutionCourse institutionCourse =
        new com.tertiary.verify.data_model.model.entities.InstitutionCourse();
    institutionCourse.setInstitution(institution);
    institutionCourse.setCourse(course);
    institutionCourse.setCreateDate(new Date());

    ArrayList<InstCourseAddress> instCourseAddressList = new ArrayList<>();
    for (Address address : addressList) {
      InstCourseAddress instCourseAddress = new InstCourseAddress();
      instCourseAddress.setAddress(address);
      instCourseAddress.setInstitutionCourse(institutionCourse);
      instCourseAddress.setCreateDate(new Date());
      instCourseAddressList.add(instCourseAddress);
    }

    if (institution.getStatus() == null
        || institution.getStatus() != ApprovalStatusEnum.RegistrationInProgress) {
      institution.setStatus(ApprovalStatusEnum.UpdateInProgress);
    }

    saveInstitution(institution);

    institutionCourseService.saveInstitutionCourse(institutionCourse);
    instCourseAddressService.saveAll(instCourseAddressList);

    return ResponseEntity.ok()
        .body(new MessageResponse(true, "Institution course successfully saved", null));
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> confirmInstitutionDetails(ConfirmInstDetailsRequest request) {
    try {
      Institution institution = findInstitutionById(request.getInstitutionId());
      if (institution == null) {
        LOGGER.error("Invalid institution ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid institution ID", null));
      }
      if (!hasAllRequiredInformation(institution)) {
        LOGGER.error(
            "Not all required information has been submitted :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(
                new MessageResponse(
                    false,
                    "Incomplete submission: An institution must have at least one address and one offered course. ",
                    null));
      }
      User user = userRepository.findByIdAndDeleted(request.getUserId(), false);
      if (user == null) {
        LOGGER.error("Invalid user ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid user ID", null));
      }

      ApprovalStatusEnum tempStatus = institution.getStatus();
      institution.setStatus(ApprovalStatusEnum.PendingApproval);
      institution.setCreatorUser(user);
      User supervisor = Optional.ofNullable(institution.getSupervisor()).orElse(getSupervisor());

      institution.setSupervisor(supervisor);
      saveInstitution(institution);

      // Complete task
      closeTask(user, institution);

      if (supervisor != null) {
        if (tempStatus == ApprovalStatusEnum.RegistrationInProgress) {
          String mssg =
              "<p>I am writing to inform you that a new institution has been registered in our system. "
                  + "The institution name is "
                  + institution.getInstitutionName().trim()
                  + ", and it has been registered by a user named "
                  + user.getName()
                  + " "
                  + user.getSurname()
                  + ".</p>"
                  + "<p>As you may know, our system requires that all institutions "
                  + "be reviewed and approved by a supervisor before they can begin using our services. "
                  + "I wanted to bring this registration to your attention so that you can review the "
                  + "institution and ensure that it meets our requirements. Please login to the system and action the task</p>"
                  + "<p>Thank you for your attention to this matter.</p>";
          sendEmailNotificationToSupervisor(supervisor, mssg);
        } else {
          String mssg =
              "<p>I am writing to inform you that one of our registered "
                  + "institutions has been updated by a user. "
                  + "The institution in question is "
                  + institution.getInstitutionName().trim()
                  + ".</p>"
                  + "<p>As the supervisor of "
                  + institution.getInstitutionName().trim()
                  + ", "
                  + "we kindly request that you take a moment to log in and review "
                  + "the changes they made. We greatly value your input and "
                  + "appreciate your time in helping us ensure the accuracy of our records. </p>"
                  + "<p>Thank you for your attention to this matter.</p>";
          sendEmailNotificationToSupervisor(supervisor, mssg);
        }
      }
      return ResponseEntity.ok()
          .body(new MessageResponse(true, "Institution details submitted", null));
    } catch (Exception e) {
      LOGGER.error("Error", e);
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(new MessageResponse(false, "Service Unavailable", null));
    }
  }

  @CacheEvict(value = {"institutions", "institution_course"}, allEntries = true)
  public ResponseEntity<MessageResponse> updateImage(UpdatedImageRequest request) {
    Institution institution = repository.findByIdAndDeleted(request.getId(), false);
    if (institution != null) {
      institution.setImage(request.getImageName());
      repository.save(institution);
      return ResponseEntity.ok(
              MessageResponse.builder().success(true).message("Image updated successfully").build());
    } else {
      return ResponseEntity.badRequest().body(new MessageResponse(false, "Institution not found", null));
    }
  }

  private void closeTask(User user, Institution institution) {
    Task task =
        taskRepository.findByUserAndClassIdAndClassName(
            user, institution.getId(), Institution.class.getName());
    task.setCompleted(true);
    taskRepository.save(task);
  }

  public boolean hasAllRequiredInformation(Institution institution) {
    List<InstCourseAddress> list =
        instCourseAddressService.findByInstitutionId(institution.getId());
    return list != null && !list.isEmpty();
  }

  public List<Institution> searchInstitution(String searchTerm, Province province) {
    List<Institution> institutionList = findAllInstitution();
    List<Institution> returnList = new ArrayList<>();
    if (institutionList != null && !institutionList.isEmpty()) {
      List<Institution> list =
          institutionList.stream()
              .filter(
                  institution ->
                      institution.getInstitutionName().trim().contains(searchTerm)
                          || institution.getKnownAs().contains(searchTerm))
              .collect(Collectors.toList());
      for (Institution i : list) {
        for (Address address : i.getAddressList()) {
          if (address.getProvince() == province) {
            returnList.add(i);
            break;
          }
        }
      }
    }
    returnList.sort(Comparator.comparing(Institution::getInstitutionName));
    return returnList;
  }

  public List<Institution> findByAccreditationNumber(String searchTerm) {
    List<Institution> list = findAllInstitution();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(institution -> institution.getAccreditationNumber().equalsIgnoreCase(searchTerm))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public User getSupervisor() throws Exception {
    User user = null;
    Optional<Role> supervisorRole = roleRepository.findByName(ERole.ROLE_SPVS);
    if (supervisorRole.isPresent()) {
      List<UserRole> userRoleList = userRoleRepository.findByRole(supervisorRole.get());
      if (userRoleList != null && !userRoleList.isEmpty()) {
        long count = repository.countBySupervisorAndDeleted(userRoleList.get(0).getUser(), false);
        user = userRoleList.get(0).getUser();
        userRoleList.remove(userRoleList.get(0));
        for (UserRole ur : userRoleList) {
          long tempCount = repository.countBySupervisorAndDeleted(ur.getUser(), false);
          if (tempCount < count) {
            count = tempCount;
            user = ur.getUser();
          }
        }
      }
    }
    if (user == null) {
      throw new Exception("No supervisor to approve this institution details found");
    }
    return user;
  }

  private void sendTaskCreationEmail(User u) {
    String mssg =
        "<p>We hope this message finds you well. "
            + "An important task has been assigned to "
            + "you regarding the completion of your institution "
            + "registration. To access the task and complete"
            + " your registration, please follow these steps:</p>"
            + "<p><b>Steps to Complete Registration:</b></p>"
            + "<ul>"
            + "<li><strong>Log in: </strong> Visit our Tertiary Verify "
            + "website and log in to your account using your credentials.</li>"
            + "<li><strong>Notification Icon: </strong> Once logged in, "
            + "locate the notification icon (usually represented by a bell icon) "
            + "on the top navigation bar of the website.</li>"
            + "<li><strong>Click on Task:</strong> Click on the "
            + "notification icon to open the dropdown menu. You "
            + "should see a task titled Complete Institution Registration. "
            + "Click on this task to access the details.</li>"
            + "<li><strong>Complete Registration: </strong> Within the task details, you will "
            + "find instructions on how to finalize your registration. Follow the provided "
            + "steps to ensure your registration is complete.</li>"
            + "</ul>"
            + "<p>If you encounter any issues or have questions "
            + "during the registration process, please don't "
            + "hesitate to reach out to our support team</p>";

    try {
      String welcome = "<p>Dear #NAME#,</p>" + mssg;
      welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());
      Mail mail = new Mail();
      mail.setContent(welcome);
      mail.setFrom("info@tertiaryverify.co.za");
      String[] to = {u.getEmail()};
      mail.setTo(to);
      mail.setSubject("Complete Institution Registration");
      mail.setCc(to);
      mailSender.saveEmail(mail);
    } catch (Exception e) {
      logger.error("Error: ", e);
    }
  }

  private void sendPrioritisationEmailEmail(User u) {
    String mssg =
        "<p>We hope this message finds you well. "
            + "An important task has been assigned to "
            + "you regarding the completion institution prioritisation. "
            + "To access the task and action it, please follow these steps:</p>"
            + "<p><b>Steps to prioritise/de-prioritise:</b></p>"
            + "<ul>"
            + "<li><strong>Log in: </strong> Visit our Tertiary Verify "
            + "website and log in to your account using your credentials.</li>"
            + "<li><strong>Notification Icon: </strong> Once logged in, "
            + "locate the notification icon (usually represented by a bell icon) "
            + "on the top navigation bar of the website.</li>"
            + "<li><strong>Click on Task:</strong> Click on the "
            + "notification icon to open the dropdown menu. You "
            + "should see a task titled Institution prioritisation. "
            + "Click on this task to access the details.</li>"
            + "<li><strong>Action the task: </strong> Approve or reject the change</li>"
            + "</ul>"
            + "<p>If you encounter any issues or have questions "
            + "during the registration process, please don't "
            + "hesitate to reach out to our support team</p>";

    try {
      String welcome = "<p>Dear #NAME#,</p>" + mssg;
      welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());
      Mail mail = new Mail();
      mail.setContent(welcome);
      mail.setFrom("info@tertiaryverify.co.za");
      String[] to = {u.getEmail()};
      mail.setTo(to);
      mail.setSubject("Institution prioritisation");
      mail.setCc(to);
      mailSender.saveEmail(mail);
    } catch (Exception e) {
      logger.error("Error: ", e);
    }
  }

  private void sendEmailNotificationToSupervisor(User u, String mssg) {

    try {
      String welcome = "<p>Dear #NAME#,</p>" + mssg;
      welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());
      Mail mail = new Mail();
      mail.setContent(welcome);
      mail.setFrom("info@tertiaryverify.co.za");
      String[] to = {u.getEmail()};
      mail.setTo(to);
      mail.setSubject("Institution Registration");
      mail.setCc(to);
      mailSender.saveEmail(mail);
    } catch (Exception e) {
      logger.error("Error", e);
    }
  }

  public void sendApprovedInstitutionEmail(User u, String instName) {

    try {
      String welcome =
          "<p>Dear #NAME#,</p>"
              + "<p>We are pleased to inform you that "
              + instName
              + " registration has been approved by our team.</p>"
              + "<p>Your application has successfully met all the necessary requirements and standards, "
              + "and we are delighted to welcome you to our community.</p>";

      welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());
      Mail mail = new Mail();
      mail.setContent(welcome);
      mail.setFrom("info@tertiaryverify.co.za");
      String[] to = {u.getEmail()};
      mail.setTo(to);
      mail.setSubject("Institution Approved");
      mail.setCc(to);
      mailSender.saveEmail(mail);
    } catch (Exception e) {
      logger.error("Error ", e);
    }
  }

  public void sendRejectedInstitutionEmail(
      User u, String instName, List<RejectReason> rejectReasons) {
    try {
      StringBuilder rejectReason = new StringBuilder("<ul>");
      for (RejectReason reason : rejectReasons) {
        rejectReason
            .append("<li>" + "<strong>")
            .append(reason.getDescription())
            .append("</strong>")
            .append("<br/>")
            .append("<p style=\"color: gray; margin-top: 0\">")
            .append(reason.getDetailInfo())
            .append("</p>")
            .append("</li>");
      }
      rejectReason.append("</ul>");

      String welcome =
          "<p>Dear #NAME#,</p>"
              + "<p>We regret to inform you that "
              + instName
              + " registration  on tertiary verify "
              + " has been rejected.</p>"
              + "<p>Our team carefully evaluated your application and determined that it does not "
              + "meet our requirements for registration. The reasons for the rejection are as follows:</p>"
              + rejectReason
              + "<p>I understand that this news may be disappointing, and I want to assure you that we "
              + "thoroughly reviewed your application and came to this decision after careful consideration.</p>"
              + "<p>If you have any questions or concerns about this decision, please feel free to contact us. "
              + "We will be happy to provide you with additional information regarding the reasons for the rejection "
              + "and any steps you can take to improve your chances of successful registration in the future.</p>";

      welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());
      Mail mail = new Mail();
      mail.setContent(welcome);
      mail.setFrom("info@tertiaryverify.co.za");
      String[] to = {u.getEmail()};
      mail.setTo(to);
      mail.setSubject("Institution Rejected");
      mail.setCc(to);
      mailSender.saveEmail(mail);
    } catch (Exception e) {
      logger.error("Error", e);
    }
  }

  /**
   * should the date of the Accreditation end date pass without a renewal... the Institution must
   * lose its accreditation. Meaning if accreditation end date is 17 January 2019, by midnight of
   * that end date...it not be accredited. It must not be found on 18 January 2019
   */
  public void updateExpiredInstitution() {
    try {
      List<Institution> list =
          repository.findByDeletedAndAccreditationEndDateLessThan(false, new Date());
      if (list != null && !list.isEmpty()) {
        for (Institution institution : list) {
          institution.setStatusUpdatedByScheduler(true);
          institution.setStatus(ApprovalStatusEnum.EndDateExpired);
          saveInstitution(institution);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error", e);
    }
  }

  public Institution findInstitutionByQRCode(String searchTerm) {
    return repository.findInstitutionByQRCodeAndDeleted(searchTerm, false);
  }

  public Institution findInstitutionById(Long id) {
    return repository.findInstitutionByIdAndDeleted(id, false);
  }

  public List<Institution> findInstitutionByName(String name) {
    return repository.findInstitutionByNameAndDeleted(name, false);
  }

  public List<Institution> searchByInstitutionName(
      String institutionName, ApprovalStatusEnum status) {
    return repository.searchByInstitutionNameAndDeleted(institutionName, status, false);
  }

  public long countByInstitutionName(String institutionName, ApprovalStatusEnum status) {
    return repository.countByInstitutionNameAndDeleted(institutionName, status, false);
  }

  public long countAll() {
    return repository.count();
  }

  public Institution findById(Long id) {
    return repository.findByIdAndDeleted(id, false);
  }

  public long countByInstitutionType(InstitutionType institutionType) {
    return repository.countByInstitutionTypeAndDeleted(institutionType, false);
  }

  public String getUniqueCode() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
      int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char) randomLimitedInt);
    }
    long millis = System.currentTimeMillis();
    return buffer.toString().toUpperCase() + millis;
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> updateAccreditationStartDate(UpdateAccreStartDateRequest request) {

    Date endDate = AppUtility.addYearsToDate(request.getStartDate(), 5);

    if (endDate.after(new Date()) || endDate.equals(new Date())) {
      Institution institution = findInstitutionById(request.getInstitutionId());
      if (institution == null) {
        LOGGER.error("Invalid institution ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid institution ID", null));
      }

      institution.setStatus(ApprovalStatusEnum.PendingApproval);
      institution.setAccreditationEndDate(endDate);
      saveInstitution(institution);

      try {
        User supervisor = Optional.ofNullable(institution.getSupervisor()).orElse(getSupervisor());
        String mssg =
            "<p>I am writing to inform you that one of our registered "
                + "institutions accreditation start date has been updated by a user. "
                + "The institution in question is "
                + institution.getInstitutionName().trim()
                + ".</p>"
                + "<p>As the supervisor of "
                + institution.getInstitutionName().trim()
                + ", "
                + "we kindly request that you take a moment to log in and review "
                + "the changes they made. We greatly value your input and "
                + "appreciate your time in helping us ensure the accuracy of our records. </p>"
                + "<p>Thank you for your attention to this matter.</p>";
        sendEmailNotificationToSupervisor(supervisor, mssg);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      return ResponseEntity.ok()
          .body(new MessageResponse(false, "Accreditation Start Date updated", null));
    } else {
      return ResponseEntity.badRequest()
          .body(
              new MessageResponse(
                  false,
                  "In accordance with the selected date, it is anticipated that the status of the institution will persist as End Date Expired.",
                  null));
    }
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> updateInstitutionStatus(UpdateStatusRequest request) {
    Institution institution = findInstitutionById(request.getInstitutionId());
    if (institution == null) {
      LOGGER.error("Invalid institution ID :{} ", request.getInstitutionId());
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid institution ID", null));
    }

    institution.setStatus(request.getStatus());
    saveInstitution(institution);

    try {
      User supervisor = Optional.ofNullable(institution.getSupervisor()).orElse(getSupervisor());
      String mssg =
          "<p>I hope this message finds you well. "
              + "I wanted to inform you that there has been an update "
              + "regarding the status of the institution. The status has been updated to "
              + request.getStatus().getFriendlyName()
              + "."
              + "The institution in question is "
              + institution.getInstitutionName().trim()
              + ".</p>"
              + "<p>As the supervisor of "
              + institution.getInstitutionName().trim()
              + ", "
              + "We greatly value your input and "
              + "appreciate your time in helping us ensure the accuracy of our records. </p>"
              + "<p>Thank you for your attention to this matter.</p>";
      sendEmailNotificationToSupervisor(supervisor, mssg);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return ResponseEntity.ok().body(new MessageResponse(false, "Institution status updated", null));
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> approveInstitution(ApproveInstitutionRequest request) {
    try {
      Institution institution = findInstitutionById(request.getInstitutionId());
      if (institution == null) {
        LOGGER.error("Invalid institution ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid institution ID", null));
      }

      User user = userRepository.findByIdAndDeleted(request.getUserId(), false);
      if (user == null) {
        LOGGER.error("Invalid user ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid user ID", null));
      }

      institution.setApprovedBy(user);
      institution.setStatus(ApprovalStatusEnum.Approved);

      if (institution.getAccreditationStartDate() == null
          || institution.getAccreditationEndDate() == null) {
        institution.setAccreditationStartDate(new Date());
        Date endDate = AppUtility.addYearsToDate(institution.getAccreditationStartDate(), 5);
        institution.setAccreditationEndDate(endDate);
      }
      sendApprovedInstitutionEmail(
          institution.getCreatorUser(), institution.getInstitutionName().trim());
      saveInstitution(institution);
      return ResponseEntity.ok().body(new MessageResponse(false, "Institution approved", null));

    } catch (Exception e) {
      LOGGER.error("Error", e);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Internal server error", null));
    }
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> rejectInstitution(RejectInstitutionRequest request) {
    try {
      Institution institution = findInstitutionById(request.getInstitutionId());
      if (institution == null) {
        LOGGER.error("Invalid institution ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid institution ID", null));
      }

      User user = userRepository.findByIdAndDeleted(request.getUserId(), false);
      if (user == null) {
        LOGGER.error("Invalid user ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid user ID", null));
      }

      institution.setStatus(ApprovalStatusEnum.Rejected);
      saveInstitution(institution);

      List<RejectReason> rejectReasonList =
          request.getRejectReasonIdList().stream()
              .map(id -> rejectReasonService.findById(id))
              .collect(Collectors.toList());

      if (rejectReasonList.isEmpty()) {
        LOGGER.error("Invalid reject reason IDs :{} ", request.getRejectReasonIdList());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid reject reason IDs", null));
      }

      for (RejectReason rejectReason : rejectReasonList) {
        InstitutionRejectReason institutionRejectReason = new InstitutionRejectReason();
        institutionRejectReason.setInstitution(institution);
        institutionRejectReason.setRejectReason(rejectReason);
        institutionRejectReasonService.saveInstitutionRejectReason(institutionRejectReason);
      }

      sendRejectedInstitutionEmail(
          institution.getCreatorUser(), institution.getInstitutionName().trim(), rejectReasonList);
      return ResponseEntity.ok().body(new MessageResponse(false, "Institution rejected", null));

    } catch (Exception e) {
      LOGGER.error("Error", e);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Internal server error", null));
    }
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> prioritizeInstitution(PrioritizeInstitutionRequest request) {
    try {
      ResponseEntity<MessageResponse> invalidInstitutionUUID =
          prioritiseInst(request.getUuid(), "prioritise", request.getUserId());
      if (invalidInstitutionUUID != null) return invalidInstitutionUUID;

      return ResponseEntity.ok()
          .body(new MessageResponse(true, "Institution prioritisation is success", null));
    } catch (Exception e) {
      LOGGER.error("Error: ", e);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Service unavailable", null));
    }
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> deprioritizeInstitution(DeprioritizeInstitutionRequest request) {
    try {
      ResponseEntity<MessageResponse> invalidInstitutionUUID =
          prioritiseInst(request.getUuid(), "de-prioritise", request.getUserId());
      if (invalidInstitutionUUID != null) return invalidInstitutionUUID;

      return ResponseEntity.ok()
          .body(new MessageResponse(true, "Institution de-prioritisation is success", null));
    } catch (Exception e) {
      LOGGER.error("Error: ", e);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Service unavailable", null));
    }
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> approvePrioritisation(PrioritisationRequest request) {
    try {

      Institution institution = findInstitutionById(request.getInstitutionId());
      if (institution == null) {
        LOGGER.error("Invalid institution ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid institution ID", null));
      }

      User user = userRepository.findByIdAndDeleted(request.getUserId(), false);
      if (user == null) {
        LOGGER.error("Invalid user ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid user ID", null));
      }

      Optional<Task> optionalTask = taskRepository.findById(request.getTaskId());
      if (!optionalTask.isPresent()) {
        LOGGER.error("Invalid task ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid task ID", null));
      }

      institution.setPrioritizedBy(user);
      institution.setPrioritize(request.getPrioritise());
      saveInstitution(institution);
      Task task = optionalTask.get();
      task.setCompleted(true);
      taskRepository.save(task);

      return ResponseEntity.ok()
          .body(new MessageResponse(true, "Institution prioritisation success", null));
    } catch (Exception e) {
      LOGGER.error("Error: ", e);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Service unavailable", null));
    }
  }

  @CacheEvict(value = "institutions", allEntries = true)
  public ResponseEntity<?> rejectPrioritisation(PrioritisationRequest request) {
    try {

      Optional<Task> optionalTask = taskRepository.findById(request.getTaskId());
      if (!optionalTask.isPresent()) {
        LOGGER.error("Invalid task ID :{} ", request.getInstitutionId());
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid task ID", null));
      }

      Task task = optionalTask.get();
      task.setCompleted(true);
      taskRepository.save(task);

      return ResponseEntity.ok()
          .body(new MessageResponse(true, "Institution prioritisation rejected", null));
    } catch (Exception e) {
      LOGGER.error("Error: ", e);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Service unavailable", null));
    }
  }

  @CacheEvict(value = "institutions", allEntries = true)
  private ResponseEntity<MessageResponse> prioritiseInst(String request, String action, Long userId)
      throws Exception {
    Institution institution =
        findAllInstitution().stream()
            .filter(inst -> inst.getUuid().equals(request))
            .findAny()
            .orElse(null);

    if (institution == null) {
      LOGGER.error("Invalid institution UUID :{} ", request);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid institution UUID", null));
    }

    User user = userRepository.findByIdAndDeleted(userId, false);
    if (user == null) {
      LOGGER.error("Invalid user ID :{} ", userId);
      return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid user ID", null));
    }

    if (institution.getSupervisor() == null) {
      institution.setSupervisor(getSupervisor());
      saveInstitution(institution);
    }

    User supervisor = institution.getSupervisor();

    createPrioritisationTask(
        supervisor,
        Institution.class.getName(),
        institution.getId(),
        institution.getUuid(),
        action,
        user);
    return null;
  }

  private void createPrioritisationTask(
      User user, String className, Long classId, String uuid, String action, User createdBy) {

    Task task = new Task();
    task.setDescription("Institution prioritisation");
    task.setUrl(
        "#/pages/institution-prioritisation?action="
            .concat(String.valueOf(action))
            .concat("&uuid=")
            .concat(uuid));
    task.setUser(user);
    task.setCompleted(false);
    task.setImage("institution-reg.png");
    task.setCreateDate(new Date());
    task.setLastUpdateDate(new Date());
    task.setDeleted(false);
    task.setClassId(classId);
    task.setClassName(className);
    task.setObjective("Action the institution prioritisation request");
    task.setDeadline(AppUtility.addDaysToDate(new Date(), 15));
    task.setCreatedByUser(createdBy);
    taskRepository.save(task);
    sendPrioritisationEmailEmail(user);
  }

  public long countByStatus(ApprovalStatusEnum status) {
    return repository.countByStatus(status);
  }

  public Institution findByIdAndDeleted(Long id, boolean b) {
    return repository.findByIdAndDeleted(id, b);
  }
}
