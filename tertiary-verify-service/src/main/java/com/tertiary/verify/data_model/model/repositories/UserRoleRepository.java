package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.Role;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

  List<UserRole> findByUser(User user);

  List<UserRole> findByRole(Role role);

  UserRole findById(long parseLong);

  long countByRole(Role role);
}
