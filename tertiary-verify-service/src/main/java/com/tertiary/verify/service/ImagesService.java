package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.entities.ImageModel;
import com.tertiary.verify.data_model.model.repositories.ImageModelRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ImagesService extends AbstractService {
  @Autowired ImageModelRepository imageModelRepository;

  @CacheEvict(value = "images", allEntries = true)
  public ImageModel save(ImageModel imageModel) {
    if (imageModel.getId() == null) {
      imageModel.setCreateDate(new Date());
    } else {
      imageModel.setLastUpdateDate(new Date());
    }
    return imageModelRepository.save(imageModel);
  }

  @Cacheable("images")
  public ImageModel getById(Long id) {
    return imageModelRepository.getById(id);
  }
}
