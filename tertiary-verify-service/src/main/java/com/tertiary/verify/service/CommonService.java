package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.DropDown;
import com.tertiary.verify.data_model.model.entities.lookup.*;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.lookup.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService extends AbstractService {

  @Autowired private TitleService titleService;
  @Autowired private GenderService genderService;
  @Autowired private CityService cityService;
  @Autowired private ProvinceService provinceService;

  @Autowired private CourseLevelService courseLevelService;

  @Autowired private CourseTypeService courseTypeService;

  @Autowired private InstitutionTypeService institutionTypeService;

  public List<DropDown> buildTitlesDropDown(String selectedOption) {
    List<DropDown> titles = new ArrayList<>();
    for (Title title : titleService.findAllTitle()) {
      titles.add(
          DropDown.builder()
              .id(title.getId())
              .option(title.getDescription())
              .selected(title.getDescription().equals(selectedOption))
              .build());
    }
    titles.sort(Comparator.comparing(DropDown::getOption));
    return titles;
  }

  public List<DropDown> buildGenderDropDown(String selectedOption) {
    List<DropDown> titles = new ArrayList<>();
    for (Gender gender : genderService.findAllGender()) {
      titles.add(
          DropDown.builder()
              .id(gender.getId())
              .option(gender.getGenderName())
              .selected(gender.getGenderName().equals(selectedOption))
              .build());
    }
    titles.sort(Comparator.comparing(DropDown::getOption));
    return titles;
  }

  public List<DropDown> buildCitiesDropDown(String selectedOption) {
    List<DropDown> titles = new ArrayList<>();
    for (City city : cityService.findAllCity()) {
      titles.add(
          DropDown.builder()
              .id(city.getId())
              .option(city.getDescription())
              .selected(city.getDescription().equals(selectedOption))
              .build());
    }
    titles.sort(Comparator.comparing(DropDown::getOption));
    return titles;
  }

  public List<DropDown> buildProvincesDropDown(String selectedOption) {
    List<DropDown> titles = new ArrayList<>();
    for (Province province : provinceService.findAllProvince()) {
      titles.add(
          DropDown.builder()
              .id(province.getId())
              .option(province.getDescription())
              .selected(province.getDescription().equals(selectedOption))
              .build());
    }
    titles.sort(Comparator.comparing(DropDown::getOption));
    return titles;
  }

  public List<DropDown> buildCourseTypeDown(String selectedOption) {
    List<DropDown> dropDowns = new ArrayList<>();
    for (CourseType courseType : courseTypeService.findAllCourseType()) {
      dropDowns.add(
          DropDown.builder()
              .id(courseType.getId())
              .option(courseType.getDescription())
              .selected(courseType.getDescription().equals(selectedOption))
              .build());
    }
    dropDowns.sort(Comparator.comparing(DropDown::getOption));
    return dropDowns;
  }

  public List<DropDown> buildCourseLevelDown(String selectedOption) {
    List<DropDown> dropDowns = new ArrayList<>();
    for (CourseLevel courseLevel : courseLevelService.findAllCourseLevel()) {
      dropDowns.add(
          DropDown.builder()
              .id(courseLevel.getId())
              .option(courseLevel.getDescription())
              .selected(courseLevel.getDescription().equals(selectedOption))
              .build());
    }
    dropDowns.sort(Comparator.comparing(DropDown::getOption));
    return dropDowns;
  }

  public List<DropDown> buildInstitutionTypeDown(String selectedOption) {
    List<DropDown> dropDowns = new ArrayList<>();
    for (InstitutionType courseLevel : institutionTypeService.findAllInstitutionType()) {
      dropDowns.add(
          DropDown.builder()
              .id(courseLevel.getId())
              .option(courseLevel.getDescription())
              .selected(courseLevel.getDescription().equals(selectedOption))
              .build());
    }
    dropDowns.sort(Comparator.comparing(DropDown::getOption));
    return dropDowns;
  }
}
