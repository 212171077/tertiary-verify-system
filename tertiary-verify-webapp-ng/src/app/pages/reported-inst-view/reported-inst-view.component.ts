import { Component, ElementRef, Input, ViewChild } from "@angular/core";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService, SelectItem } from "primeng/api";
import { Table } from "primeng/table";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: 'app-reported-inst-view',
  templateUrl: './reported-inst-view.component.html',
  providers: [MessageService, ConfirmationService],
})
export class ReportedInstViewComponent {
  @Input() configs: any;
  loading: boolean = true;
  displayInstDetails: boolean = false;
  displayInvestigatorDetails: boolean = false;
  displayReporterDetails: boolean = false;
  displayFeedbackDetails: boolean = false;
  displayInvestigationHistory: boolean = false;
  dataList: any;
  selectedInstitution: any;
  loginResponse: LoginResponse;
  selectedStatus: SelectItem;
  feedbackDetails: string;
  statusList: SelectItem[];
  token: string;
  @ViewChild("filter") filter: ElementRef;
  @ViewChild("dt") table: Table;

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private messageService: MessageService,
    private router: Router,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit() {
    this.loadInstitutionDetails();
    this.statusList = [
      { label: "Submitted", value: "Submitted" },
      { label: "Under Investigation", value: "Under Investigation" },
      { label: "Investigation Pending", value: "Investigation Pending" },
      { label: "Closed", value: "Closed" },
    ];
  }

  loadInstitutionDetails() {
    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;
    this.tertiaryVerifyService
      .tertiaryGenericGet(this.token, this.configs.serviceName)
      .subscribe(
        (response) => {
          this.dataList = response;
          this.loading = false;
          console.info("Data List: ", this.dataList);
        },
        (error) => {
          console.error("Fetch user details error:", error);
          if (error.status === 402) {
            this.router.navigate(["/access"]);
          } else {
            this.router.navigate(["/error"]);
          }
        }
      );
  }

  submitFeedback() {
   
    if (this.selectedStatus == undefined || this.selectedStatus == null) {
      this.messageService.add({
        key: "tst",
        severity: "warn",
        summary: "Success",
        detail: "Please select status",
      });
    } else if (
      this.feedbackDetails == undefined ||
      this.feedbackDetails == null
    ) {
      this.messageService.add({
        key: "tst",
        severity: "warn",
        summary: "Success",
        detail: "Please enter feedback details",
      });
    } else {

      const request = {
        reportedInstitutionId: this.selectedInstitution.id,
        feedbackDetails: this.feedbackDetails,
        status: this.selectedStatus.toString().replace(/\s/g, ''),
      };

      console.info("request: "+JSON.stringify(request));

      this.tertiaryVerifyService
        .tertiaryGenericPost(
          request,
          this.sessionService.getLoginObject().token,
          "save_investigation"
        )
        .subscribe(
          (response) => {
            this.messageService.add({
              key: "tst",
              severity: "info",
              summary: "Success",
              detail: "Investigation feedback submitted successfully",
            });
            this.loadInstitutionDetails();
            this.displayFeedbackDetails = false;
          },
          (error) => {
            console.error("Error: ", error);
            if (error.status == 400) {
              this.messageService.add({
                key: "tst",
                severity: "warn",
                summary: "Error",
                detail: error.error.message,
              });
            } else {
              this.messageService.add({
                key: "tst",
                severity: "error",
                summary: "Error",
                detail:
                  "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.",
              });
            }
          }
        );
    }
  }

  clear(table: Table) {
    table.clear();
    this.filter.nativeElement.value = "";
  }

  showInvestigatorDialog(data: any) {
    this.selectedInstitution = data;
    this.displayInvestigatorDetails = true;
  }

  showIReporterDialog(data: any) {
    this.selectedInstitution = data;
    this.displayReporterDetails = true;
  }

  showInstDetailsDialog(data: any) {
    this.selectedInstitution = data;
    this.displayInstDetails = true;
  }

  showFeedbackDialog(data: any) {
    this.selectedInstitution = data;
    this.displayFeedbackDetails = true;
    this.selectedStatus = data.status;
    this.feedbackDetails = data.investigatorFeedback;
  }

  showInvestigationFeedback(data: any){
    this.selectedInstitution = data;
    this.displayInvestigationHistory = true;
  }
}
