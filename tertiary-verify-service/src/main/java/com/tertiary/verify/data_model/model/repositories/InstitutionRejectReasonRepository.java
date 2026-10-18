package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.Institution;
import com.tertiary.verify.data_model.model.entities.InstitutionRejectReason;
import com.tertiary.verify.data_model.model.entities.lookup.RejectReason;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRejectReasonRepository
    extends JpaRepository<InstitutionRejectReason, Integer> {

  InstitutionRejectReason findById(Long parseLong);

  List<InstitutionRejectReason> findByInstitution(Institution institution);

  long countByRejectReason(RejectReason rejectReason);
}
