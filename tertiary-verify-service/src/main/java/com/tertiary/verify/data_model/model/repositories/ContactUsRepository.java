package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.ContactUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {

  ContactUs findById(Long parseLong);
}
