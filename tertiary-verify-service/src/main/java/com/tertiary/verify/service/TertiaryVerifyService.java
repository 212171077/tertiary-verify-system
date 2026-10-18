package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.constants.AppConstants;
import com.tertiary.verify.data_model.model.entities.InstitutionCourse;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.*;
import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import com.tertiary.verify.data_model.model.entities.enums.ReportedInstitutionsStatus;
import com.tertiary.verify.data_model.model.entities.lookup.*;
import com.tertiary.verify.data_model.model.repositories.ReportedInstitutionsRepository;
import com.tertiary.verify.data_model.model.repositories.TaskRepository;
import com.tertiary.verify.mail.MailSender;
import com.tertiary.verify.service.lookup.AppConfigService;
import com.tertiary.verify.service.lookup.CourseService;
import com.tertiary.verify.utils.AppUtility;
import com.tertiary.verify.utils.CommonUtil;
import com.tertiary.verify.utils.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TertiaryVerifyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TertiaryVerifyService.class);

  @Autowired private InstitutionService institutionService;
  @Autowired private CourseService courseService;
  @Autowired private InstitutionCourseService institutionCourseService;
  @Autowired private AppConfigService appConfigService;
  @Autowired private ReportedInstitutionsService reportedInstitutionsService;
  @Autowired private ReportedInstitutionsRepository reportedInstitutionsRepository;
  @Autowired private ReportedInstitutionCourseService reportedInstitutionCourseService;
  @Autowired private UserService userService;
  @Autowired private InstCourseAddressService instCourseAddressService;
  @Autowired private CommonService commonService;
  @Autowired private TaskRepository taskRepository;
  @Autowired private StatisticService statisticService;
  @Autowired private MailSender mailSender;
  @Autowired private AsyncService asyncService;

    public ResponseEntity<MessageResponse> updateImage(UpdatedImageRequest request) {
        LOGGER.info("Update Image Request: {}", request);
        try {
            if (request.getImageType() == ImageType.USER_PROFILE_IMAGE) {
                return userService.updateImage(request);
            } else if (request.getImageType() == ImageType.INSTITUTION_IMAGE) {
                return institutionService.updateImage(request);
            } else {
                LOGGER.error("Unknown image type. Request: {}", request);
                return ResponseEntity.badRequest()
                        .body(new MessageResponse(false, "Unknown image type", null));
            }
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        }
    }

    public ReportedInstitutionDTO findReportedInst(String refNumber) {
        LOGGER.info("Find reported institution. Ref Number: {}", refNumber);

        ReportedInstitutions reportedInstitution =
                reportedInstitutionsService.findByRefNumber(refNumber);
        try {
            if (reportedInstitution != null) {

                saveReportedInstitutionEmail(reportedInstitution);
                StringBuilder investigation = new StringBuilder();
                if (reportedInstitution.getInvestigationList() != null
                        && !reportedInstitution.getInvestigationList().isEmpty()) {
                    for (Investigation inv : reportedInstitution.getInvestigationList()) {
                        investigation.append(inv.getDescription()).append(", ");
                    }

                } else {
                    investigation =
                            new StringBuilder("No specific feedback or comments provided at this time.");
                }
                return ReportedInstitutionDTO.builder()
                        .success(true)
                        .institutionName(reportedInstitution.getInstitutionName().trim())
                        .reportType(reportedInstitution.getReportType().getFriendlyName())
                        .reportedDate(AppUtility.sdf.format(reportedInstitution.getCreateDate()))
                        .refNumber(refNumber)
                        .investigatorFeedback(investigation.toString())
                        .status(reportedInstitution.getStatus().getFriendlyName())
                        .lastUpdatedDate(
                                (reportedInstitution.getLastUpdateDate() == null
                                        ? "N/A"
                                        : AppUtility.sdf.format(reportedInstitution.getLastUpdateDate())))
                        .build();
            } else {
                LOGGER.info("Invalid reference number: {}", refNumber);
                return ReportedInstitutionDTO.builder()
                        .success(false)
                        .errorMessage("Invalid reference number")
                        .build();
            }
        } catch (Exception e) {
            LOGGER.info("Error: ", e);
            return ReportedInstitutionDTO.builder()
                    .success(false)
                    .errorMessage(ConstantUtil.SERVICE_UNAVAILABLE_MSSG)
                    .build();
        }
    }

    public ResponseEntity<MessageResponse> updateInstitution(RegisterInstitutionRequest request) {
        LOGGER.info("Update Institution Request: {}", request);
        try {
            institutionService.createOrUpdateInstitution(request, ApprovalStatusEnum.UpdateInProgress);
            return ResponseEntity.ok(
                    MessageResponse.builder()
                            .success(true)
                            .message("Institution registration successful")
                            .build());
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        }
    }

    public ResponseEntity<MessageResponse> emitRegisterInstMessage(
            RegisterInstitutionRequest request) {
        LOGGER.info("Register Institution Request: {}", request);
        asyncService.createOrUpdateInstitution(request);
        return ResponseEntity.ok(
                MessageResponse.builder()
                        .success(true)
                        .message("Institution registration successful")
                        .build());
    }

    public ResponseEntity<MessageResponse> emitReportBogusInstMessage(
            ReportInstitutionRequest request) {
        LOGGER.info(
                "Report Institution Request. Institution Name: {}, Institution Known As: {}, Institution Address line 1: {}"
                        + ", Institution Address line 2: {}, Institution Address line 3: {}, Province ID: {}, Postal Code: {}, Additional Info: {}",
                request.getInstName(),
                request.getInstKnownAs(),
                request.getInstAddressLine1(),
                request.getInstAddressLine2(),
                request.getInstAddressLine3(),
                request.getProvinceName(),
                request.getInstPostalCode(),
                request.getAdditionalInfo());
        asyncService.reportBogusInstitution(request);
        return ResponseEntity.ok(
                MessageResponse.builder().success(true).message("Bogus institution reported").build());
    }

    public ResponseEntity<MessageResponse> emitReportBogusCourseMessage(ReportCourseRequest request) {
        LOGGER.info("Report Course Request. Request: {}", request);
        asyncService.reportBogusCourse(request);
        return ResponseEntity.ok(
                MessageResponse.builder().success(true).message("Bogus course reported").build());
    }


    public ResponseEntity<MessageResponse> emitContactUsMessage(ContactUsRequest request) {
        LOGGER.info(
                "Contact Us Request. FullName: {}, Message: {}",
                request.getFullName(),
                request.getMessage());
        asyncService.contactUs(request);
        LOGGER.info("Returning response...");
        return ResponseEntity.ok(
                MessageResponse.builder().success(true).message("Contact us submitted").build());
    }

    public ResponseEntity<?> findAllInstitutions() {
        LOGGER.info("Find all institutions...");
        List<Institution> allInstitution = institutionService.findAllInstitution();
        if (allInstitution != null && !allInstitution.isEmpty()) {
            List<InstitutionDTO> institutionList =
                    allInstitution.stream()
                            .map(
                                    inst ->
                                            InstitutionDTO.builder()
                                                    .id(inst.getId())
                                                    .institutionName(inst.getInstitutionName().trim())
                                                    .contactNumber(inst.getContactNumber())
                                                    .accreditationNumber(inst.getAccreditationNumber())
                                                    .image(getImage(inst))
                                                    .prioritize(inst.isPrioritize())
                                                    .applyLink(inst.getApplyLink() == null ? "" : inst.getApplyLink())
                                                    .status(inst.getStatus().getFriendlyName())
                                                    .statusDescription(inst.getStatus().getDescription())
                                                    .build())
                            .sorted(Comparator.comparing(InstitutionDTO::getInstitutionName))
                            .collect(Collectors.toList());
            return ResponseEntity.ok(institutionList);
        } else {
            return ResponseEntity.ok(
                    MessageResponse.builder().success(false).message("No institutions available").build());
        }
    }

    public ResponseEntity<? extends List<?>> findAccreditedInst() {
        LOGGER.info("Find accredited institutions...");
        List<Institution> allInstitution = findAccreditedInstitutions();
        if (allInstitution != null && !allInstitution.isEmpty()) {

            allInstitution =
                    allInstitution.stream()
                            .filter(
                                    inst ->
                                            inst.getAccreditationStartDate() != null
                                                    && inst.getAccreditationEndDate() != null)
                            .collect(Collectors.toList());

            List<AccreditedInstitution> institutionList =
                    allInstitution.stream()
                            .map(
                                    inst ->
                                            AccreditedInstitution.builder()
                                                    .id(inst.getId())
                                                    .name(inst.getInstitutionName().trim())
                                                    .knownAs(
                                                            inst.getKnownAs() == null
                                                                    || inst.getKnownAs().equalsIgnoreCase("null")
                                                                    ? ""
                                                                    : inst.getKnownAs())
                                                    .image(getImage(inst))
                                                    .addressList(CommonUtil.buildAddress(inst.getAddressList()))
                                                    .accreditedStartDate(
                                                            AppUtility.sdf.format(inst.getAccreditationStartDate()))
                                                    .accreditedEndDate(AppUtility.sdf.format(inst.getAccreditationEndDate()))
                                                    .email((inst.getEmail() == null ? "N/A" : inst.getEmail()))
                                                    .cellNumber(
                                                            inst.getContactNumber()
                                                                    .concat("/".concat(inst.getAlternativeContactNumber())))
                                                    .accreditedNumber(inst.getAccreditationNumber())
                                                    .applyLink(inst.getApplyLink() == null ? "" : inst.getApplyLink())
                                                    .prioritize(inst.isPrioritize())
                                                    .status(inst.getStatus().getFriendlyName())
                                                    .statusDescription(inst.getStatus().getDescription())
                                                    .build())
                            .sorted(Comparator.comparing(AccreditedInstitution::getName))
                            .collect(Collectors.toList());

            return ResponseEntity.ok(institutionList);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    private String getImage(Institution inst) {
        AppConfig displayDefaultImg =
                findAppConfigsByCode("DISPLAY_DEFAULT_IMAGES").get(0);
        if (Boolean.parseBoolean(displayDefaultImg.getValue())) {
            return findAppConfigsByCode("DEFAULT_INST_IMAGE").get(0).getValue();
        } else {
            AppConfig imageUrlAppConfig = findAppConfigsByCode("IMAGES_URL").get(0);
            return imageUrlAppConfig
                    .getValue()
                    .concat(Optional.ofNullable(inst.getImage()).orElse("default_inst.png"));
        }
    }

    public ResponseEntity<?> findAllCourses() {
        LOGGER.info("Find all courses....");
        List<Course> allCourse = courseService.findAllCourse();
        if (allCourse != null && !allCourse.isEmpty()) {
            List<CourseDTO> courseList =
                    allCourse.stream()
                            .map(
                                    course ->
                                            CourseDTO.builder()
                                                    .id(course.getId())
                                                    .description(course.getDescription())
                                                    .courseType(course.getCourseType().getDescription())
                                                    .courseLevel(course.getCourseLevel().getDescription())
                                                    .build())
                            .sorted(Comparator.comparing(CourseDTO::getDescription))
                            .collect(Collectors.toList());
            return ResponseEntity.ok(courseList);
        } else {
            return ResponseEntity.ok(
                    MessageResponse.builder().success(false).message("No courses available").build());
        }
    }

    private static String getStatusMessage(ReportedInstitutions reportedInstitution) {
        String statusMssg = "";
        if (ReportedInstitutionsStatus.Submitted == reportedInstitution.getStatus()) {
            statusMssg =
                    "<p>We would like to inform you that we have received your report regarding the institution in question. "
                            + "Our team is currently reviewing the provided information and conducting a thorough investigation. "
                            + "We appreciate your patience as we work diligently to assess the situation.</p>";
        } else if (ReportedInstitutionsStatus.UnderInvestigation == reportedInstitution.getStatus()) {
            statusMssg =
                    "<p>We want to assure you that our team is actively investigating the reported institution. "
                            + "We are thoroughly examining the details and gathering necessary information to address "
                            + "the issue at hand. Please be assured that we take this matter seriously and are committed "
                            + "to resolving it as quickly as possible.</p>";
        } else if (ReportedInstitutionsStatus.InvestigationPending == reportedInstitution.getStatus()) {
            statusMssg =
                    "<p>We wanted to inform you that our team has received your report regarding the institution. "
                            + "We appreciate your vigilance in bringing this matter to our attention. Our investigators "
                            + "are currently reviewing the details provided, and we assure you that "
                            + "your concerns are being taken seriously.</p>";
        } else if (ReportedInstitutionsStatus.Closed == reportedInstitution.getStatus()) {
            statusMssg =
                    "<p>We want to inform you that the investigation into the reported institution has been completed. "
                            + "Based on the findings and assessment, we have taken appropriate actions to address the reported concerns. "
                            + "The reported institution has been thoroughly reviewed, and necessary measures have been implemented to "
                            + "ensure the integrity and compliance of our institution.</p>";
        }
        return statusMssg;
    }

    public List<AppConfig> findAppConfigsByCode(String code) {
        List<AppConfig> list = appConfigService.findAllAppConfig();
        if (list != null && !list.isEmpty()) {
            return list.stream()
                    .filter(ac -> ac.getCode().equals(code) && (ac.getDeleted() != null && !ac.getDeleted()))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public void saveStatistic(StatisticRequest request) {
        try {
            LOGGER.info("Saving Statistic, Statistic Request: {}", request);
            statisticService.saveStatistic(request);
        } catch (Exception e) {
            LOGGER.error("Unable to Register Institution: ", e);
        }
    }

    public ResponseEntity<?> emitStatisticEvent(StatisticRequest statistic) {
       asyncService.saveStatistic(statistic);
        return ResponseEntity.ok(
                MessageResponse.builder().success(true).message("Stats submitted").build());
    }

    public List<Institution> findAccreditedInstitutions() {
        List<Institution> list = institutionService.findAllInstitution();
        if (!list.isEmpty()) {
            return list.stream()
                    .filter(
                            inst ->
                                    Arrays.asList(ApprovalStatusEnum.ProvisionalApproved, ApprovalStatusEnum.Approved)
                                            .contains(inst.getStatus()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public InstitutionDetails findInstitutionDetailsByUUId(String uuid) {
        LOGGER.info("Find Institution Details By UUID: {}", uuid);
        List<Institution> list = institutionService.findAllInstitution();
        if (!list.isEmpty()) {
            return buildInstitutionDetails(
                    Objects.requireNonNull(
                            list.stream()
                                    .filter(institution -> institution.getUuid().equals(uuid))
                                    .findAny()
                                    .orElse(null)));
        } else {
            return null;
        }
    }

    public InstitutionDetails findInstitutionDetailsById(Long id) {
        Institution institution = institutionService.findByIdAndDeleted(id, false);
        return buildInstitutionDetails(institution);
    }

    private InstitutionDetails buildInstitutionDetails(Institution institution) {
        InstitutionDetails institutionDetails = new InstitutionDetails();
        institutionDetails.setId(institution.getId());
        institutionDetails.setUuid(institution.getUuid());
        institutionDetails.setCreateDate(AppUtility.sdf.format(institution.getCreateDate()));
        institutionDetails.setLastUpdateDate(
                AppUtility.sdf.format(
                        institution.getLastUpdateDate() == null
                                ? institution.getCreateDate()
                                : institution.getLastUpdateDate()));
        institutionDetails.setCreatorUserID(
                Optional.ofNullable(institution.getCreatorUser()).map(User::getId).orElse(null));
        institutionDetails.setSupervisorID(
                Optional.ofNullable(institution.getSupervisor()).map(User::getId).orElse(null));
        institutionDetails.setInstitutionTypeList(
                commonService.buildInstitutionTypeDown(institution.getInstitutionType().getDescription()));
        institutionDetails.setInstitutionName(institution.getInstitutionName().trim());
        institutionDetails.setKnownAs(
                institution.getKnownAs() == null || institution.getKnownAs().equalsIgnoreCase("null")
                        ? ""
                        : institution.getKnownAs());
        institutionDetails.setContactNumber(
                (institution.getContactNumber() != null
                        ? institution.getContactNumber().replaceAll("\\s", "")
                        : ""));
        institutionDetails.setAlternativeContactNumber(
                (institution.getAlternativeContactNumber() != null
                        ? institution.getAlternativeContactNumber().replaceAll("\\s", "")
                        : ""));
        institutionDetails.setEmail(institution.getEmail());
        institutionDetails.setAccreditationStartDate(institution.getAccreditationStartDate());
        institutionDetails.setAccreditationEndDate(institution.getAccreditationEndDate());
        institutionDetails.setAccreditationNumber(institution.getAccreditationNumber());
        institutionDetails.setStatus(institution.getStatus().getFriendlyName());
        institutionDetails.setQrCode(institution.getQrCode());

        institutionDetails.setImage(getImage(institution));
        institutionDetails.setApplyLink(
                institution.getApplyLink() == null ? "" : institution.getApplyLink());
        institutionDetails.setPrioritize(institution.isPrioritize());

        if (institution.getAddressList() != null && !institution.getAddressList().isEmpty()) {
            List<InstitutionAddress> addressList =
                    institution.getAddressList().stream()
                            .map(this::buildInstitutionAddress)
                            .collect(Collectors.toList());
            institutionDetails.setAddressList(addressList);
        } else {
            institutionDetails.setAddressList(new ArrayList<>());
        }

        List<com.tertiary.verify.data_model.model.entities.InstitutionCourse> institutionCourses =
                findByInstitution(institution);
        if (institutionCourses != null && !institutionCourses.isEmpty()) {
            List<com.tertiary.verify.data_model.model.beans.InstitutionCourse> courseList =
                    institutionCourses.stream()
                            .map(this::buildInstitutionCourse)
                            .collect(Collectors.toList());

            institutionDetails.setCourseList(courseList);
        }
        return institutionDetails;
    }

    private com.tertiary.verify.data_model.model.beans.InstitutionCourse buildInstitutionCourse(
            com.tertiary.verify.data_model.model.entities.InstitutionCourse instCourse) {
        com.tertiary.verify.data_model.model.beans.InstitutionCourse institutionCourse =
                new com.tertiary.verify.data_model.model.beans.InstitutionCourse();
        institutionCourse.setId(instCourse.getCourse().getId());
        institutionCourse.setDescription(instCourse.getCourse().getDescription());
        institutionCourse.setCourseTypeList(
                commonService.buildCourseTypeDown(instCourse.getCourse().getCourseType().getDescription()));
        institutionCourse.setCourseLevelList(
                commonService.buildCourseLevelDown(
                        instCourse.getCourse().getCourseLevel().getDescription()));

        instCourse.setInstCourseAddressList(
                instCourseAddressService.findByInstitutionCourse(instCourse));

        if (instCourse.getInstCourseAddressList() != null
                && !instCourse.getInstCourseAddressList().isEmpty()) {
            List<InstitutionAddress> addressList =
                    instCourse.getInstCourseAddressList().stream()
                            .map(instCourseAddress -> buildInstitutionAddress(instCourseAddress.getAddress()))
                            .collect(Collectors.toList());
            institutionCourse.setAddressList(addressList);
        } else {
            institutionCourse.setAddressList(new ArrayList<>());
        }

        return institutionCourse;
    }

    private InstitutionAddress buildInstitutionAddress(Address address) {
        InstitutionAddress institutionAddress = new InstitutionAddress();
        institutionAddress.setId(address.getId());
        institutionAddress.setAddressLine1(address.getAddressLine1());
        institutionAddress.setAddressLine2(address.getAddressLine2());
        institutionAddress.setAddressLine3(address.getAddressLine3());
        institutionAddress.setAddressLine4(address.getAddressLine4());
        institutionAddress.setSuburb(address.getSuburb());
        institutionAddress.setCode(address.getCode());
        institutionAddress.setLatitude(address.getLatitude());
        institutionAddress.setLongitude(address.getLongitude());
        institutionAddress.setCreateDate(address.getCreateDate());
        institutionAddress.setLastUpdateDate(address.getLastUpdateDate());
        institutionAddress.setExaminationNumber(address.getExaminationNumber());
        institutionAddress.setProvinceList(
                commonService.buildProvincesDropDown(
                        Optional.ofNullable(address.getProvince()).map(Province::getDescription).orElse("")));
        institutionAddress.setCityList(
                commonService.buildCitiesDropDown(
                        Optional.ofNullable(address.getCity()).map(City::getDescription).orElse("")));
        return institutionAddress;
    }

    public List<ReportedInstitutionDetails> findAllReportedInstitutionDetails() {
        List<ReportedInstitutions> institutionsList =
                reportedInstitutionsService.findAllReportedInstitutions();
        if (!institutionsList.isEmpty()) {
            return institutionsList.stream()
                    .map(this::buildReportedInstitutionDetails)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<ReportedInstitutionDetails> findByInvestigatorOpenInstitution(Long id) {
        List<ReportedInstitutions> institutionsList =
                reportedInstitutionsRepository.findByInvestigatorAndStatusNot(
                        userService.findByIdAndDeleted(id, false), ReportedInstitutionsStatus.Closed);
        List<ReportedInstitutionDetails> responseList = new ArrayList<>();
        if (institutionsList != null) {
            responseList =
                    institutionsList.stream()
                            .map(this::buildReportedInstitutionDetails)
                            .collect(Collectors.toList());
        }
        return responseList;
    }

    private ReportedInstitutionDetails buildReportedInstitutionDetails(ReportedInstitutions inst) {
        ReportedInstitutionDetails institutionDetails = new ReportedInstitutionDetails();
        institutionDetails.setId(inst.getId());
        institutionDetails.setCreateDate(inst.getCreateDate());
        institutionDetails.setLastUpdateDate(inst.getLastUpdateDate());
        institutionDetails.setReportType(inst.getReportType().getFriendlyName());

        institutionDetails.setInstitutionName(
                (inst.getInstitution() != null)
                        ? inst.getInstitution().getInstitutionName().trim()
                        : inst.getInstitutionName().trim());

        institutionDetails.setKnownAs(
                Optional.ofNullable(inst.getInstitution())
                        .map(Institution::getKnownAs)
                        .orElse(
                                inst.getKnownAs() == null || inst.getKnownAs().equalsIgnoreCase("null")
                                        ? ""
                                        : inst.getKnownAs()));

        institutionDetails.setAdditionalInfo(inst.getAdditionalInfo());
        institutionDetails.setRefNumber(inst.getRefNumber());
        institutionDetails.setStatus(inst.getStatus().getFriendlyName());
        institutionDetails.setInvestigatorName(
                Optional.ofNullable(inst.getInvestigator()).map(User::getName).orElse("N/A"));
        institutionDetails.setInvestigatorSurname(
                Optional.ofNullable(inst.getInvestigator()).map(User::getSurname).orElse("N/A"));
        institutionDetails.setInvestigatorEmail(
                Optional.ofNullable(inst.getInvestigator()).map(User::getEmail).orElse("N/A"));
        institutionDetails.setInvestigatorCell(
                Optional.ofNullable(inst.getInvestigator()).map(User::getCellNumber).orElse("N/A"));
        institutionDetails.setReporterName(inst.getReporterName());
        institutionDetails.setReporterSurname(inst.getReporterSurname());
        institutionDetails.setReporterEmail(inst.getReporterEmail());
        institutionDetails.setReporterCell(inst.getReporterCell());
        institutionDetails.setAddressLine1(
                Optional.ofNullable(inst.getAddress()).map(Address::getAddressLine1).orElse("N/A"));
        institutionDetails.setAddressLine2(
                Optional.ofNullable(inst.getAddress()).map(Address::getAddressLine2).orElse("N/A"));
        institutionDetails.setAddressLine3(
                Optional.ofNullable(inst.getAddress()).map(Address::getAddressLine3).orElse("N/A"));
        institutionDetails.setAddressLine4(
                Optional.ofNullable(inst.getAddress()).map(Address::getAddressLine4).orElse("N/A"));
        institutionDetails.setCode(
                Optional.ofNullable(inst.getAddress()).map(Address::getCode).orElse("N/A"));
        institutionDetails.setProvince(
                Optional.ofNullable(inst.getAddress())
                        .map(Address::getProvince)
                        .map(Province::getDescription)
                        .orElse("N/A"));
        institutionDetails.setCity(
                Optional.ofNullable(inst.getAddress())
                        .map(Address::getCity)
                        .map(City::getDescription)
                        .orElse("N/A"));
        institutionDetails.setInvestigationList(buildInvestigationList(inst.getInvestigationList()));

        List<ReportedInstitutionCourse> institutionCourses =
                reportedInstitutionCourseService.findByReportedInstitution(inst);
        List<CourseBean> reportedCourseList = new ArrayList<>();
        if (institutionCourses != null) {
            for (ReportedInstitutionCourse course : institutionCourses) {
                CourseBean courseBean = new CourseBean();
                courseBean.setId(course.getId());
                courseBean.setDescription(
                        Optional.ofNullable(course.getCourse())
                                .map(Course::getDescription)
                                .orElse(course.getCourseDesc()));
                courseBean.setCourseType(
                        Optional.ofNullable(course.getCourse())
                                .map(Course::getCourseType)
                                .map(CourseType::getDescription)
                                .orElse(course.getCourseType()));
                courseBean.setCourseLevel(
                        Optional.ofNullable(course.getCourse())
                                .map(Course::getCourseLevel)
                                .map(CourseLevel::getDescription)
                                .orElse(course.getCourseLevel()));
                reportedCourseList.add(courseBean);
            }
        }
        institutionDetails.setReportedCourseList(reportedCourseList);
        return institutionDetails;
    }

    public ResponseEntity<?> findAllInstitutionDetails() {
        List<Institution> allInstitution = institutionService.findAllInstitution();
        if (allInstitution != null && !allInstitution.isEmpty()) {
            List<InstitutionDetailsResponse> institutionList =
                    getInstitutionDetailsResponses(allInstitution);
            return ResponseEntity.ok(institutionList);
        } else {
            return ResponseEntity.ok(
                    MessageResponse.builder().success(false).message("No institutions available").build());
        }
    }

    private List<InvestigationResponse> buildInvestigationList(
            List<Investigation> investigationList) {
        List<InvestigationResponse> list = new ArrayList<>();
        if (investigationList != null && !investigationList.isEmpty()) {
            list =
                    investigationList.stream()
                            .map(
                                    inv ->
                                            InvestigationResponse.builder()
                                                    .feedbackDetails(inv.getDescription())
                                                    .createDate(AppUtility.sdf.format(inv.getCreateDate()))
                                                    .build())
                            .collect(Collectors.toList());
        }
        return list;
    }

    public ResponseEntity<?> findPendingApprovalInstitution(Long userId) {

        if (userId == null) {
            LOGGER.error("Invalid user ID, User ID is null ");
            return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid user ID", null));
        }

        List<Institution> allInstitutions = institutionService.findAllInstitution();

        List<Institution> list = null;

        if (!allInstitutions.isEmpty()) {
            list =
                    allInstitutions.stream()
                            .filter(
                                    inst ->
                                            (inst.getDeleted() == null || !inst.getDeleted())
                                                    && inst.getSupervisor() != null
                                                    && userId.equals(inst.getSupervisor().getId()))
                            .collect(Collectors.toList());
        }

        List<Institution> pendingInstitutionsList = null;

        if (list != null && !list.isEmpty()) {
            pendingInstitutionsList =
                    list.stream()
                            .filter(institution -> institution.getStatus() == ApprovalStatusEnum.PendingApproval)
                            .collect(Collectors.toList());
        }

        if (pendingInstitutionsList != null && !pendingInstitutionsList.isEmpty()) {
            List<InstitutionDetailsResponse> institutionList =
                    getInstitutionDetailsResponses(pendingInstitutionsList);
            return ResponseEntity.ok(institutionList);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    private List<InstitutionDetailsResponse> getInstitutionDetailsResponses(
            List<Institution> allInstitution) {
        return allInstitution.stream()
                .map(
                        inst ->
                                InstitutionDetailsResponse.builder()
                                        .id(inst.getId())
                                        .createDate(AppUtility.sdf.format(inst.getCreateDate()))
                                        .lastUpdateDate(
                                                inst.getLastUpdateDate() == null
                                                        ? AppUtility.sdf.format(inst.getCreateDate())
                                                        : AppUtility.sdf.format(inst.getLastUpdateDate()))
                                        .creatorUserID(inst.getCreatorUser().getId())
                                        .supervisorID(
                                                Optional.ofNullable(inst.getSupervisor()).map(User::getId).orElse(null))
                                        .institutionName(inst.getInstitutionName().trim())
                                        .knownAs(
                                                inst.getKnownAs() == null || inst.getKnownAs().equalsIgnoreCase("")
                                                        ? ""
                                                        : null)
                                        .contactNumber(inst.getContactNumber())
                                        .alternativeContactNumber(inst.getAlternativeContactNumber())
                                        .email(inst.getEmail())
                                        .accreditationStartDate(
                                                inst.getAccreditationStartDate() != null
                                                        ? AppUtility.sdf.format(inst.getAccreditationStartDate())
                                                        : "N/A")
                                        .accreditationEndDate(
                                                inst.getAccreditationEndDate() != null
                                                        ? AppUtility.sdf.format(inst.getAccreditationEndDate())
                                                        : "N/A")
                                        .accreditationNumber(inst.getAccreditationNumber())
                                        .image(getImage(inst))
                                        .qrCode(inst.getQrCode())
                                        .type(inst.getInstitutionType().getDescription())
                                        .status(inst.getStatus().getFriendlyName())
                                        .uuid(inst.getUuid())
                                        .prioritize(inst.isPrioritize())
                                        .applyLink(inst.getApplyLink() == null ? "" : inst.getApplyLink())
                                        .build())
                .sorted(Comparator.comparing(InstitutionDetailsResponse::getInstitutionName))
                .collect(Collectors.toList());
    }

    public ResponseEntity<MessageResponse> deleteInstitution(Long instId) {
        try {
            Institution institution = institutionService.findInstitutionById(instId);

            institutionService.deleteInstitution(institution);

            List<Task> taskList =
                    taskRepository.findByClassIdAndClassName(
                            institution.getId(), Institution.class.getName());

            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(
                        task -> {
                            task.setCompleted(true);
                            taskRepository.save(task);
                        });
            }

            return ResponseEntity.ok().body(new MessageResponse(true, "Institution deleted", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(false, "Invalid institution ID", null));
        }
    }

    public ResponseEntity<? extends List<?>> findAccreditedCourses() {
        long startTime = System.currentTimeMillis();
        try {
            List<InstitutionCourse> institutionCourseList =
                    filterInstitutions(institutionCourseService.findAllInstitutionCourse());

            List<Course> courseList = courseService.findAllCourse();

            if (courseList != null && !courseList.isEmpty()) {

                List<AccreditedCourse> accreditedCourseList = new ArrayList<>();
                for (Course course : courseList) {
                    try {
                        List<AccreditedInstitution> institutions =
                                buildAccreditedInstitution(course, institutionCourseList);
                        if (!institutions.isEmpty()) {
                            AccreditedCourse build =
                                    AccreditedCourse.builder()
                                            .id(course.getId())
                                            .name(course.getDescription())
                                            .type(course.getCourseType().getDescription())
                                            .level(course.getCourseLevel().getDescription())
                                            .institutionList(institutions)
                                            .build();
                            accreditedCourseList.add(build);
                        }

                    } catch (Exception e) {
                        LOGGER.error("Failed to build course [{}]", course, e);
                    }
                }
                accreditedCourseList.sort(Comparator.comparing(AccreditedCourse::getName));

                return ResponseEntity.ok(
                        accreditedCourseList.stream()
                                .filter(ac -> !ac.getInstitutionList().isEmpty())
                                .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(new ArrayList<>());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to build Accredited Courses ", e);
            return ResponseEntity.ok(new ArrayList<>());
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LOGGER.info("findAccreditedCourses took " + duration + " milliseconds to execute.");
        }
    }

    private List<AccreditedInstitution> buildAccreditedInstitution(
            Course course, List<InstitutionCourse> institutionCourseList) {

        List<AccreditedInstitution> institutionList = new ArrayList<>();
        if (institutionCourseList != null && !institutionCourseList.isEmpty()) {
            for (InstitutionCourse instCourse : institutionCourseList) {
                try {
                    if (instCourse != null
                            && instCourse.getCourse() != null
                            && instCourse.getCourse().getId().equals(course.getId())) {
                        institutionList.add(buildAccreditedInstitution(instCourse));
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to build Accredited Institution [{}]", instCourse, e);
                }
            }
        }
        return institutionList;
    }

    private List<InstitutionCourse> filterInstitutions(
            List<InstitutionCourse> institutionCourseList) {
        if (institutionCourseList != null && !institutionCourseList.isEmpty()) {
            List<ApprovalStatusEnum> axceptedStatusList =
                    Arrays.asList(ApprovalStatusEnum.Approved, ApprovalStatusEnum.ProvisionalApproved);
            return institutionCourseList.stream()
                    .filter(
                            inst ->
                                    inst.getInstitution().getAccreditationStartDate() != null
                                            && inst.getInstitution().getAccreditationEndDate() != null
                                            && axceptedStatusList.contains(inst.getInstitution().getStatus()))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private AccreditedInstitution buildAccreditedInstitution(InstitutionCourse instCourse) {
        String knownAs = instCourse.getInstitution().getKnownAs();
        String formattedKnownAs = (knownAs == null || knownAs.equalsIgnoreCase("null")) ? "" : knownAs;

        String email = Optional.ofNullable(instCourse.getInstitution().getEmail()).orElse("N/A");
        String accreditedEndDate =
                AppUtility.sdf.format(instCourse.getInstitution().getAccreditationEndDate());
        String accreditedStartDate =
                AppUtility.sdf.format(instCourse.getInstitution().getAccreditationStartDate());
        List<String> addressList =
                CommonUtil.buildAddress(instCourse.getInstitution().getAddressList());
        String cellNumber =
                instCourse
                        .getInstitution()
                        .getContactNumber()
                        .concat("/".concat(instCourse.getInstitution().getAlternativeContactNumber()));
        String image = getImage(instCourse.getInstitution());
        String link =
                instCourse.getInstitution().getApplyLink() == null
                        ? ""
                        : instCourse.getInstitution().getApplyLink();

        return AccreditedInstitution.builder()
                .id(instCourse.getInstitution().getId())
                .name(instCourse.getInstitution().getInstitutionName().trim())
                .knownAs(formattedKnownAs)
                .email(email)
                .accreditedEndDate(accreditedEndDate)
                .accreditedStartDate(accreditedStartDate)
                .addressList(addressList)
                .cellNumber(cellNumber)
                .accreditedNumber(instCourse.getInstitution().getAccreditationNumber())
                .image(image)
                .prioritize(instCourse.getInstitution().isPrioritize())
                .applyLink(link)
                .status(instCourse.getInstitution().getStatus().getFriendlyName())
                .statusDescription(instCourse.getInstitution().getStatus().getDescription())
                .build();
    }

    public List<InstitutionCourse> findByInstitution(Institution institution) {
        List<InstitutionCourse> courseList = institutionCourseService.findAllInstitutionCourse();
        if (courseList != null && !courseList.isEmpty()) {
            return courseList.stream()
                    .filter(ic -> ic.getInstitution().getId().equals(institution.getId()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public InstitutionCourse findByInstitutionAndCourse(Institution institution, Course course)
            throws NonUniqueResultException {
        List<InstitutionCourse> courseList = institutionCourseService.findAllInstitutionCourse();
        if (courseList != null && !courseList.isEmpty()) {
            List<InstitutionCourse> list =
                    courseList.stream()
                            .filter(
                                    ic ->
                                            ic.getInstitution().getId().equals(institution.getId())
                                                    && ic.getCourse().getId().equals(course.getId()))
                            .collect(Collectors.toList());
            if (list.size() > 1) {
                throw new NonUniqueResultException("Non UniqueResult");
            } else if (!list.isEmpty()) {
                return list.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public long countAccreditedCourse() {
        long total = 0;
        List<InstitutionCourse> courseList = institutionCourseService.findAllInstitutionCourse();
        if (courseList != null && !courseList.isEmpty()) {
            total =
                    courseList.stream()
                            .filter(ic -> ic.getInstitution().getStatus() == ApprovalStatusEnum.Approved)
                            .count();
        }
        return total;
    }

    private List<InstitutionCourse> findInstCourseByInstId(Long id) {
        List<InstitutionCourse> courseList = institutionCourseService.findAllInstitutionCourse();
        if (courseList != null && !courseList.isEmpty()) {
            return courseList.stream()
                    .filter(ic -> ic.getInstitution().getId().equals(id))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<CourseBean> findInstitutionCourseList(Long id) {
        List<CourseBean> list = new ArrayList<>();

        List<InstitutionCourse> institutionCourses = findInstCourseByInstId(id);

        institutionCourses =
                institutionCourses.stream()
                        .filter(instCourse -> instCourse.getCourse() != null)
                        .collect(Collectors.toList());

        if (!institutionCourses.isEmpty()) {
            list =
                    institutionCourses.stream()
                            .map(
                                    instCourse ->
                                            CourseBean.builder()
                                                    .id(instCourse.getCourse().getId())
                                                    .description(instCourse.getCourse().getDescription())
                                                    .courseLevel(instCourse.getCourse().getCourseLevel().getDescription())
                                                    .courseType(instCourse.getCourse().getCourseType().getDescription())
                                                    .build())
                            .collect(Collectors.toList());
        }
        list.sort(Comparator.comparing(CourseBean::getDescription));
        return list;
    }

    public List<InstitutionCourseDetails> findInstitutionCourseDetailsList(Long id) {
        List<InstitutionCourseDetails> list = new ArrayList<>();

        List<InstitutionCourse> institutionCourses = findInstCourseByInstId(id);

        institutionCourses =
                institutionCourses.stream()
                        .filter(instCourse -> instCourse.getCourse() != null)
                        .collect(Collectors.toList());
        if (!institutionCourses.isEmpty()) {
            list =
                    institutionCourses.stream()
                            .map(
                                    instCourse ->
                                            InstitutionCourseDetails.builder()
                                                    .id(instCourse.getId())
                                                    .description(instCourse.getCourse().getDescription())
                                                    .courseLevel(instCourse.getCourse().getCourseLevel().getDescription())
                                                    .courseType(instCourse.getCourse().getCourseType().getDescription())
                                                    .addressList(buildAddressList(instCourse.getInstCourseAddressList()))
                                                    .build())
                            .collect(Collectors.toList());
        }
        list.sort(Comparator.comparing(InstitutionCourseDetails::getDescription));
        return list;
    }

    private List<InstitutionCourseAddress> buildAddressList(
            List<InstCourseAddress> instCourseAddressList) {
        List<InstitutionCourseAddress> list = new ArrayList<>();
        if (instCourseAddressList != null && !instCourseAddressList.isEmpty()) {
            list =
                    instCourseAddressList.stream()
                            .map(
                                    instCourseAddress ->
                                            InstitutionCourseAddress.builder()
                                                    .addressLine1(instCourseAddress.getAddress().getAddressLine1())
                                                    .addressLine2(instCourseAddress.getAddress().getAddressLine2())
                                                    .addressLine3(instCourseAddress.getAddress().getAddressLine3())
                                                    .addressLine4(instCourseAddress.getAddress().getAddressLine4())
                                                    .code(instCourseAddress.getAddress().getCode())
                                                    .examinationNumber(instCourseAddress.getAddress().getExaminationNumber())
                                                    .province(
                                                            Optional.ofNullable(instCourseAddress.getAddress().getProvince())
                                                                    .map(Province::getDescription)
                                                                    .orElse(""))
                                                    .city(
                                                            Optional.ofNullable(instCourseAddress.getAddress().getCity())
                                                                    .map(City::getDescription)
                                                                    .orElse(""))
                                                    .build())
                            .collect(Collectors.toList());
        }

        return list;
    }

    private void saveReportedInstitutionEmail(ReportedInstitutions reportedInstitution) {
        StringBuilder investigation = new StringBuilder();
        if (reportedInstitution.getInvestigationList() != null
                && !reportedInstitution.getInvestigationList().isEmpty()) {
            for (Investigation inv : reportedInstitution.getInvestigationList()) {
                investigation.append("<li>").append(inv.getDescription()).append("</li>");
            }

        } else {
            investigation =
                    new StringBuilder("<li>No specific feedback or comments provided at this time.</li>");
        }
        String message =
                "<p>Dear #NAME#,</p>"
                        + "<p>We hope this email finds you well. We would like to "
                        + "provide you with an update on the status of the reported "
                        + "institution. Here are the details:</p>"
                        + "<ul>"
                        + "<li><strong>Status: </strong>"
                        + reportedInstitution.getStatus().getFriendlyName()
                        + "</li>"
                        + "<li><strong>Type of Report: </strong>"
                        + reportedInstitution.getReportType().getFriendlyName()
                        + "</li>"
                        + "<li><strong>Reference Number: </strong>"
                        + reportedInstitution.getRefNumber()
                        + "</li>"
                        + "<li><strong>Reported Date: </strong>"
                        + AppUtility.sdf.format(reportedInstitution.getCreateDate())
                        + "</li>"
                        + "<li><strong>Last Updated Date: </strong>"
                        + (reportedInstitution.getLastUpdateDate() != null
                        ? AppUtility.sdf.format(reportedInstitution.getLastUpdateDate())
                        : AppUtility.sdf.format(reportedInstitution.getCreateDate()))
                        + "</li>"
                        + "<li><strong>Investigator Feedback: </strong></li>"
                        + investigation
                        + "</ul>"
                        + getStatusMessage(reportedInstitution)
                        + "<p>Thank you for bringing this matter to our attention, "
                        + "and we value your contribution in ensuring the integrity "
                        + "of our institution.</p>";
        message =
                message.replaceAll(
                        "#NAME#",
                        reportedInstitution.getReporterName() + " " + reportedInstitution.getReporterSurname());

        Mail mail = new Mail();

        mail.setContent(message);
        mail.setFrom(AppConstants.FROM_EMAIL);
        String[] to = {reportedInstitution.getReporterEmail()};
        mail.setTo(to);
        mail.setSubject("Status of Reported Institution");
        mail.setCc(to);
        mailSender.saveEmail(mail);
    }

    public ResponseEntity<?> findAppConfigValueByCode(String code) {
        List<AppConfig> appConfigsByCode = findAppConfigsByCode(code);
        if (appConfigsByCode != null && !appConfigsByCode.isEmpty()) {
            return ResponseEntity.ok()
                    .body(new MessageResponse(true, "Success", appConfigsByCode.get(0).getValue()));
        }
        return ResponseEntity.ok().body(new MessageResponse(false, "App config not found", null));
    }

    public WelcomeDataResponse getWelcomeData() {
        List<AppConfig> appConfigsByCode = findAppConfigsByCode("DATA_PRIVACY_URL");
        if (appConfigsByCode != null && !appConfigsByCode.isEmpty()) {
            return WelcomeDataResponse.builder().success(true).dataPrivacyUrl(appConfigsByCode.get(0).getValue()).build();
        }
        return WelcomeDataResponse.builder().success(false).errorMessage("Welcome data not found").build();
    }
}
