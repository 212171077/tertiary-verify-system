package com.tertiary.verify.scheduler;

import com.tertiary.verify.mail.MailSender;
import com.tertiary.verify.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
  /**
   * <a
   * href="https://www.callicoder.com/spring-boot-task-scheduling-with-scheduled-annotation/">Tutorial
   * Link 1</a> <a
   * href="https://dzone.com/articles/running-on-time-with-springs-scheduled-tasks">Tutorial Link
   * 2</a>
   */
  @Autowired private InstitutionService institutionService;

  @Autowired private MailSender mailSender;

  /**
   * @Scheduled(cron = "[Seconds] [Minutes] [Hours] [Day of month] [Month] [Day of week] [Year]")
   * -------------------------------------------------------------------------------------------- @Scheduled(cron
   * = "0 * * * * ?")=Task will be executed every minute @Scheduled(cron = "0 0 12 * * ?")=Fires at
   * 12 PM every day @Scheduled(cron = "0 15 10 * * ? 2005")=Fires at 10:15 AM every day in the year
   * 2005 @Scheduled(cron = "0/20 * * * * ?")=Fires every 20 seconds
   * --------------------------------------------------------------------------------------------
   */
  @Scheduled(cron = "0 0 12 * * ?")
  public void scheduleTaskWithCronExpression() {
    institutionService.updateExpiredInstitution();
  }

  @Scheduled(fixedDelay = 2000)
  public void scheduleTaskWithFixedDelay() {
    mailSender.sendEmailContent();
  }
}
