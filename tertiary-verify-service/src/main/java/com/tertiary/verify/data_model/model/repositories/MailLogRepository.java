package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.MailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailLogRepository extends JpaRepository<MailLog, Integer> {

  MailLog findById(Long parseLong);

  Page<MailLog> findTop100ByOrderByCreateDateDesc(Pageable pageable);
}
