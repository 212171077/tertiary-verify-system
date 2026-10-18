package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.entities.*;
import com.tertiary.verify.data_model.model.entities.Role;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.enums.ERole;
import com.tertiary.verify.data_model.model.entities.enums.ReportTypeEnum;
import com.tertiary.verify.data_model.model.entities.enums.ReportedInstitutionsStatus;
import com.tertiary.verify.data_model.model.entities.lookup.*;
import com.tertiary.verify.data_model.model.repositories.ReportedInstitutionsRepository;
import com.tertiary.verify.data_model.model.repositories.RoleRepository;
import com.tertiary.verify.data_model.model.repositories.UserRoleRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.mail.MailSender;
import com.tertiary.verify.service.lookup.CourseService;
import com.tertiary.verify.service.lookup.ProvinceService;
import com.tertiary.verify.utils.CommonUtil;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ReportedInstitutionsService extends AbstractService {
  @Autowired private ReportedInstitutionsRepository repository;
  @Autowired private ReportedInstitutionCourseService reportedInstitutionCourseService;
  @Autowired private AddressService addressService;
  @Autowired private RoleRepository roleRepository;
  @Autowired private UserRoleRepository userRoleRepository;
  @Autowired private InstitutionService institutionService;
  @Autowired private CourseService courseService;
  @Autowired private MailSender mailSender;
  @Autowired private ProvinceService provinceService;
  Random random = new Random();

  @CacheEvict(value = "reported_institutions", allEntries = true)
  public void saveReportedInstitutions(ReportedInstitutions reportedInstitutions) {
    if (reportedInstitutions.getId() == null) {
      reportedInstitutions.setDeleted(false);
      reportedInstitutions.setCreateDate(new Date());
    } else {
      reportedInstitutions.setLastUpdateDate(new Date());
    }
    repository.save(reportedInstitutions);
  }

  public List<ReportedInstitutionBean> getReportedInstitutionBean(
      Province province, Integer year, Integer mon) {
    if (province != null && year != null && mon != null) {
      return repository.getReportedInstitutionBean(province, year, mon);
    } else if (province != null && year != null) {
      return repository.getReportedInstitutionBean(province, year);
    } else if (province != null && mon != null) {
      return repository.getReportedInstitutionBeanByMonAndProvince(province, mon);
    } else if (year != null && mon != null) {
      return repository.getReportedInstitutionBean(year, mon);
    } else if (year != null) {
      return repository.getReportedInstitutionBean(year);
    } else if (mon != null) {
      return repository.getReportedInstitutionBean(mon);
    } else {
      return repository.getReportedInstitutionBean();
    }
  }

  @CacheEvict(value = "reported_institutions", allEntries = true)
  public void reportBogusCourse(ReportCourseRequest request) throws Exception {
    ReportedInstitutions reportedInstitutions = new ReportedInstitutions();
    reportedInstitutions.setReportType(ReportTypeEnum.BogusCourses);
    if (request.getInstId() != null) {
      reportedInstitutions.setInstitution(institutionService.findById(request.getInstId()));
    } else {
      reportedInstitutions.setInstitutionName(request.getInstName());
      reportedInstitutions.setKnownAs(
          request.getInstKnownAs() == null || request.getInstKnownAs().equalsIgnoreCase("null")
              ? null
              : request.getInstKnownAs());
    }
    reportedInstitutions.setReporterName(request.getUserName());
    reportedInstitutions.setReporterSurname(request.getUserSurname());
    reportedInstitutions.setReporterEmail(request.getUserEmail());
    reportedInstitutions.setReporterCell(request.getUserCellNumber());

    ArrayList<ReportedInstitutionCourse> reportedInstitutionCourseList = new ArrayList<>();
    ReportedInstitutionCourse course = new ReportedInstitutionCourse();
    if (request.getCourseId() != null) {
      course.setCourse(courseService.findById(request.getCourseId()));
    } else {
      course.setCourseType(request.getCourseType());
      course.setCourseLevel(request.getCourseLevel());
      course.setCourseDesc(request.getCourseDesc());
    }
    reportedInstitutionCourseList.add(course);

    submitReportedInstitutions(reportedInstitutions, null, reportedInstitutionCourseList);
  }

  @CacheEvict(value = "reported_institutions", allEntries = true)
  public void reportBogusInstitution(ReportInstitutionRequest request) throws Exception {
    ReportedInstitutions reportedInstitutions = new ReportedInstitutions();
    reportedInstitutions.setReportType(ReportTypeEnum.BogusInstitute);

    reportedInstitutions.setReporterName(request.getUserName());
    reportedInstitutions.setReporterSurname(request.getUserSurname());
    reportedInstitutions.setReporterEmail(request.getUserEmail());
    reportedInstitutions.setReporterCell(request.getUserCellNumber());

    reportedInstitutions.setInstitutionName(request.getInstName());
    reportedInstitutions.setKnownAs(
        request.getInstKnownAs() == null || request.getInstKnownAs().equalsIgnoreCase("null")
            ? ""
            : request.getInstKnownAs());

    reportedInstitutions.setAdditionalInfo(request.getAdditionalInfo());

    Address address = new Address();
    address.setAddressLine1(request.getInstAddressLine1());
    address.setAddressLine2(request.getInstAddressLine2());
    address.setAddressLine3(request.getInstAddressLine3());
    address.setSuburb(request.getInstSuburb());
    address.setCode(request.getInstPostalCode());
    address.setProvince(provinceService.findByName(request.getProvinceName()));

    submitReportedInstitutions(reportedInstitutions, address, null);
  }

  @CacheEvict(value = "reported_institutions", allEntries = true)
  public void submitReportedInstitutions(
      ReportedInstitutions reportedInstitutions,
      Address address,
      ArrayList<ReportedInstitutionCourse> reportedInstitutionCourseList) {
    if (reportedInstitutions.getId() == null) {
      reportedInstitutions.setStatus(ReportedInstitutionsStatus.Submitted);
      reportedInstitutions.setCreateDate(new Date());
      User investigator = getInvestigator();
      reportedInstitutions.setInvestigator(investigator);
      reportedInstitutions.setDeleted(false);
      if (investigator != null) {
        sendEmailNotificationToInvestigator(investigator);
      }
    }
    if (address != null) {
      addressService.saveAddress(address);
      reportedInstitutions.setAddress(address);
    }
    repository.save(reportedInstitutions);
    reportedInstitutions.setRefNumber(getRefNumber() + reportedInstitutions.getId());
    repository.save(reportedInstitutions);
    if (reportedInstitutionCourseList != null && !reportedInstitutionCourseList.isEmpty()) {
      for (ReportedInstitutionCourse c : reportedInstitutionCourseList) {
        c.setReportedInstitution(reportedInstitutions);
        reportedInstitutionCourseService.saveReportedInstitutionCourse(c);
      }
    }

    sendRefeNumberToReport(reportedInstitutions);
  }

  @CacheEvict(value = "reported_institutions", allEntries = true)
  public void deleteReportedInstitutions(ReportedInstitutions reportedInstitutions) {
    repository.delete(reportedInstitutions);
  }

  @CacheEvict(value = "reported_institutions", allEntries = true)
  public void deleteReportedInstitutionsByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("reported_institutions")
  public List<ReportedInstitutions> findAllReportedInstitutions() {
    return repository.findByDeleted(false);
  }

  public Optional<ReportedInstitutions> findReportedInstitutionsById(Integer arg0) {
    return repository.findById(arg0);
  }

  public ReportedInstitutions findByRefNumber(String refNumber) {
    return repository.findByRefNumber(refNumber);
  }

  public List<ReportedInstitutions> findByInvestigatorAndStatusNot(
      User investigator, ReportedInstitutionsStatus status) {
    return repository.findByInvestigatorAndStatusNot(investigator, status);
  }

  public ReportedInstitutions findById(Long id) {
    return repository.findById(id);
  }

  public long countAll() {
    return repository.count();
  }

  public long countByStatus(ReportedInstitutionsStatus status) {
    return repository.countByStatus(status);
  }

  public User getInvestigator() {
    User user = null;
    Optional<Role> investigatoRole = roleRepository.findByName(ERole.ROLE_INV);
    if (investigatoRole.isPresent()) {
      List<UserRole> userRoleList = userRoleRepository.findByRole(investigatoRole.get());
      if (userRoleList != null && !userRoleList.isEmpty()) {
        long count = repository.countByInvestigator(userRoleList.get(0).getUser());
        user = userRoleList.get(0).getUser();
        userRoleList.remove(userRoleList.get(0));
        for (UserRole ur : userRoleList) {
          long tempCount = repository.countByInvestigator(ur.getUser());
          if (tempCount < count) {
            count = tempCount;
            user = ur.getUser();
          }
        }
      }
    }
    return user;
  }

  private void sendEmailNotificationToInvestigator(User u) {

    try {
      String welcome =
          "<p>Dear #NAME#,</p>"
              + "<p>We are writing to inform you that a report of a bogus institution "
              + "has been submitted on our application. As a responsible member of our "
              + "team, We believe it is important to bring this to your attention so that "
              + "appropriate action can be taken.</p>"
              + "<p>We would like to request that you login to the "
              + "website and review the report for more details. </p>"
              + "<p>We encourage you to take immediate action to investigate "
              + "the legitimacy of this institution and take necessary steps "
              + "to ensure that our institution's standards are maintained.</p>"
              + "<p>Thank you for your attention to this matter. "
              + "We look forward to your prompt action on this issue.</p>";
      welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());
      Mail mail = new Mail();
      mail.setContent(welcome);
      mail.setFrom("info@tertiaryverify.co.za");
      String[] to = {u.getEmail()};
      mail.setTo(to);
      mail.setSubject("Reporting a Bogus Institution");
      mail.setCc(to);
      mailSender.saveEmail(mail);
    } catch (Exception e) {
      logger.error("Error", e);
    }
  }

  private void sendRefeNumberToReport(ReportedInstitutions reportedInstitution) {

    try {
      String welcome =
          "<p>Dear #NAME#,</p>"
              + "<p>We are writing to provide you with the reference number for "
              + "the report you recently submitted regarding the institution in "
              + "question. The reference number for your report is "
              + reportedInstitution.getRefNumber()
              + ".</p>"
              + "<p>We appreciate your efforts in reporting this institution and "
              + "bringing it to our attention. Your report will be reviewed and "
              + "investigated to determine its legitimacy, and appropriate action "
              + "will be taken as necessary.</p>"
              + "<p>You can use the reference number provided to check the status "
              + "of your report at any time. Simply use the mobile application and "
              + "click \"Check Report Status\" button,  enter the reference number in the "
              + "search field, and the status of your report will be displayed.</p>"
              + "<p>Please keep this reference number for your records. If you have "
              + "any further information or evidence to support your report, please do not "
              + "hesitate to contact us and provide the reference number for easy "
              + "identification of your report.</p>"
              + "<p>Thank you for your cooperation and support in ensuring the "
              + "standards and legitimacy of institutions in our system.</p>";
      welcome =
          welcome.replaceAll(
              "#NAME#",
              reportedInstitution.getReporterName()
                  + " "
                  + reportedInstitution.getReporterSurname());
      Mail mail = new Mail();
      mail.setContent(welcome);
      mail.setFrom("info@tertiaryverify.co.za");
      String[] to = {reportedInstitution.getReporterEmail()};
      mail.setTo(to);
      mail.setSubject("Reference Number for Reported Institution");
      mail.setCc(to);
      mailSender.saveEmail(mail);
    } catch (Exception e) {
      logger.error("Error", e);
    }
  }

  private String getRefNumber() {
    StringBuilder buffer = new StringBuilder();
    // Generate random alphabetic characters
    for (int i = 0; i < 3; i++) {
      char randomChar = (char) (random.nextInt(26) + 'a');
      buffer.append(randomChar);
    }
    // Append a portion of the current system time
    long currentTime = System.currentTimeMillis();
    buffer.append(currentTime % 100);

    return buffer.toString().toUpperCase();
  }
}
