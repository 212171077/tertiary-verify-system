import { Component } from "@angular/core";
import { SessionService } from "src/app/service/session.service";

@Component({
  selector: "app-my-reported-institutions",
  templateUrl: "./my-reported-institutions.component.html",
})
export class MyReportedInstitutionsComponent {

  constructor(
    private sessionService: SessionService
  ) {}

  configs = {
    title: "My Reported Institutions",
    updateEnabled: true,
    viewInvestigator: false,
    viewReporter: true,
    serviceName:"find_open_reported_institution/"+this.sessionService.getLoginObject().id
  };
}
