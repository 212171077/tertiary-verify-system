package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.beans.MonthlyStatsResponse;
import com.tertiary.verify.data_model.model.beans.StatisticCountResponse;
import com.tertiary.verify.data_model.model.entities.Statistic;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {
  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.StatisticCountResponse(s.title, COUNT(s)) FROM Statistic s WHERE DATE(s.createDate) = :currentDate GROUP BY s.title")
  List<StatisticCountResponse> countByTitleForDay(@Param("currentDate") Date date);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.StatisticCountResponse(s.title, COUNT(s)) FROM Statistic s WHERE MONTH(s.createDate) = :month GROUP BY s.title")
  List<StatisticCountResponse> countByTitleForMonth(@Param("month") int month);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.StatisticCountResponse(s.title, COUNT(s)) FROM Statistic s WHERE YEAR(s.createDate) = :year GROUP BY s.title")
  List<StatisticCountResponse> countByTitleForYear(@Param("year") int year);

  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.MonthlyStatsResponse(MONTH(s.createDate), s.title, COUNT(s.id)) FROM Statistic s WHERE YEAR(s.createDate) = :year GROUP BY MONTH(s.createDate), s.title")
  List<MonthlyStatsResponse> getMonthlyDataForYear(@Param("year") int year);
}
