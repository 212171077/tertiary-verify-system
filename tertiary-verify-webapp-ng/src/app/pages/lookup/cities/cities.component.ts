import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { Table } from "primeng/table";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-cities",
  templateUrl: "./cities.component.html",
  providers: [MessageService, ConfirmationService],
})
export class CitiesComponent {
  provinceList: any;
  selectedProvince: any;
  loading: boolean = true;
  dataList: any;
  loginResponse: LoginResponse;
  token: string;
  updateRequest: any;
  @ViewChild("filter") filter: ElementRef;
  @ViewChild("dt") table: Table;

  provinceControl = new FormControl("", [Validators.required]);
  descControl = new FormControl("", [Validators.required]);

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
    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_provinces")
      .subscribe(
        (response) => {
          this.provinceList = response;
        },
        (error) => {
          console.error("Fetch province error:", error);
          if (error.status === 402) {
            this.router.navigate(["/access"]);
          } else {
            this.router.navigate(["/error"]);
          }
        }
      );
  }

  loadData() {
    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_cities")
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
      province: "",
    };
    this.provinceControl = new FormControl("", [Validators.required]);
    this.descControl = new FormControl("", [Validators.required]);
  }

  saveData() {

    let request = {
      id: this.updateRequest.id,
      description: this.updateRequest.description,
      provinceId: this.updateRequest.province.id,
    };

    this.tertiaryVerifyService
      .lookupGenericPost(
        request,
        this.sessionService.getLoginObject().token,
        "save_city"
      )
      .subscribe(
        (response) => {
          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail: "City saved successful",
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
    console.info("Data: " + JSON.stringify(data));
    this.scrollToComponent();
    this.updateRequest = {
      id: data.id,
      description: data.description,
      province: data.province,
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
            "delete_city/" + data.id
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
