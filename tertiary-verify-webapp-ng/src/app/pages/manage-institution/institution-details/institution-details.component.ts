import { Component, OnInit } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MenuItem, MessageService } from "primeng/api";
import { DropDown } from "src/app/model/DropDown";
import { InstitutionDetails } from "src/app/model/InstitutionDetails";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

// Custom validator function
function validLinkOrEmpty(control: FormControl) {
  const value = control.value;

  // Allow blank/empty or null values
  if (!value) {
    return null;
  }

  // Check if the value is a valid URL
  const urlPattern = /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/i;
  return urlPattern.test(value) ? null : { invalidLink: true };
}

@Component({
  selector: "app-institution-details",
  templateUrl: "./institution-details.component.html",
  providers: [MessageService, ConfirmationService],
})
export class InstitutionDetailsComponent {
  optionItems: MenuItem[];
  showRejectReasonsPopup = false;
  showUpdateStartDatePopup = false;
  startDate: Date[] | undefined;
  updateLoading: boolean;
  updateStartLoading: boolean;
  updateInstDetails: any;
  rejectReasonList: any;
  manageInstDetails: InstitutionDetails;
  institutionTypeControl = new FormControl("", [Validators.required]);
  accreditationNumberControl = new FormControl("", [Validators.required]);
  institutionNameControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
    Validators.minLength(1),
    Validators.maxLength(100),
  ]);

  contactNumberControl = new FormControl("", [Validators.required]);

  alternativeContactNumberControl = new FormControl("", [Validators.required]);

  emailControl = new FormControl("", [Validators.required, Validators.email]);

  applyLinkControl = new FormControl("", [validLinkOrEmpty]);
  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.loadInstitutionDetailsData();
    this.prepareOptionItems();
  }

  loadInstitutionDetailsData() {
    this.tertiaryVerifyService.manageInstDetails$.subscribe((data) => {
      this.manageInstDetails = data;

      let startDate = null;
      let endDate = null;

      if (this.manageInstDetails.accreditationStartDate !== null) {
        startDate = new Date(this.manageInstDetails.accreditationStartDate);
      }

      if (this.manageInstDetails.accreditationEndDate !== null) {
        endDate = new Date(this.manageInstDetails.accreditationEndDate);
      }

      this.updateInstDetails = {
        id: this.manageInstDetails.id,
        institutionName: this.manageInstDetails.institutionName,
        contactNumber: this.manageInstDetails.contactNumber,
        alternativeContactNumber:
          this.manageInstDetails.alternativeContactNumber,
        email: this.manageInstDetails.email,
        accreditationStartDate: startDate,
        accreditationEndDate: endDate,
        accreditationNumber: this.manageInstDetails.accreditationNumber,
        status: this.manageInstDetails.status,
        qrCode: this.manageInstDetails.qrCode,
        image: this.manageInstDetails.image,
        createDate: this.manageInstDetails.createDate,
        lastUpdateDate: this.manageInstDetails.lastUpdateDate,
        creatorUserID: this.manageInstDetails.creatorUserID,
        supervisorID: this.manageInstDetails.supervisorID,
        uuid: this.manageInstDetails.uuid,
        applyLink: this.manageInstDetails.applyLink,
        institutionType: this.findSelectedOption(
          this.manageInstDetails.institutionTypeList
        ),
        prioritize: this.manageInstDetails.prioritize,
      };

      console.info(this.manageInstDetails.accreditationStartDate);
    });
  }

  prepareOptionItems() {
    this.optionItems = [
      {
        label: "Prioritise",
        icon: "pi pi-verified",
        command: () => {
          this.prioritizeInstitution();
        },
      },
      {
        label: "Deprioritise",
        icon: "pi pi-times-circle",
        command: () => {
          this.deprioritizeInstitution();
        },
      },
      {
        label: "Reject Reasons",
        icon: "pi pi-thumbs-down",
        command: () => {
          this.viewRejectReasons();
        },
      },
      {
        label: "Update Start Date",
        icon: "pi pi-calendar-plus",
        command: () => {
          this.showUpdateStartDateDialog();
        },
      },
      {
        label: "Change to expired",
        icon: "pi pi-times-circle",
        command: () => {
          this.updateExpiredInst();
        },
      },
    ];
  }

  viewRejectReasons() {
    this.tertiaryVerifyService
      .tertiaryGenericGet(
        this.sessionService.getLoginObject().token,
        "find_institution_reject_reasons/" + this.updateInstDetails.id
      )
      .subscribe(
        (response) => {
          this.rejectReasonList = response;
          this.showRejectReasonsPopup = true;
        },
        (error) => {
          this.messageService.add({
            key: "tst",
            severity: "error",
            summary: "Error",
            detail:
              "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.",
          });
        }
      );
  }

  updateStartDate() {
    this.updateStartLoading = true;
    const request = {
      institutionId: this.updateInstDetails.id,
      startDate: this.startDate,
    };

    if (this.startDate != undefined && this.startDate != null) {
      this.tertiaryVerifyService
        .tertiaryGenericPost(
          request,
          this.sessionService.getLoginObject().token,
          "update_accreditation_start_date"
        )
        .subscribe(
          (response) => {
            this.messageService.add({
              severity: "info",
              summary: "Success",
              detail: "Accreditation start date updated",
            });
            this.showUpdateStartDatePopup = false;
            this.updateStartLoading= false;
          },
          (error) => {
            this.updateStartLoading= false;
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
    } else {
      this.messageService.add({
        severity: "warn",
        summary: "Success",
        detail: "Please select accreditation start date",
      });
      this.updateStartLoading= false;
    }
  }

  showUpdateStartDateDialog() {
    this.confirmationService.confirm({
      key: "confirm1",
      message:
        "After this action, the institution will be verified. Are you sure you want to continue?",
      accept: () => {
        this.showUpdateStartDatePopup = true;
      },
      reject: () => {
        //Rejected
      },
    });
  }

  prioritizeInstitution() {
    this.confirmationService.confirm({
      key: "confirm1",
      message: "Are you sure you want prioritise this institution?",
      accept: () => {
        const request = {
          uuid: this.updateInstDetails.uuid,
          userId: this.sessionService.getLoginObject().id,
        };

        this.tertiaryVerifyService
          .tertiaryGenericPost(
            request,
            this.sessionService.getLoginObject().token,
            "prioritise_institution"
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                severity: "info",
                summary: "Success",
                detail: "Institution prioritisation",
              });
            },
            (error) => {
              console.error("Error: ", error);
              this.updateLoading = false;
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

  deprioritizeInstitution() {
    this.confirmationService.confirm({
      key: "confirm1",
      message: "Are you sure you want deprioritise this institution?",
      accept: () => {
        const request = {
          uuid: this.updateInstDetails.uuid,
          userId: this.sessionService.getLoginObject().id,
        };

        this.tertiaryVerifyService
          .tertiaryGenericPost(
            request,
            this.sessionService.getLoginObject().token,
            "deprioritise_institution"
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                severity: "info",
                summary: "Success",
                detail: "Institution prioritisation",
              });
            },
            (error) => {
              console.error("Error: ", error);
              this.updateLoading = false;
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

  updateExpiredInst() {
    this.confirmationService.confirm({
      key: "confirm1",
      message:
        "This action will set the status to Expired, Are you sure you want update the status?",
      accept: () => {
        const request = {
          institutionId: this.updateInstDetails.id,
          status: "Expired",
        };

        console.info("Request: " + JSON.stringify(request));

        this.tertiaryVerifyService
          .tertiaryGenericPost(
            request,
            this.sessionService.getLoginObject().token,
            "update_institution_status"
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                severity: "info",
                summary: "Success",
                detail: "Institution status updated",
              });
              this.showUpdateStartDatePopup = false;
            },
            (error) => {
              console.error("Error: ", error);
              this.updateLoading = false;
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

  discardData(event: Event) {
    console.info("Discard Data...");
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want discard the changes?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.loadInstitutionDetailsData();
        this.messageService.add({
          key: "tst",
          severity: "warn",
          summary: "Discard",
          detail: "The changes has been discarded.",
        });
      },
      reject: () => {
        this.messageService.add({
          key: "tst",
          severity: "info",
          summary: "Rejected",
          detail: "Thank you, save your changes when you are done!",
        });
      },
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

  updateData() {
    this.updateLoading = true;
    if (
      ((this.updateInstDetails.accreditationStartDate == null ||
        this.updateInstDetails.accreditationStartDate == undefined) &&
        (this.updateInstDetails.accreditationEndDate != null ||
          this.updateInstDetails.accreditationEndDate != undefined)) ||
      ((this.updateInstDetails.accreditationEndDate == null ||
        this.updateInstDetails.accreditationEndDate == undefined) &&
        (this.updateInstDetails.accreditationStartDate != null ||
          this.updateInstDetails.accreditationStartDate != undefined))
    ) {
      this.messageService.add({
        key: "tst",
        severity: "warn",
        summary: "Error",
        detail:
          "Please select both accreditation start date and accreditation end date",
      });

      return null;
    }

    
    const request = {
      id: this.updateInstDetails.id,
      creatorUserID: this.sessionService.getLoginObject().id,
      institutionName: this.updateInstDetails.institutionName,
      contactNumber: this.updateInstDetails.contactNumber,
      email: this.updateInstDetails.email,
      alternativeContactNumber: this.updateInstDetails.alternativeContactNumber,
      accreditationNumber: this.updateInstDetails.accreditationNumber,
      institutionType: this.updateInstDetails.institutionType.option,
      applyLink: this.updateInstDetails.applyLink,
      accreditationStartDate: this.updateInstDetails.accreditationStartDate,
      accreditationEndDate: this.updateInstDetails.accreditationEndDate,
    };

    this.tertiaryVerifyService
      .tertiaryGenericPost(
        request,
        this.sessionService.getLoginObject().token,
        "update_institution"
      )
      .subscribe(
        (response) => {
          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail: "Institution successfully updated",
          });
          setTimeout(() => {
            this.updateLoading = false;
            this.router.navigate(
              ["/pages/manage-institution/address-details"],
              {
                queryParams: { uuid: "test" },
              }
            );
          }, 1000); // 1 second
        },
        (error) => {
          console.error("Error: ", error);
          this.updateLoading = false;

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

  nextPage() {
    this.router.navigate(["/pages/manage-institution/address-details"]);
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    const blobName = "inst-image-" + this.manageInstDetails.id;
   //TODO: enable upload
  }

  updateImage(blobName: string) {
    const request = {
      id: this.manageInstDetails.id,
      imageName: blobName,
      imageType: "INSTITUTION_IMAGE",
    };
    this.tertiaryVerifyService
      .tertiaryGenericPost(
        request,
        this.sessionService.getLoginObject().token,
        "update_image"
      )
      .subscribe(
        (response) => {
          this.updateManageInstDetails();
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
  }

  updateManageInstDetails() {
    this.tertiaryVerifyService
      .tertiaryGenericGet(
        this.sessionService.getLoginObject().token,
        "find_institution_by_uuid/" + this.manageInstDetails.uuid
      )
      .subscribe(
        (response) => {
          this.tertiaryVerifyService.setManageInstDetails(response);
          this.messageService.add({
            key: "tst",
            severity: "success",
            summary: "Success Message",
            detail: "Image uploaded successfully",
          });
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
  }
}
