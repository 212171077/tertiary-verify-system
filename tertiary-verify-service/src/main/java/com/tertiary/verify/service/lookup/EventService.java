package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.EventRequest;
import com.tertiary.verify.data_model.model.beans.lookup.EventResponse;
import com.tertiary.verify.data_model.model.entities.lookup.AppConfig;
import com.tertiary.verify.data_model.model.entities.lookup.Event;
import com.tertiary.verify.data_model.model.repositories.lookup.EventRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.TertiaryVerifyService;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EventService extends AbstractService {
  @Autowired TertiaryVerifyService tertiaryVerifyService;
  @Autowired private EventRepository repository;

  @CacheEvict(value = "events", allEntries = true)
  public void saveEvent(Event event) {
    if (event.getId() == null) {
      event.setDeleted(false);
      event.setCreateDate(new Date());
    }
    event.setLastUpdateDate(new Date());
    repository.save(event);
  }

  @CacheEvict(value = "events", allEntries = true)
  public void deleteEvent(Event event) {
    event.setDeleted(true);
    repository.save(event);
  }

  @Cacheable("events")
  public List<Event> findAllEvent() {
    return repository.findByDeleted(false);
  }

  @Cacheable("events")
  public Event findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  public List<Event> findByDescriptionStartingWith(String description) {
    List<Event> list = findAllEvent();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(event -> event.getDescription().startsWith(description))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public List<Event> findActiveEvents() {
    List<Event> list = findAllEvent();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(event -> event.getActive() != null && event.getActive().booleanValue())
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public ResponseEntity<? extends Serializable> saveEvent(EventRequest request) {
    Event event = findById(request.getId());
    if (event == null) {
      event = new Event();
    }
    try {
      event.setCode(request.getCode());
      event.setTitle(request.getTitle());
      event.setSubTitle(request.getSubTitle());
      event.setImageUrl(request.getImageUrl());
      event.setDescription(request.getDescription());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      event.setDateAndTime(sdf.parse(request.getDateAndTime()));
      event.setActive(request.getActive());
      event.setLocation(request.getLocation());
      saveEvent(event);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Event saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteEvent(Long id) {
    Event event = findById(id);
    if (event == null) {
      return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid event ID", null));
    }
    try {
      deleteEvent(event);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Event deleted", null));
  }

  public ResponseEntity<List<EventResponse>> findAllEvents() {
    AppConfig imageUrlAppConfig = tertiaryVerifyService.findAppConfigsByCode("IMAGES_URL").get(0);

    List<Event> list = findAllEvent();
    List<EventResponse> returnList = new ArrayList<>();
    if (!list.isEmpty()) {
      for (Event event : list) {
        EventResponse response = new EventResponse();
        BeanUtils.copyProperties(event, response);
        String image =
            imageUrlAppConfig
                .getValue()
                .concat(
                    (event.getImageUrl() == null ? "event-placeholder.png" : event.getImageUrl()));
        response.setImageUrl(image);
        returnList.add(response);
      }
      returnList.sort(Comparator.comparing(EventResponse::getDescription));
    }
    return ResponseEntity.ok(returnList);
  }
}
