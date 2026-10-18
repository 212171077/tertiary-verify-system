import { Component } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { SessionService } from "src/app/service/session.service";

@Component({
  selector: "app-all-reported-institutions",
  templateUrl: "./all-reported-institutions.component.html",
  providers: [MessageService, ConfirmationService],
})
export class AllReportedInstitutionsComponent {
  constructor(
    private sessionService: SessionService
  ) {}

  configs = {
    title: "All Reported Institutions",
    updateEnabled: false,
    viewInvestigator: true,
    viewReporter: true,
    serviceName:"find_all_reported_institution"
  };
}
