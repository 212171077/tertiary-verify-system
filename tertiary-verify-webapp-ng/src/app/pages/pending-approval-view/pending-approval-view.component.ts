import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { DropDown } from "src/app/model/DropDown";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-pending-approval-view",
  templateUrl: "./pending-approval-view.component.html",
  providers: [MessageService, ConfirmationService],
})
export class PendingApprovalViewComponent {
  loginResponse: LoginResponse;
  token: string;
  uuid: string;
  institutionDetails: any;
  loading: boolean = true;
  addressList: any;
  institutionCourseList: any;
  loadingInstCourses: boolean;
  confirmLoading: boolean;
  selectedRejectReasons: any[] = []; 
  displayRejectReason: boolean = false;
  rejectReasonList: any;

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit() {
 
    this.activatedRoute.queryParams.subscribe((params) => {
      this.uuid = params["uuid"];
    });

    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;

    this.tertiaryVerifyService
      .tertiaryGenericGet(this.token, "find_institution_by_uuid/" + this.uuid)
      .subscribe(
        (response) => {
          this.loading = false;
          this.institutionDetails = response;
          this.buildAddressList(this.institutionDetails.addressList);
          this.loadInstitutionCourse();
        },
        (error) => {
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
      this.loadRejectReason();
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

  loadRejectReason(){
    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;
    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_reject_reasons")
      .subscribe(
        (response) => {
          this.rejectReasonList = response;
        },
        (error) => {
          console.error("Fetch reject reason error:", error);
          if (error.status === 402) {
            this.router.navigate(["/access"]);
          } else {
            this.router.navigate(["/error"]);
          }
        }
      );
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

  findInstitutionType(options: any) {
    return this.findSelectedOption(options)?.option;
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

  approveInstitution(event: Event) {

    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message:
        "Are you sure the institution details have been accurately verified, and you wish to grant approval to this institution?",
        icon: "pi pi-exclamation-triangle",
      accept: () => {
        const request = {
          userId: this.sessionService.getLoginObject().id,
          institutionId: this.institutionDetails.id,
        };

        this.tertiaryVerifyService
          .tertiaryGenericPost(
            request,
            this.sessionService.getLoginObject().token,
            "approve_institution"
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                key: "tst",
                severity: "info",
                summary: "Success",
                detail: "Institution has received formal approval",
              });

              setTimeout(() => {
                this.router.navigate(["pages/pending-institutions"]);
              }, 1000);
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
        this.messageService.add({
          severity: "error",
          summary: "Rejected",
          detail: "You have rejected",
        });
      },
    });
  }

  rejectInstitution(event: Event) {
    if (
      this.selectedRejectReasons == null ||
      this.selectedRejectReasons == undefined ||
      this.selectedRejectReasons.length < 1
    ) {
      this.messageService.add({
        key: "tst",
        severity: "warn",
        summary: "Error",
        detail: "Please select at least one reject reason",
      });
    } else {
      this.confirmationService.confirm({
        key: "confirm2",
        target: event.target,
        message:
          "Are you sure the institution details have been accurately verified, and you wish to reject this institution?",
        accept: () => {

          console.info("selectedRejectReasons: "+JSON.stringify(this.selectedRejectReasons));
          const request = {
            userId: this.sessionService.getLoginObject().id,
            institutionId: this.institutionDetails.id,
            rejectReasonIdList: this.selectedRejectReasons.map((rj) => rj.id),
          };

          this.tertiaryVerifyService
            .tertiaryGenericPost(
              request,
              this.sessionService.getLoginObject().token,
              "reject_institution"
            )
            .subscribe(
              (response) => {
                this.messageService.add({
                  severity: "info",
                  summary: "Success",
                  detail: "Institution has been successfully regeceded",
                });

                setTimeout(() => {
                  this.router.navigate(["pages/pending-institutions"]);
                }, 1000);
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
          //Institution not rejected
        },
      });
    }
  }
}
