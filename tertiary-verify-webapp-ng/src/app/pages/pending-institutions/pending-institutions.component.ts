import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { Table } from "primeng/table";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: 'app-pending-institutions',
  templateUrl: './pending-institutions.component.html',
  providers: [MessageService, ConfirmationService],
})
export class PendingInstitutionsComponent {

  loading: boolean = true;
  regLoading: boolean = false;
  dataList: any;
  loginResponse: LoginResponse;
  token: string;
  display: boolean;
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
    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;
 
    this.loadAllInstitutions();
  }

  loadAllInstitutions(){
    this.tertiaryVerifyService
    .tertiaryGenericGet(this.token, "find_pending_approval_institution_details/"+this.loginResponse.id)
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

  updateData(data: any) {
    this.router.navigate(['/pages/pending-approval-view'], {
      queryParams: { uuid: data.uuid }
    });
  }
}
