package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.UserBrowserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBrowserInfoRepository extends JpaRepository<UserBrowserInfo, Integer> {

  UserBrowserInfo findById(Long parseLong);
}
