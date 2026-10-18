package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.Faculty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

  Faculty findByIdAndDeleted(Long parseLong, boolean deleted);

  List<Faculty> findByDescriptionAndDeleted(String desc, boolean deleted);

  List<Faculty> findByDeleted(boolean deleted);
}
