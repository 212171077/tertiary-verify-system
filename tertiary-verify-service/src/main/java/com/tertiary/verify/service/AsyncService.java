package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncService.class);
    private ContactUsService contactUsService;
    private ReportedInstitutionsService reportedInstitutionsService;
    private StatisticService statisticService;
    private InstitutionService institutionService;

    // @Lazy breaks the circular dependency, which otherwise fails startup when running from the packaged jar
    public AsyncService(@Lazy ContactUsService contactUsService,
                        @Lazy ReportedInstitutionsService reportedInstitutionsService,
                        @Lazy StatisticService statisticService,
                        @Lazy InstitutionService institutionService) {
        this.contactUsService = contactUsService;
        this.reportedInstitutionsService = reportedInstitutionsService;
        this.statisticService = statisticService;
        this.institutionService = institutionService;
    }

    @Async
    public void contactUs(ContactUsRequest request) {
        try {
           contactUsService.sendContactUsMessage(request);
            LOGGER.info("Contact us message sent.");
        } catch (Exception e) {
            LOGGER.error("Error when saving contact us message. Error: {}, Request Details: {}", e.getMessage(),request);
        }
    }

    @Async
    public void reportBogusCourse(ReportCourseRequest request) {
        try {
            reportedInstitutionsService.reportBogusCourse(request);
        } catch (Exception e) {
            LOGGER.error("Error when report bogus course. Error: {}, Request Details: {}", e.getMessage(),request);
        }
    }

    @Async
    public void reportBogusInstitution(ReportInstitutionRequest request) {
        try {
            reportedInstitutionsService.reportBogusInstitution(request);
        } catch (Exception e) {
            LOGGER.error("Error when report bogus institution. Error: {}, Request Details: {}", e.getMessage(),request);
        }
    }

    @Async
    public void createOrUpdateInstitution(RegisterInstitutionRequest request) {
        try {
            institutionService.createOrUpdateInstitution(request, ApprovalStatusEnum.RegistrationInProgress);
        } catch (Exception e) {
            LOGGER.error("Error when creating/updating institution. Error: {}, Request Details: {}", e.getMessage(),request);
        }
    }

    @Async
    public void saveStatistic(StatisticRequest request) {
        try {
            statisticService.saveStatistic(request);
        } catch (Exception e) {
            LOGGER.error("Error when saving statistics. Error: {}, Request Details: {}", e.getMessage(),request);
        }
    }








}
