import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { Table } from "primeng/table";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: 'app-faculties',
  templateUrl: './faculties.component.html',
  providers: [MessageService, ConfirmationService]
})
export class FacultiesComponent {
  loading: boolean = true;
  dataList: any;
  loginResponse: LoginResponse;
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
    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;
    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_faculties")
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

  clear(table: Table) {
    table.clear();
    this.filter.nativeElement.value = "";
  }

  updateData(data: any) {
    console.info("Update Data: ", data);
  }

  deleteData(event: Event, data: any) {
    console.info("Delete Data: ", data);
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want delete this record?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.messageService.add({
          key: "tst",
          severity: "warn",
          summary: "Deleted",
          detail: "The record has been deleted.",
        });
      },
      reject: () => {
        this.messageService.add({
          key: "tst",
          severity: "info",
          summary: "Rejected",
          detail: "Thank you..",
        });
      },
    });
  }
}
