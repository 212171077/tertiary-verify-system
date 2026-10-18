package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.Task;
import com.tertiary.verify.data_model.model.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByUserAndDeletedAndCompleted(User user, boolean deleted, boolean completed);

  Task findByIdAndDeletedAndCompleted(Long id, boolean deleted, boolean completed);

  List<Task> findByUserAndDeleted(User user, boolean deleted);

  Task findByUserAndClassIdAndClassName(User user, Long classId, String className);

  List<Task> findByClassIdAndClassName(Long classId, String className);
}
