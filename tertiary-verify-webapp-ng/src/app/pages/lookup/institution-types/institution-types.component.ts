import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { Table } from "primeng/table";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-institution-types",
  templateUrl: "./institution-types.component.html",
  providers: [MessageService, ConfirmationService],
})
export class InstitutionTypesComponent {
  descControl = new FormControl("", [Validators.required]);
  updateRequest: any;
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
    private confirmationService: ConfirmationService,
    private elementRef: ElementRef
  ) {}

  ngOnInit() {
    this.clearData();
    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;
    this.loadData();
  }

  loadData() {
    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_institution_types")
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

  clearData() {
    this.updateRequest = {
      id: "",
      description: "",
    };
    this.descControl = new FormControl("", [Validators.required]);
  }

  saveData() {
    this.tertiaryVerifyService
      .lookupGenericPost(
        this.updateRequest,
        this.sessionService.getLoginObject().token,
        "save_institution_type"
      )
      .subscribe(
        (response) => {
          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail: "Institution type saved successful",
          });

          this.clearData();
          this.loadData();
        },
        (error) => {
          console.error("Error: ", error);
          let errorMssg = error.error.message;
          if (errorMssg == null || errorMssg == undefined) {
            errorMssg =
              "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";
          }
          this.messageService.add({
            key: "tst",
            severity: "error",
            summary: "Error",
            detail: errorMssg,
          });
        }
      );
  }

  updateData(data: any) {
    this.scrollToComponent();
    this.updateRequest = {
      id: data.id,
      description: data.description,
    };
  }

  scrollToComponent() {
    const element = this.elementRef.nativeElement.querySelector("#mainForm");
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  }

  clear(table: Table) {
    table.clear();
    this.filter.nativeElement.value = "";
  }

  deleteData(event: Event, data: any) {
    console.info("Delete Data: ", data);
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want delete this record?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.tertiaryVerifyService
        .lookupGenericDelete(
          this.sessionService.getLoginObject().token,
          "delete_institution_type/" + data.id
        )
        .subscribe(
          (response) => {
            this.messageService.add({
              key: "tst",
              severity: "warn",
              summary: "Deleted",
              detail: "The record has been deleted.",
            });
            this.clearData();
            this.loadData();
          },
          (error) => {
            console.error("Error: ", error);
            let errorMssg = error.error.message;
            if (errorMssg == null || errorMssg == undefined) {
              errorMssg =
                "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";
            }
            this.messageService.add({
              key: "tst",
              severity: "error",
              summary: "Error",
              detail: errorMssg,
            });
          }
        );
      },
      reject: () => {
       //Rejected
      },
    });
  }
}
