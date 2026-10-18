package com.tertiary.verify.controllers;

import com.tertiary.verify.data_model.model.beans.lookup.*;
import com.tertiary.verify.service.lookup.*;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('INV') or hasRole('SPVS')")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/tertiary_verify/lookups")
public class LookupsController {
  private static final Logger LOGGER = LoggerFactory.getLogger(LookupsController.class);
  @Autowired private AppConfigService appConfigService;
  @Autowired private CityService cityService;
  @Autowired private CourseLevelService courseLevelService;
  @Autowired private CourseTypeService courseTypeService;
  @Autowired private EventService eventService;
  @Autowired private FacultyService facultyService;
  @Autowired private GenderService genderService;
  @Autowired private InstitutionTypeService institutionTypeService;
  @Autowired private NewsService newsService;
  @Autowired private ProvinceService provinceService;
  @Autowired private RejectReasonService rejectReasonService;
  @Autowired private TitleService titleService;
  @Autowired private CourseService courseService;

  /** =============== APP CONFIG SERVICES =============== */
  @GetMapping("/find_all_app_configs")
  public ResponseEntity<?> findAllAppConfigs() {
    LOGGER.info("Finding all app configs");
    return appConfigService.findAllAppConfigs();
  }

  @PostMapping("/save_app_config")
  public ResponseEntity<?> saveAppConfig(@Valid @RequestBody AppConfigRequest request) {
    LOGGER.info("Saving app configs, Request: {}", request);
    return appConfigService.saveAppConfig(request);
  }

  @DeleteMapping("/delete_app_config/{id}")
  public ResponseEntity<?> deleteAppConfig(@PathVariable("id") Long id) {
    LOGGER.info("Deleting app configs, ID: {}", id);
    return appConfigService.deleteAppConfig(id);
  }

  /** =============== CITY SERVICES =============== */
  @GetMapping("/find_all_cities")
  public ResponseEntity<?> findAllCities() {
    LOGGER.info("Finding all cities");
    return cityService.findAllCities();
  }

  @PostMapping("/save_city")
  public ResponseEntity<?> saveCity(@Valid @RequestBody CityRequest request) {
    LOGGER.info("Saving city, Request: {}", request);
    return cityService.saveCity(request);
  }

  @DeleteMapping("/delete_city/{id}")
  public ResponseEntity<?> deleteCity(@PathVariable("id") Long id) {
    LOGGER.info("Deleting city, ID: {}", id);
    return cityService.deleteCity(id);
  }

  /** =============== COURSE LEVEL SERVICES =============== */
  @GetMapping("/find_all_course_levels")
  public ResponseEntity<?> findAllCourseLevels() {
    LOGGER.info("Finding all course levels");
    return courseLevelService.findAllCourseLevels();
  }

  @PostMapping("/save_course_levels")
  public ResponseEntity<?> saveCourseLevel(@Valid @RequestBody CourseLevelRequest request) {
    LOGGER.info("Saving course levels, Request: {}", request);
    return courseLevelService.saveCourseLevel(request);
  }

  @DeleteMapping("/delete_course_level/{id}")
  public ResponseEntity<?> deleteCourseLevel(@PathVariable("id") Long id) {
    LOGGER.info("Deleting course levels, ID: {}", id);
    return courseLevelService.deleteCourseLevel(id);
  }

  /** =============== COURSE TYPE SERVICES =============== */
  @GetMapping("/find_all_course_types")
  public ResponseEntity<?> findAllCourseTypes() {
    LOGGER.info("Finding all course types");
    return courseTypeService.findAllCourseTypes();
  }

  @PostMapping("/save_course_type")
  public ResponseEntity<?> saveCourseType(@Valid @RequestBody CourseTypeRequest request) {
    LOGGER.info("Saving course types, Request: {}", request);
    return courseTypeService.saveCourseType(request);
  }

  @DeleteMapping("/delete_course_type/{id}")
  public ResponseEntity<?> deleteCourseType(@PathVariable("id") Long id) {
    LOGGER.info("Deleting course types, ID: {}", id);
    return courseTypeService.deleteCourseType(id);
  }

  /** =============== EVENT SERVICES =============== */
  @GetMapping("/find_all_events")
  public ResponseEntity<?> findAllEvents() {
    LOGGER.info("Finding all events");
    return eventService.findAllEvents();
  }

  @PostMapping("/save_event")
  public ResponseEntity<?> saveEvent(@Valid @RequestBody EventRequest request) {
    LOGGER.info("Saving event, Request: {}", request);
    return eventService.saveEvent(request);
  }

  @DeleteMapping("/delete_event/{id}")
  public ResponseEntity<?> deleteEvent(@PathVariable("id") Long id) {
    LOGGER.info("Deleting event, ID: {}", id);
    return eventService.deleteEvent(id);
  }

  /** =============== FACULTY SERVICES =============== */
  @GetMapping("/find_all_faculties")
  public ResponseEntity<?> findAllFaculties() {
    LOGGER.info("Finding all faculties");
    return facultyService.findAllFaculties();
  }

  @PostMapping("/save_faculty")
  public ResponseEntity<?> saveFaculty(@Valid @RequestBody FacultyRequest request) {
    LOGGER.info("Saving faculty, Request: {}", request);
    return facultyService.saveFaculty(request);
  }

  @DeleteMapping("/delete_faculty/{id}")
  public ResponseEntity<?> deleteFaculty(@PathVariable("id") Long id) {
    LOGGER.info("Deleting faculty, ID: {}", id);
    return facultyService.deleteFaculty(id);
  }

  /** =============== GENDER SERVICES =============== */
  @GetMapping("/find_all_genders")
  public ResponseEntity<?> findAllGenders() {
    LOGGER.info("Finding all genders");
    return genderService.findAllGenders();
  }

  @PostMapping("/save_gender")
  public ResponseEntity<?> saveGender(@Valid @RequestBody GenderRequest request) {
    LOGGER.info("Saving gender, Request: {}", request);
    return genderService.saveGender(request);
  }

  @DeleteMapping("/delete_gender/{id}")
  public ResponseEntity<?> deleteGender(@PathVariable("id") Long id) {
    LOGGER.info("Deleting gender, ID: {}", id);
    return genderService.deleteGender(id);
  }

  /** =============== INSTITUTION TYPE SERVICES =============== */
  @GetMapping("/find_all_institution_types")
  public ResponseEntity<?> findAllInstitutionTypes() {
    LOGGER.info("Finding all institution types");
    return institutionTypeService.findAllInstitutionTypes();
  }

  @PostMapping("/save_institution_type")
  public ResponseEntity<?> saveInstitutionType(@Valid @RequestBody InstitutionTypeRequest request) {
    LOGGER.info("Saving institution type, Request: {}", request);
    return institutionTypeService.saveInstitutionType(request);
  }

  @DeleteMapping("/delete_institution_type/{id}")
  public ResponseEntity<?> deleteInstitutionType(@PathVariable("id") Long id) {
    LOGGER.info("Deleting institution type, ID: {}", id);
    return institutionTypeService.deleteInstitutionType(id);
  }

  /** =============== NEWS SERVICES =============== */
  @GetMapping("/find_all_news")
  public ResponseEntity<?> findAllNews() {
    LOGGER.info("Finding all news");
    return newsService.findAll_News();
  }

  @PostMapping("/save_news")
  public ResponseEntity<?> saveNews(@Valid @RequestBody NewsRequest request) {
    LOGGER.info("Saving news, Request: {}", request);
    return newsService.saveNews(request);
  }

  @DeleteMapping("/delete_news/{id}")
  public ResponseEntity<?> deleteNews(@PathVariable("id") Long id) {
    LOGGER.info("Deleting news, ID: {}", id);
    return newsService.deleteNews(id);
  }

  /** =============== PROVINCE SERVICES =============== */
  @GetMapping("/find_all_provinces")
  public ResponseEntity<?> findAllProvince() {
    LOGGER.info("Finding all province");
    return provinceService.findAllProvinces();
  }

  @PostMapping("/save_province")
  public ResponseEntity<?> saveProvince(@Valid @RequestBody ProvinceRequest request) {
    LOGGER.info("Saving province, Request: {}", request);
    return provinceService.saveProvince(request);
  }

  @DeleteMapping("/delete_province/{id}")
  public ResponseEntity<?> deleteProvince(@PathVariable("id") Long id) {
    LOGGER.info("Deleting province, ID: {}", id);
    return provinceService.deleteProvince(id);
  }

  /** =============== REJECT REASON SERVICES =============== */
  @GetMapping("/find_all_reject_reasons")
  public ResponseEntity<?> findAllRejectReasons() {
    LOGGER.info("Finding all reason");
    return rejectReasonService.findAllRejectReasons();
  }

  @PostMapping("/save_reject_reason")
  public ResponseEntity<?> saveRejectReason(@Valid @RequestBody RejectReasonRequest request) {
    LOGGER.info("Saving reason, Request: {}", request);
    return rejectReasonService.saveRejectReason(request);
  }

  @DeleteMapping("/delete_reject_reason/{id}")
  public ResponseEntity<?> deleteRejectReason(@PathVariable("id") Long id) {
    LOGGER.info("Deleting reason, ID: {}", id);
    return rejectReasonService.deleteRejectReason(id);
  }

  /** =============== TITLE SERVICES =============== */
  @GetMapping("/find_all_titles")
  public ResponseEntity<?> findAllTitles() {
    LOGGER.info("Finding all titles");
    return titleService.findAllTitles();
  }

  @PostMapping("/save_title")
  public ResponseEntity<?> saveTitle(@Valid @RequestBody TitleRequest request) {
    LOGGER.info("Saving title, Request: {}", request);
    return titleService.saveTitle(request);
  }

  @DeleteMapping("/delete_title/{id}")
  public ResponseEntity<?> deleteTitle(@PathVariable("id") Long id) {
    LOGGER.info("Deleting title, ID: {}", id);
    return titleService.deleteTitle(id);
  }

  /** =============== COURSE SERVICES =============== */
  @GetMapping("/find_all_courses")
  public ResponseEntity<?> findAllCourses() {
    LOGGER.info("Finding all courses");
    return courseService.findAllCourses();
  }

  @PostMapping("/save_course")
  public ResponseEntity<?> saveCourse(@Valid @RequestBody CourseRequest request) {
    LOGGER.info("Saving course, Request: {}", request);
    return courseService.saveCourse(request);
  }

  @DeleteMapping("/delete_course/{id}")
  public ResponseEntity<?> deleteCourse(@PathVariable("id") Long id) {
    LOGGER.info("Deleting course, ID: {}", id);
    return courseService.deleteCourse(id);
  }
}
