package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.Title;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleRepository extends JpaRepository<Title, Integer> {

  Title findByIdAndDeleted(Long parseLong, boolean deleted);

  List<Title> findByDescriptionAndDeleted(String desc, boolean deleted);

  List<Title> findByDeleted(boolean deleted);
}
