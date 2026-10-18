import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { DropDown } from "src/app/model/DropDown";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-course-details",
  templateUrl: "./course-details.component.html",
  providers: [MessageService, ConfirmationService],
})
export class CourseDetailsComponent {
  loadingCourses: boolean;
  loadingAddress: boolean;
  loadingInstCourses: boolean;
  updateLoading: boolean;
  uuid: string;
  courseList: any;
  selectedCourse: any;
  filteredCourseList: any;
  institutionDetails: any;
  institutionCourseList: any;
  addressList: any;
  loginResponse: LoginResponse;
  token: string;
  selectedAddresses: any[] = [];
  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadingCourses = true;
    this.tertiaryVerifyService
      .lookupGenericGet(
        this.sessionService.getLoginObject().token,
        "find_all_courses"
      )
      .subscribe(
        (response) => {
          this.courseList = response;
        },
        (error) => {
          console.error("Error: ", error);
          this.loadingCourses = false;
          this.messageService.add({
            key: "tst",
            severity: "error",
            summary: "Error",
            detail:
              "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.",
          });
        }
      );

    this.loadingAddress = true;
    this.tertiaryVerifyService.manageInstDetails$.subscribe((data) => {
      this.uuid = data.uuid;

      if (
        this.uuid !== undefined &&
        (this.tertiaryVerifyService === null ||
          this.institutionDetails === undefined ||
          Object.keys(this.institutionDetails).length === 0)
      ) {
        console.info("On Init, confirm details, fetching institution details");
        this.loginResponse = this.sessionService.getLoginObject();
        this.token = this.loginResponse.token;

        this.tertiaryVerifyService
          .tertiaryGenericGet(
            this.token,
            "find_institution_by_uuid/" + this.uuid
          )
          .subscribe(
            (response) => {
              this.loadingAddress = false;
              this.institutionDetails = response;

              this.loadInstitutionCourse();

              this.buildAddressList(this.institutionDetails.addressList);
              this.tertiaryVerifyService.setManageInstDetails(response);
            },
            (error) => {
              this.loadingAddress = false;
              console.error(
                "Fetch institution details error: " + JSON.stringify(error)
              );
              if (error.status === 402) {
                this.router.navigate(["/access"]);
              } else {
                this.router.navigate(["/error"]);
              }
            }
          );
      }
    });
  }

  loadInstitutionCourse() {
    this.loadingInstCourses = true;
    this.tertiaryVerifyService
      .tertiaryGenericGet(
        this.token,
        "find_institution_course_details/" + this.institutionDetails.id
      )
      .subscribe(
        (response) => {
          this.loadingInstCourses = false;
          this.institutionCourseList = response;
        },
        (error) => {
          this.loadingInstCourses = false;
          console.error(
            "Fetch institution course details error: " + JSON.stringify(error)
          );
          if (error.status === 402) {
            this.router.navigate(["/access"]);
          } else {
            this.router.navigate(["/error"]);
          }
        }
      );
  }

  deleteCourse(event: Event, data: any) {
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want delete this course?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.tertiaryVerifyService
          .tertiaryGenericDelete(
            this.sessionService.getLoginObject().token,
            "delete_institution_course/" + data.id
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                key: "tst",
                severity: "info",
                summary: "Success",
                detail: "Course successfully deleted",
              });
              this.loadInstitutionCourse();
            },
            (error) => {
              console.error("Error: ", error);
              this.messageService.add({
                key: "tst",
                severity: "error",
                summary: "Error",
                detail:
                  "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.",
              });
            }
          );
      },
      reject: () => {
        //Course not deleted
      },
    });
  }

  saveCourseDetails() {
    this.updateLoading = true;
    if (this.selectedCourse == null || this.selectedCourse == undefined) {
      this.messageService.add({
        key: "tst",
        severity: "error",
        summary: "Error",
        detail: "Please select a course",
      });
    } else if (
      this.selectedAddresses == null ||
      this.selectedAddresses == undefined ||
      this.selectedAddresses.length == 0
    ) {
      this.messageService.add({
        key: "tst",
        severity: "error",
        summary: "Error",
        detail: "Please select at least 1 address",
      });
    } else {
      const request = {
        courseId: this.selectedCourse.id,
        institutionId: this.institutionDetails.id,
        addressIdList: this.selectedAddresses.map((addr) => addr.id),
      };

      this.tertiaryVerifyService
        .tertiaryGenericPost(
          request,
          this.sessionService.getLoginObject().token,
          "save_institution_course"
        )
        .subscribe(
          (response) => {
            this.messageService.add({
              key: "tst",
              severity: "info",
              summary: "Success",
              detail: "Course successfully added",
            });
            this.discardData();
            this.loadInstitutionCourse();
            this.updateLoading = false;
          },
          (error) => {
            console.error("Error: ", error);
            this.updateLoading = false;
            let errorMssg = "";
            if (error.status == 400) {
              errorMssg = error.error.message;
            } else if (error.status == 0) {
              console.error("Login error:", error);
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
  }

  nextPage() {
    this.router.navigate(["/pages/manage-institution/confirm-details"]);
  }

  discardData() {
    this.selectedAddresses = null;
    this.selectedCourse = null;
  }

  filterCourse(event) {
    const filtered: any[] = [];
    const query = event.query;
    for (let i = 0; i < this.courseList.length; i++) {
      const course = this.courseList[i];
      if (course.description.toLowerCase().indexOf(query.toLowerCase()) == 0) {
        filtered.push(course);
      }
    }
    this.filteredCourseList = filtered;
  }

  buildAddressList(list: any) {
    this.addressList = [];
    this.institutionDetails.addressList.forEach((addr) => {
      const address = {
        id: addr.id,
        institutionId: this.institutionDetails.id,
        addressLine1: addr.addressLine1,
        addressLine2: addr.addressLine2,
        addressLine3: addr.addressLine3,
        addressLine4: addr.addressLine4,
        code: addr.code,
        examinationNumber: addr.examinationNumber,
        city: this.findSelectedOption(addr.cityList)?.option,
        province: this.findSelectedOption(addr.provinceList)?.option,
      };

      this.addressList.push(address);
    });
  }

  findSelectedOption(options: DropDown[]) {
    var selected;
    if (options != null && options.length > 0) {
      options.forEach((item) => {
        if (item.selected) {
          selected = item;
          return true;
        }
        return false;
      });
    }
    return selected;
  }
}
