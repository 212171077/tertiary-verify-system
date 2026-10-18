import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { Table } from "primeng/table";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-courses",
  templateUrl: "./courses.component.html",
  providers: [MessageService, ConfirmationService],
})
export class CoursesComponent {
  descControl = new FormControl("", [Validators.required]);
  courseTypeControl = new FormControl("", [Validators.required]);
  courseLeveControl = new FormControl("", [Validators.required]);
  updateRequest: any;
  courseTypeList: any;
  courseLevelList: any;
  selectedCourseType: any;
  selectedCourseLevel: any;
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

    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_course_types")
      .subscribe(
        (response) => {
          this.courseTypeList = response;
          console.info("courseTypeList: ", JSON.stringify(this.courseTypeList));
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

    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_course_levels")
      .subscribe(
        (response) => {
          this.courseLevelList = response;
          console.info("courseTypeList: ", JSON.stringify(this.courseTypeList));
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
      .lookupGenericGet(this.token, "find_all_courses")
      .subscribe(
        (response) => {
          this.dataList = response;
          this.loading = false;
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
    this.courseLeveControl = new FormControl("", [Validators.required]);
    this.courseTypeControl = new FormControl("", [Validators.required]);

    this.selectedCourseLevel = null;
    this.selectedCourseType = null;
    
  }

  saveData() {
    let request = {
      id: this.updateRequest.id,
      description: this.updateRequest.description,
      courseTypeId: this.selectedCourseType.id,
      courseLevelId: this.selectedCourseLevel.id,
    };
    this.tertiaryVerifyService
      .lookupGenericPost(
        request,
        this.sessionService.getLoginObject().token,
        "save_course"
      )
      .subscribe(
        (response) => {
          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail: "Course saved successful",
          });
          this.loadData();
          this.clearData();
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
    console.info("Data: ", JSON.stringify(data));
    this.selectedCourseLevel = data.courseLevel;
    this.selectedCourseType = data.courseType;
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
            "delete_course/" + data.id
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
