package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.beans.AllInstitutionDTO;
import com.tertiary.verify.data_model.model.entities.Institution;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import com.tertiary.verify.data_model.model.entities.lookup.InstitutionType;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Integer> {
  String FIND_BY_INS_NAME =
      "SELECT o FROM Institution o WHERE UPPER(o.institutionName) LIKE CONCAT(?1, '%') AND o.status <> ?2 AND o.deleted = ?3";
  String COUNT_BY_INS_NAME =
      "SELECT COUNT(o) FROM Institution o WHERE (UPPER(o.institutionName) LIKE CONCAT(?1, '%') OR UPPER(o.knownAs) LIKE CONCAT(?1, '%')) AND o.status <> ?2 AND o.deleted = ?3";

  List<Institution> getByDeletedAndInstitutionNameContainingOrderByInstitutionNameDesc(
      boolean deleted, String searchTerm);

  List<Institution> findByAccreditationNumberAndDeleted(String searchTerm, boolean deleted);

  List<Institution> findByDeletedAndAccreditationEndDateLessThan(boolean deleted, Date currentDate);

  @Query("select i from Institution i where i.qrCode = ?1 AND i.deleted = ?2")
  Institution findInstitutionByQRCodeAndDeleted(String searchTerm, boolean deleted);

  @Query("select i from Institution i where i.id = ?1 AND i.deleted = ?2")
  Institution findInstitutionByIdAndDeleted(Long id, boolean deleted);

  @Query("select i from Institution i where i.institutionName = ?1 AND i.deleted = ?2")
  List<Institution> findInstitutionByNameAndDeleted(String name, boolean deleted);

  @Query(FIND_BY_INS_NAME)
  List<Institution> searchByInstitutionNameAndDeleted(
      String institutionName, ApprovalStatusEnum status, boolean deleted);

  @Query(COUNT_BY_INS_NAME)
  long countByInstitutionNameAndDeleted(
      String institutionName, ApprovalStatusEnum status, boolean deleted);

  long countByDeleted(boolean deleted);

  Institution findByIdAndDeleted(Long id, boolean deleted);

  Institution findByUuidAndDeleted(String uuid, boolean deleted);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.AllInstitutionDTO(o.id,o.institutionName) FROM Institution o WHERE o.deleted =?1 GROUP BY o.institutionName")
  List<AllInstitutionDTO> findAllInstitutionDTO(boolean deleted);

  long countBySupervisorAndDeleted(User investigator, boolean deleted);

  long countByInstitutionTypeAndDeleted(InstitutionType institutionType, boolean deleted);

  List<Institution> findByDeleted(boolean deleted);

  List<Institution> findByDeletedAndStatusIn(boolean deleted, List<ApprovalStatusEnum> statuses);

  List<Institution> findBySupervisorAndDeleted(User supervisor, boolean deleted);

  long countByStatus(ApprovalStatusEnum status);
}
