package com.tertiary.verify.controllers;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tertiary_verify")
public class TertiaryVerifyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TertiaryVerifyController.class);
   @Autowired private InstitutionService institutionService;
   @Autowired private TaskService taskService;
   @Autowired private InstitutionCourseService institutionCourseService;
   @Autowired private AddressService addressService;
   @Autowired private InstitutionRejectReasonService institutionRejectReasonService;
   @Autowired private InvestigationService investigationService;
   @Autowired private ReportService reportService;
   @Autowired private TertiaryVerifyService tertiaryVerifyService;
   @Autowired private StatisticService statisticService;

    @GetMapping("/find_accredited_courses")
    public ResponseEntity<?> accreditedCourses() {
        LOGGER.info("Find accredited courses....");
        return tertiaryVerifyService.findAccreditedCourses();
    }

    @GetMapping("/find_all_courses")
    public ResponseEntity<?> allCourses() {
        return tertiaryVerifyService.findAllCourses();
    }

    @GetMapping("/find_accredited_institutions")
    public ResponseEntity<?> findAccreditedInstitutions() {
        return tertiaryVerifyService.findAccreditedInst();
    }

    @GetMapping("/find_all_institutions")
    public ResponseEntity<?> findAllInstitutions() {
        return tertiaryVerifyService.findAllInstitutions();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @GetMapping("/find_all_institution_details")
    public ResponseEntity<?> findAllInstitutionDetails() {
        LOGGER.info("Find all institutions details...");
        return tertiaryVerifyService.findAllInstitutionDetails();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/dashboard_cards_report")
    public ResponseEntity<?> getDashboardCardsReport() {
        LOGGER.info("Getting dashboard cards report...");
        return reportService.dashboardCardsReport();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find_all_mailLogs")
    public ResponseEntity<?> findAllMailLogs() {
        LOGGER.info("Find all mail logs...");
        return reportService.findAllMailLogs();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find_pending_email")
    public ResponseEntity<?> findPendingEmail() {
        LOGGER.info("Find all pending emails...");
        return reportService.findPendingEmail();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find_task_chart/{userId}")
    public ResponseEntity<?> findTaskChart(@PathVariable("userId") Long userId) {
        LOGGER.info("Finding task chart data...");
        return reportService.taskChart(userId);
    }

    // @PreAuthorize("hasRole('USER')")
    @GetMapping("/get_chart_stats_data_for_year/{year}")
    public ResponseEntity<?> getChartStatsDataForYear(@PathVariable("year") int year) {
        return reportService.getChartStatsDataForYear(year);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('INV') or hasRole('SPVS')")
    @GetMapping("/find_pending_approval_institution_details/{id}")
    public ResponseEntity<?> findPendingApprovalInstitutionDetails(@PathVariable("id") Long id) {
        LOGGER.info("Finding all pending approval. User ID ID {}", id);
        return tertiaryVerifyService.findPendingApprovalInstitution(id);
    }

    @PostMapping("/contact_us")
    public ResponseEntity<?> contactUs(@Valid @RequestBody ContactUsRequest request) {
        return tertiaryVerifyService.emitContactUsMessage(request);
    }

    @PostMapping("/report_bogus_course")
    public ResponseEntity<?> reportBogusCourse(@Valid @RequestBody ReportCourseRequest request) {
        return tertiaryVerifyService.emitReportBogusCourseMessage(request);
    }

    @PostMapping("/report_bogus_institution")
    public ResponseEntity<?> reportBogusInstitution(
            @Valid @RequestBody ReportInstitutionRequest request) {
        return tertiaryVerifyService.emitReportBogusInstMessage(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register_institution")
    public ResponseEntity<?> registerInstitution(
            @Valid @RequestBody RegisterInstitutionRequest request) {
        return tertiaryVerifyService.emitRegisterInstMessage(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/update_institution")
    public ResponseEntity<?> updateInstitution(
            @Valid @RequestBody RegisterInstitutionRequest request) {
        return tertiaryVerifyService.updateInstitution(request);
    }

    @PostMapping("/save_statistic")
    public ResponseEntity<?> saveStatistic(@Valid @RequestBody StatisticRequest request) {
        return tertiaryVerifyService.emitStatisticEvent(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/update_accreditation_start_date")
    public ResponseEntity<?> updateAccreditationStartDate(
            @Valid @RequestBody UpdateAccreStartDateRequest request) {
        LOGGER.info("update accreditation start dateRequest: {}", request);
        return institutionService.updateAccreditationStartDate(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/update_institution_status")
    public ResponseEntity<?> updateInstitutionStatus(
            @Valid @RequestBody UpdateStatusRequest request) {
        LOGGER.info("Update institution status request: {}", request);
        return institutionService.updateInstitutionStatus(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/save_institution_course")
    public ResponseEntity<?> saveInstitutionCourse(
            @Valid @RequestBody InstitutionCourseRequest request) {
        LOGGER.info("Institution Course Request: {}", request);
        return institutionService.saveInstitutionCourse(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/confirm_institution_details")
    public ResponseEntity<?> confirmInstitutionDetails(
            @Valid @RequestBody ConfirmInstDetailsRequest request) {
        LOGGER.info("Confirm Institution Details Request: {}", request);
        return institutionService.confirmInstitutionDetails(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/prioritise_institution")
    public ResponseEntity<?> prioritizeInstitution(
            @Valid @RequestBody PrioritizeInstitutionRequest request) {
        LOGGER.info("Prioritise institution Institution. Request: {}", request);
        return institutionService.prioritizeInstitution(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/deprioritise_institution")
    public ResponseEntity<?> deprioritizeInstitution(
            @Valid @RequestBody DeprioritizeInstitutionRequest request) {
        LOGGER.info("Deprioritise institution Institution. Request: {}", request);
        return institutionService.deprioritizeInstitution(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/approve_prioritisation")
    public ResponseEntity<?> approvePrioritisation(
            @Valid @RequestBody PrioritisationRequest request) {
        LOGGER.info("Approve Prioritisation. Request: {}", request);
        return institutionService.approvePrioritisation(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/reject_prioritisation")
    public ResponseEntity<?> rejectPrioritisation(@Valid @RequestBody PrioritisationRequest request) {
        LOGGER.info("Reject Prioritisation. Request: {}", request);
        return institutionService.rejectPrioritisation(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @DeleteMapping("/delete_institution_course/{id}")
    public ResponseEntity<?> deleteInstitutionCourse(@PathVariable("id") Long id) {
        LOGGER.info("Deleting Institution Course, ID: {}", id);
        return institutionCourseService.deleteInstitutionCourse(id);
    }

    @GetMapping("/find_institution_course/{id}")
    public List<CourseBean> findInstitutionCourse(@PathVariable("id") Long id) {
        LOGGER.info("Find Institution Course. Institution ID {}", id);
        return tertiaryVerifyService.findInstitutionCourseList(id);
    }

    @GetMapping("/find_institution_course_details/{id}")
    public List<InstitutionCourseDetails> findInstitutionCourseDetails(@PathVariable("id") Long id) {
        LOGGER.info("Find Institution Course. Institution ID {}", id);
        return tertiaryVerifyService.findInstitutionCourseDetailsList(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @GetMapping("/find_institution_reject_reasons/{id}")
    public ResponseEntity<?> findInstitutionRejectReasons(@PathVariable("id") Long id) {
        LOGGER.info("Find institution reject reasons. ID: {}", id);
        return institutionRejectReasonService.findByInstitutionId(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @GetMapping("/find_open_reported_institution/{userId}")
    public List<ReportedInstitutionDetails> findOpenReportedInstitution(
            @PathVariable("userId") Long id) {
        LOGGER.info("Find Reported Institution By User ID {}", id);
        return tertiaryVerifyService.findByInvestigatorOpenInstitution(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('INV') or hasRole('SPVS')")
    @GetMapping("/find_all_reported_institution")
    public List<ReportedInstitutionDetails> findAllReportedInstitution() {
        LOGGER.info("Find All Reported Institution");
        return tertiaryVerifyService.findAllReportedInstitutionDetails();
    }

    @GetMapping("/find_reported_institution/{refNumber}")
    public ReportedInstitutionDTO findReportedInstitution(
            @PathVariable("refNumber") String refNumber) {
        return tertiaryVerifyService.findReportedInst(refNumber);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('INV') or hasRole('SPVS')")
    @GetMapping("/find_institution_by_id/{id}")
    public InstitutionDetails findInstitutionByID(@PathVariable("id") Long id) {
        LOGGER.info("Find institution by ID. ID: {}", id);
        return tertiaryVerifyService.findInstitutionDetailsById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find_tasks_by_user_id/{userId}")
    public List<TaskResponse> findTasksByUserID(@PathVariable("userId") Long id) {
        return taskService.findUserTasks(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/count_stats_by_title_for_year/{year}")
    public List<StatisticCountResponse> countStatsByTitleForYear(@PathVariable("year") int year) {
        return statisticService.countByTitleForYear(year);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get_monthly_data_for_year/{year}")
    public List<MonthlyStatsResponse> getMonthlyDataForYear(@PathVariable("year") int year) {
        return statisticService.getMonthlyDataForYear(year);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/count_stats_by_title_for_month/{month}")
    public List<StatisticCountResponse> countStatsByTitleForMonth(@PathVariable("month") int month) {
        return statisticService.countByTitleForMonth(month);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/count_stats_by_title_for_day/{date}")
    public List<StatisticCountResponse> countStatsByTitleForDay(@PathVariable("date") String date) {
        return statisticService.countByTitleForDay(date);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get_bar_graph_stats_data_for_month/{month}")
    public ResponseEntity<?> getBarGraphStatsDataForMonth(@PathVariable("month") int month) {
        return reportService.getBarGraphStatsDataForMonth(month);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get_bar_graph_stats_data_for_year/{year}")
    public ResponseEntity<?> getBarGraphStatsDataForYear(@PathVariable("year") int year) {
        return reportService.getBarGraphStatsDataForYear(year);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get_bar_graph_stats_data_for_date/{date}")
    public ResponseEntity<?> getBarGraphStatsSataForDate(@PathVariable("date") String date) {
        return reportService.getBarGraphStatsSataForDate(date);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('INV') or hasRole('SPVS')")
    @GetMapping("/find_institution_by_uuid/{uuid}")
    public InstitutionDetails findInstitutionByID(@PathVariable("uuid") String uuid) {
        return tertiaryVerifyService.findInstitutionDetailsByUUId(uuid);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/save_institution_address")
    public ResponseEntity<?> updateInstitutionAddress(
            @Valid @RequestBody InstitutionAddressRequest request) {
        LOGGER.info("Updating address details, Request: {}", request);
        return addressService.saveAddress(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/approve_institution")
    public ResponseEntity<?> approveInstitution(
            @Valid @RequestBody ApproveInstitutionRequest request) {
        LOGGER.info("Approving institution, Request: {}", request);
        return institutionService.approveInstitution(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @PostMapping("/reject_institution")
    public ResponseEntity<?> rejectInstitution(@Valid @RequestBody RejectInstitutionRequest request) {
        LOGGER.info("Rejecting institution, Request: {}", request);
        return institutionService.rejectInstitution(request);
    }

    @PreAuthorize("hasRole('INV')")
    @PostMapping("/save_investigation")
    public ResponseEntity<?> saveInvestigation(@Valid @RequestBody InvestigationRequest request) {
        LOGGER.info("Save Investigation, Request: {}", request);
        return investigationService.saveInvestigation(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete_institution/{id}")
    public ResponseEntity<?> deleteInstitution(@PathVariable("id") Long id) {
        LOGGER.info("Deleting Institution, ID: {}", id);
        return tertiaryVerifyService.deleteInstitution(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SPVS')")
    @DeleteMapping("/delete_address/{id}")
    public ResponseEntity<?> deleteAddressByID(@PathVariable("id") Long id) {
        LOGGER.info("Deleting address, ID: {}", id);
        return addressService.deleteAddressByID(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update_image")
    public ResponseEntity<MessageResponse> updateImage(
            @Valid @RequestBody UpdatedImageRequest request) {
        return tertiaryVerifyService.updateImage(request);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find_app_config_value/{code}")
    public ResponseEntity<?> findAppConfigValueByCode(@PathVariable("code") String code) {
        return tertiaryVerifyService.findAppConfigValueByCode(code);
    }

    @GetMapping("/get_welcome_data")
    public WelcomeDataResponse getWelcomeData() {
        return tertiaryVerifyService.getWelcomeData();
    }

}
