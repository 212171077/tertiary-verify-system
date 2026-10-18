package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.lookup.Gender;
import com.tertiary.verify.data_model.model.entities.lookup.Title;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  @Query("select c from User c where c.email = ?1 and c.deleted =?2")
  User getUserByEmailAndDeleted(String email, boolean deleted);

  @Query("select c from User c where c.rsaId = ?1 and c.deleted =?2")
  User getUserByRsaIdAndDeleted(String rsaId, boolean deleted);

  User findByEmailAndDeleted(String email, boolean deleted);

  User findByIdAndDeleted(Long id, boolean deleted);

  User findByEmailAndDeletedAndRsaIdNot(String email, boolean deleted, String rsaId);

  long countByTitleAndDeleted(Title title, boolean deleted);

  long countByGenderAndDeleted(Gender gender, boolean deleted);

  List<User> findByDeleted(boolean deleted);

  Boolean existsByEmail(String email);
}
