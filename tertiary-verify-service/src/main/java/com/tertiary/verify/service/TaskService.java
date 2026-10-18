package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.TaskResponse;
import com.tertiary.verify.data_model.model.entities.Task;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.lookup.AppConfig;
import com.tertiary.verify.data_model.model.repositories.TaskRepository;
import com.tertiary.verify.data_model.model.repositories.UserRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends AbstractService {
  @Autowired TaskRepository repository;
  @Autowired TertiaryVerifyService tertiaryVerifyService;
  @Autowired private UserRepository userRepository;

  @CacheEvict(value = "tasks", allEntries = true)
  public void saveTask(Task task) {
    if (task.getId() == null) {
      task.setCreateDate(new Date());
    } else {
      task.setLastUpdateDate(new Date());
    }
    repository.save(task);
  }

  @CacheEvict(value = "tasks", allEntries = true)
  public void deleteTask(Task task) {
    task.setDeleted(true);
    repository.save(task);
  }

  @Cacheable("tasks")
  public List<Task> findAllTask() {
    return repository.findAll();
  }

  public Task findById(Long parseLong) {
    return repository.findByIdAndDeletedAndCompleted(parseLong, false, false);
  }

  public List<Task> findByUserAndDeletedAndCompleted(User user) {
    return repository.findByUserAndDeletedAndCompleted(user, false, false);
  }

  public List<Task> findByUserAndDeleted(User user) {
    return repository.findByUserAndDeleted(user, false);
  }

  public List<TaskResponse> findUserTasks(Long id) {
    List<TaskResponse> list = new ArrayList<>();
    List<Task> tasks =
        findByUserAndDeletedAndCompleted(userRepository.findByIdAndDeleted(id, false));
    for (Task task : tasks) {
      TaskResponse response = new TaskResponse();
      BeanUtils.copyProperties(task, response);
      response.setUrl(task.getUrl().concat("&task-id=".concat(String.valueOf(task.getId()))));
      response.setImage(getImage(task));
      list.add(response);
    }
    return list;
  }

  private String getImage(Task task) {
    AppConfig displayDefaultImg =
            tertiaryVerifyService.findAppConfigsByCode("DISPLAY_DEFAULT_IMAGES").get(0);
    if (Boolean.parseBoolean(displayDefaultImg.getValue())) {
      return tertiaryVerifyService.findAppConfigsByCode("DEFAULT_TASK_IMAGE").get(0).getValue();
    } else {
      AppConfig imageUrlAppConfig = tertiaryVerifyService.findAppConfigsByCode("IMAGES_URL").get(0);
      return imageUrlAppConfig
                      .getValue()
                      .concat(
                              task.getImage() == null || task.getImage().isEmpty()
                                      ? "task-done-flat.png"
                                      : task.getImage());
    }
  }
}
