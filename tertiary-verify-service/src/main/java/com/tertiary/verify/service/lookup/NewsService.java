package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.NewsRequest;
import com.tertiary.verify.data_model.model.entities.lookup.News;
import com.tertiary.verify.data_model.model.repositories.lookup.NewsRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.ImagesService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NewsService extends AbstractService {
  @Autowired private NewsRepository repository;
  @Autowired private ImagesService imagesService;

  @CacheEvict(value = "news", allEntries = true)
  public void saveNews(News news) {
    if (news.getId() == null) {
      news.setDeleted(false);
      news.setCreateDate(new Date());
    }
    news.setLastUpdateDate(new Date());
    if (news.getImageUrl() == null) {
      news.setActive(false);
    }

    repository.save(news);
  }

  @CacheEvict(value = "news", allEntries = true)
  public void deleteNews(News news) {
    news.setDeleted(true);
    repository.save(news);
  }

  @Cacheable("news")
  public List<News> findAllNews() {
    return repository.findByDeleted(false);
  }

  @Cacheable("news")
  public News findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  public List<News> findByDescriptionStartingWith(String description) {
    List<News> list = findAllNews();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(news -> news.getDescription().startsWith(description))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public List<News> findByActive(boolean active) {
    List<News> list = findAllNews();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(news -> news.getActive() != null && news.getActive().booleanValue() == active)
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public ResponseEntity<? extends Serializable> saveNews(NewsRequest request) {
    News news = findById(request.getId());
    if (news == null) {
      news = new News();
    }
    try {
      news.setCode(request.getCode());
      news.setTitle(request.getTitle());
      news.setSubTitle(request.getSubTitle());
      news.setImageUrl(request.getImageUrl());
      news.setDescription(request.getDescription());
      news.setActive(request.getActive());
      saveNews(news);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "News saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteNews(Long id) {
    News news = findById(id);
    if (news == null) {
      return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid news ID", null));
    }
    try {
      deleteNews(news);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "News deleted", null));
  }

  public ResponseEntity<List<News>> findAll_News() {
    List<News> list = findAllNews();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(News::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
