package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.News;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

  News findByIdAndDeleted(Long parseLong, boolean deleted);

  List<News> findByDeletedAndDescriptionStartingWith(boolean deleted, String description);

  List<News> findByActiveAndDeleted(boolean active, boolean deleted);

  List<News> findByDeleted(boolean deleted);
}
