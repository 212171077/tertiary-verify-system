package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean;
import com.tertiary.verify.data_model.model.entities.ReportedInstitutions;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.enums.ReportedInstitutionsStatus;
import com.tertiary.verify.data_model.model.entities.lookup.Province;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedInstitutionsRepository
    extends JpaRepository<ReportedInstitutions, Integer> {

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean(o.institutionName, COUNT(o)) "
          + "FROM ReportedInstitutions o GROUP BY o.institutionName")
  List<ReportedInstitutionBean> getReportedInstitutionBean();

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean(o.institutionName, COUNT(o)) "
          + "FROM ReportedInstitutions o WHERE YEAR(o.createDate) = ?1 GROUP BY o.institutionName")
  List<ReportedInstitutionBean> getReportedInstitutionBean(int year);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean(o.institutionName, COUNT(o)) "
          + "FROM ReportedInstitutions o WHERE YEAR(o.createDate) = ?1 AND MONTH(o.createDate) = ?2 GROUP BY o.institutionName")
  List<ReportedInstitutionBean> getReportedInstitutionBean(int year, int mon);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean(o.institutionName, COUNT(o)) "
          + "FROM ReportedInstitutions o WHERE o.address.province = ?1 AND YEAR(o.createDate) = ?2 AND MONTH(o.createDate) = ?3 GROUP BY o.institutionName")
  List<ReportedInstitutionBean> getReportedInstitutionBean(Province province, int year, int mon);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean(o.institutionName, COUNT(o)) "
          + "FROM ReportedInstitutions o WHERE o.address.province = ?1 AND YEAR(o.createDate) = ?2 GROUP BY o.institutionName")
  List<ReportedInstitutionBean> getReportedInstitutionBean(Province province, int year);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean(o.institutionName, COUNT(o)) "
          + "FROM ReportedInstitutions o WHERE o.address.province = ?1 AND MONTH(o.createDate) = ?2 GROUP BY o.institutionName")
  List<ReportedInstitutionBean> getReportedInstitutionBeanByMonAndProvince(
      Province province, int mon);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.ReportedInstitutionBean(o.institutionName, COUNT(o)) "
          + "FROM ReportedInstitutions o WHERE o.address.province = ?1 GROUP BY o.institutionName")
  List<ReportedInstitutionBean> getReportedInstitutionBean(Province province);

  ReportedInstitutions findByRefNumber(String refNumber);

  ReportedInstitutions findById(Long id);

  List<ReportedInstitutions> findByInvestigatorAndStatusNot(
      User investigator, ReportedInstitutionsStatus status);

  long count();

  long countByStatus(ReportedInstitutionsStatus status);

  long countByInvestigator(User investigator);

  List<ReportedInstitutions> findByDeleted(boolean b);
}
