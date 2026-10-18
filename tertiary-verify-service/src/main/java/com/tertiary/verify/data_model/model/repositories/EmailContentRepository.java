package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.EmailContent;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailContentRepository extends JpaRepository<EmailContent, Integer> {

  EmailContent findById(Long parseLong);

  List<EmailContent> findTop100ByOrderByIdDesc();

  Page<EmailContent> findTop100ByOrderByCreateDateDesc(Pageable pageable);
}
