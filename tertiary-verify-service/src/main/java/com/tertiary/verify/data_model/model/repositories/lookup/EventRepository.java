package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.Event;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

  Event findByIdAndDeleted(Long parseLong, boolean deleted);

  ArrayList<Event> findByActiveAndDeleted(boolean active, boolean deleted);

  ArrayList<Event> findByDeleted(boolean deleted);
}
