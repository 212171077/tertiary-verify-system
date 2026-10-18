package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.MonthlyStatsResponse;
import com.tertiary.verify.data_model.model.beans.StatisticCountResponse;
import com.tertiary.verify.data_model.model.beans.StatisticRequest;
import com.tertiary.verify.data_model.model.entities.Statistic;
import com.tertiary.verify.data_model.model.repositories.StatisticRepository;
import com.tertiary.verify.framework.AbstractService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class StatisticService extends AbstractService {
  @Autowired private StatisticRepository repository;

  @CacheEvict(value = "statistics", allEntries = true)
  public void saveStatistic(Statistic statistic) {
    if (statistic.getId() == null) {
      statistic.setCreateDate(new Date());
    }
    repository.save(statistic);
  }

  @CacheEvict(value = "statistics", allEntries = true)
  public void saveStatistic(StatisticRequest request) {
    Statistic statistic = new Statistic();

    statistic.setEchoRef(request.getEchoRef());
    statistic.setDescription(request.getDescription());
    statistic.setChannel(request.getChannel());
    statistic.setTitle(request.getTitle());
    statistic.setDeleted(false);
    saveStatistic(statistic);
  }

  public List<StatisticCountResponse> countByTitleForDay(String date) {
    try {
      return repository.countByTitleForDay(
          new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public List<StatisticCountResponse> countByTitleForMonth(int month) {
    return repository.countByTitleForMonth(month);
  }

  public List<StatisticCountResponse> countByTitleForYear(int year) {
    return repository.countByTitleForYear(year);
  }

  public List<MonthlyStatsResponse> getMonthlyDataForYear(int year) {
    return repository.getMonthlyDataForYear(year);
  }
}
