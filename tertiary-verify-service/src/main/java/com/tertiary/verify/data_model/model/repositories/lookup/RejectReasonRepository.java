package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.RejectReason;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectReasonRepository extends JpaRepository<RejectReason, Integer> {

  RejectReason findByIdAndDeleted(Long parseLong, boolean deleted);

  List<RejectReason> findByDeletedAndDescriptionStartingWith(boolean deleted, String description);

  List<RejectReason> findByDescriptionAndDeleted(String desc, boolean deleted);

  List<RejectReason> findByDeleted(boolean deleted);
}
