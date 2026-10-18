package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.chart.bar.BarGraphData;
import com.tertiary.verify.data_model.model.chart.pie.*;
import com.tertiary.verify.data_model.model.entities.Task;
import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import com.tertiary.verify.data_model.model.entities.enums.ReportedInstitutionsStatus;
import com.tertiary.verify.framework.AbstractService;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReportService extends AbstractService {
  List<String> monthList =
      Arrays.asList(
          "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
  @Autowired private ReportedInstitutionsService reportedInstitutionsService;
  @Autowired private InstitutionService institutionService;
  @Autowired private MailLogService mailLogService;
  @Autowired private EmailContentService emailContentService;
  @Autowired private TaskService taskService;
  @Autowired private UserService userService;
  @Autowired private TertiaryVerifyService tertiaryVerifyService;
  @Autowired private StatisticService statisticService;

  public ResponseEntity<?> dashboardCardsReport() {
    try {
      DashboardCardResponse response = new DashboardCardResponse();
      response.setReportedInstitutions(reportedInstitutionsService.countAll());
      response.setUnderInvestigationInstitutions(
          reportedInstitutionsService.countByStatus(ReportedInstitutionsStatus.UnderInvestigation));
      response.setAccreditedCourses(tertiaryVerifyService.countAccreditedCourse());
      response.setApprovedInstitutions(
          institutionService.countByStatus(ApprovalStatusEnum.Approved));
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error: ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(MessageResponse.builder().success(false).message("Internal Server Error").build());
    }
  }

  public ResponseEntity<?> findPendingEmail() {
    try {
      List<MailLogResponse> responseList =
          emailContentService.findTop100ByOrderByCreateDateDesc().stream()
              .map(
                  mailLog -> {
                    String status =
                        mailLog.getErrorMessage() == null || mailLog.getErrorMessage().isEmpty()
                            ? "PENDING"
                            : "FAILED";
                    return MailLogResponse.builder()
                        .createDate(mailLog.getCreateDate())
                        .fromEmail(mailLog.getFromEmail())
                        .toEmail(mailLog.getToEmail())
                        .subject(mailLog.getSubject())
                        .errorMessage(mailLog.getErrorMessage())
                        .status(status)
                        .build();
                  })
              .collect(Collectors.toList());

      return ResponseEntity.ok(responseList);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(MessageResponse.builder().success(false).message("Internal Server Error").build());
    }
  }

  public ResponseEntity<?> findAllMailLogs() {
    try {
      List<MailLogResponse> responseList =
          mailLogService.findTop100ByOrderByCreateDateDesc().stream()
              .map(
                  mailLog -> {
                    String status =
                        (mailLog.getError() == null || !mailLog.getError()) ? "SENT" : "FAILED";
                    return MailLogResponse.builder()
                        .createDate(mailLog.getCreateDate())
                        .fromEmail(mailLog.getFromEmail())
                        .toEmail(mailLog.getToEmail())
                        .subject(mailLog.getSubject())
                        .error(mailLog.getError())
                        .errorMessage(mailLog.getErrorMessage())
                        .status(status)
                        .build();
                  })
              .collect(Collectors.toList());

      return ResponseEntity.ok(responseList);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(MessageResponse.builder().success(false).message("Internal Server Error").build());
    }
  }

  public ResponseEntity<?> taskChart(Long userId) {
    try {
      TaskPieChartResponse response = new TaskPieChartResponse();
      int completed = 0;
      int open = 0;
      int totalTasks = 0;

      List<Task> taskList =
          taskService.findByUserAndDeleted(userService.findByIdAndDeleted(userId, false));
      if (!taskList.isEmpty()) {
        totalTasks = taskList.size();
        completed = (int) taskList.stream().filter(Task::isCompleted).count();
        open = (int) taskList.stream().filter(task -> !task.isCompleted()).count();
      }

      PieChartData chartData = new PieChartData();
      chartData.setLabels(Arrays.asList("Completed Task(s)", "Open Task(s)"));
      Dataset dataset = new Dataset();
      dataset.setData(Arrays.asList(completed, open));
      dataset.setBackgroundColor(Arrays.asList("#2ECC71", "#F7DC6F"));
      dataset.setHoverBackgroundColor(Arrays.asList("#2ECC71", "#F7DC6F"));
      chartData.setDatasets(Collections.singletonList(dataset));

      ChartOptions chartOptions = new ChartOptions();

      Plugins plugins = new Plugins();
      Legend legend = new Legend();
      Labels labels = new Labels();
      labels.setUsePointStyle(true);
      labels.setColor("textColor");
      legend.setLabels(labels);
      plugins.setLegend(legend);

      chartOptions.setPlugins(plugins);

      response.setChartOptions(chartOptions);
      response.setPieChartData(chartData);
      response.setDisplayMessage(
          "You have completed "
              .concat(String.valueOf(completed))
              .concat(" task(s) out of ")
              .concat(String.valueOf(totalTasks)));

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(MessageResponse.builder().success(false).message("Internal Server Error").build());
    }
  }

  public ResponseEntity<?> getChartStatsDataForYear(int year) {
    List<MonthlyStatsResponse> list = statisticService.getMonthlyDataForYear(year);
    if (list != null && !list.isEmpty()) {
      List<com.tertiary.verify.data_model.model.chart.bar.Dataset> datasets = new ArrayList<>();
      BarGraphData barGraphData = new BarGraphData();
      barGraphData.setLabels(monthList);

      List<String> titleList =
          list.stream().map(MonthlyStatsResponse::getTitle).collect(Collectors.toList());

      titleList.forEach(
          title -> {
            com.tertiary.verify.data_model.model.chart.bar.Dataset dataset =
                new com.tertiary.verify.data_model.model.chart.bar.Dataset();
            dataset.setLabel(title);

            List<Integer> data = new ArrayList<>();
            for (int x = 1; x <= 12; x++) {
              int finalX = x;
              MonthlyStatsResponse monthlyStatsResponse =
                  list.stream()
                      .filter(
                          msr -> msr.getMonth() == finalX && msr.getTitle().equalsIgnoreCase(title))
                      .findAny()
                      .orElse(new MonthlyStatsResponse());
              data.add((int) monthlyStatsResponse.getCount());
            }

            dataset.setData(data);
            dataset.setBorderColor(Collections.singletonList(generateRandomHtmlColor()));
            dataset.setBorderWidth(3);
            Random random = new Random();
            if (random.nextBoolean()) {
              dataset.setBorderDash(Arrays.asList(5, 5));
              dataset.setPointRadius(3);
            }
            dataset.setFill(false);
            dataset.setTension(0.4);

            datasets.add(dataset);
          });

      barGraphData.setDatasets(datasets);
      return ResponseEntity.ok(barGraphData);
    } else {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
          .body(MessageResponse.builder().success(false).message("No stats data found").build());
    }
  }

  public ResponseEntity<?> getBarGraphStatsDataForMonth(int mon) {
    List<StatisticCountResponse> list = statisticService.countByTitleForMonth(mon);
    String monthName = Month.of(mon).getDisplayName(TextStyle.FULL, Locale.getDefault());
    String label = monthName.concat("'s mobile app usage statistics");
    return buildBarGraphDataResponse(list, label);
  }

  public ResponseEntity<?> getBarGraphStatsSataForDate(String date) {
    List<StatisticCountResponse> list = statisticService.countByTitleForDay(date);
    return buildBarGraphDataResponse(list, "Today's mobile app usage statistics");
  }

  public ResponseEntity<?> getBarGraphStatsDataForYear(int year) {
    List<StatisticCountResponse> list = statisticService.countByTitleForYear(year);
    String label = String.format("App Usage Statistics for %s", year);
    return buildBarGraphDataResponse(list, label);
  }

  private ResponseEntity<?> buildBarGraphDataResponse(
      List<StatisticCountResponse> list, String label) {
    if (list != null && !list.isEmpty()) {
      List<String> titleList =
          list.stream().map(StatisticCountResponse::getTitle).collect(Collectors.toList());
      List<Integer> countList =
          list.stream()
              .map(statisticCountResponse -> Math.toIntExact(statisticCountResponse.getCount()))
              .collect(Collectors.toList());

      List<com.tertiary.verify.data_model.model.chart.bar.Dataset> datasets = new ArrayList<>();
      com.tertiary.verify.data_model.model.chart.bar.Dataset dataset =
          new com.tertiary.verify.data_model.model.chart.bar.Dataset();

      dataset.setData(countList);
      dataset.setLabel(label);

      List<String> backgroundColorList = new ArrayList<>();
      List<String> borderColorList = new ArrayList<>();

      titleList.forEach(
          s -> {
            String borderColor = generateRandomRGBColor();
            String backgroundColor = borderColor.replace("rgb", "rgba").replace(")", " 0.2)");
            borderColorList.add(borderColor);
            backgroundColorList.add(backgroundColor);
          });

      dataset.setBackgroundColor(backgroundColorList);
      dataset.setBorderColor(borderColorList);
      dataset.setBorderWidth(1);
      datasets.add(dataset);

      BarGraphData barGraphData = new BarGraphData();
      barGraphData.setLabels(titleList);
      barGraphData.setDatasets(datasets);

      return ResponseEntity.ok(barGraphData);

    } else {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
          .body(MessageResponse.builder().success(false).message("No stats data found").build());
    }
  }

  public String generateRandomHtmlColor() {
    Random random = new Random();

    // Generate random RGB values
    int red = random.nextInt(256);
    int green = random.nextInt(256);
    int blue = random.nextInt(256);

    // Convert RGB to HTML color code
    return String.format("#%02X%02X%02X", red, green, blue);
  }

  public String generateRandomRGBColor() {
    Random random = new Random();

    // Generate random RGB values
    int red = random.nextInt(256);
    int green = random.nextInt(256);
    int blue = random.nextInt(256);

    return String.format("rgb(%s, %s, %s)", red, green, blue);
  }
}
