import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { Table } from "primeng/table";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-institution-registration",
  templateUrl: "./institution-registration.component.html",
  styleUrls: ["../../../assets/demo/badges.scss"],
  providers: [MessageService, ConfirmationService],
})
export class InstitutionRegistrationComponent {
  loading: boolean = true;
  regLoading: boolean = false;
  dataList: any;
  loginResponse: LoginResponse;
  token: string;
  display: boolean;
  @ViewChild("filter") filter: ElementRef;
  @ViewChild("dt") table: Table;

  updateInstDetails: any;
  institutionTypeList: any;
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

    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_institution_types")
      .subscribe(
        (response) => {
          this.institutionTypeList = response;
        },
        (error) => {
          console.error("Error:", error);
          if (error.status === 402) {
            this.router.navigate(["/access"]);
          } else {
            this.router.navigate(["/error"]);
          }
        }
      );

    this.prepareInstDetails();
  }

  clear(table: Table) {
    table.clear();
    this.filter.nativeElement.value = "";
  }

  updateData(data: any) {
    this.router.navigate(["/pages/manage-institution/institution-details"], {
      queryParams: { uuid: data.uuid },
    });
  }

  deleteData(event: Event, data: any) {
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want delete this institution?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.tertiaryVerifyService
          .tertiaryGenericDelete(
            this.sessionService.getLoginObject().token,
            "delete_institution/" + data.id
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                key: "tst",
                severity: "info",
                summary: "Success",
                detail: "Institution successfully deleted",
              });
              this.loadAllInstitutions();
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

  closeDialog(event: Event) {
    console.info("Discard Data...");
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want discard the changes?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.prepareInstDetails();
        this.display = false;
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

  registerInstitution() {
    this.regLoading = true;
    console.info("Request: ", this.updateInstDetails);
    this.tertiaryVerifyService
      .tertiaryGenericPost(
        this.updateInstDetails,
        this.sessionService.getLoginObject().token,
        "register_institution"
      )
      .subscribe(
        (response) => {
          this.regLoading = false;
          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail:
              "Institution successfully submitted, a task will be created for you to complete the registration",
          });
          this.prepareInstDetails();
          this.display = false;
          setTimeout(() => {
            this.loadAllInstitutions();
          }, 2000); // 2 seconds
        },
        (error) => {
          console.error("Error: ", error);
          this.regLoading = false;
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

  prepareInstDetails() {
    this.updateInstDetails = {
      creatorUserID: this.loginResponse.id,
      institutionName: "",
      contactNumber: "",
      email: "",
      alternativeContactNumber: "",
      accreditationNumber: "",
      image: "",
      institutionType: "",
    };
  }

  loadAllInstitutions() {
    this.tertiaryVerifyService
      .tertiaryGenericGet(this.token, "find_all_institution_details")
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
}
