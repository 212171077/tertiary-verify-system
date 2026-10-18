package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.Role;
import com.tertiary.verify.data_model.model.entities.enums.ERole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
