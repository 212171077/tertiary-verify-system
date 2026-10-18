import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { DropDown } from "src/app/model/DropDown";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-confirm-institution-details",
  templateUrl: "./confirm-institution-details.component.html",
  styleUrls: ["../../../../assets/demo/badges.scss"],
  providers: [MessageService, ConfirmationService],
})
export class ConfirmInstitutionDetailsComponent {
  loginResponse: LoginResponse;
  token: string;
  uuid: string;
  institutionDetails: any;
  loading: boolean = true;
  addressList: any;
  institutionCourseList: any;
  loadingInstCourses: boolean;
  confirmLoading: boolean;

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit() {
    this.tertiaryVerifyService.manageInstDetails$.subscribe((data) => {
      this.uuid = data.uuid;

      if (
        this.uuid !== undefined &&
        (this.tertiaryVerifyService === null ||
          this.institutionDetails === undefined ||
          Object.keys(this.institutionDetails).length === 0)
      ) {
       
        this.loginResponse = this.sessionService.getLoginObject();
        this.token = this.loginResponse.token;

        this.tertiaryVerifyService
          .tertiaryGenericGet(
            this.token,
            "find_institution_by_uuid/" + this.uuid
          )
          .subscribe(
            (response) => {
              this.loading = false;
              this.institutionDetails = response;

              this.buildAddressList(this.institutionDetails.addressList);
              this.loadInstitutionCourse();
              this.tertiaryVerifyService.setManageInstDetails(response);
            },
            (error) => {
              this.loading = false;
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

  confirmInstDetails(event: Event) {
   
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure you want submit the institution details for verification?",
      accept: () => {
        this.confirmLoading = true;
        const request = {
          userId: this.sessionService.getLoginObject().id,
          institutionId: this.institutionDetails.id,
        };

        this.tertiaryVerifyService
          .tertiaryGenericPost(
            request,
            this.sessionService.getLoginObject().token,
            "confirm_institution_details"
          )
          .subscribe(
            (response) => {
              this.confirmLoading = false;
              this.messageService.add({
                severity: "info",
                summary: "Success",
                detail: "Institution details submitted for verification",
              });
              this.router.navigate(["/"]);
            },
            (error) => {
              this.confirmLoading = false;
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
        //Rejected
      },
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
}
