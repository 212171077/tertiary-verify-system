import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Renderer2,
  ViewChild,
} from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { Table } from "primeng/table";
import { DropDown } from "src/app/model/DropDown";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-users",
  templateUrl: "./users.component.html",
  providers: [MessageService, ConfirmationService],
})
export class UsersComponent {
  loading: boolean = true;
  userDetailsLoading: boolean = false;
  userRolesLoading: boolean = false;
  loginResponse: LoginResponse;
  token: string;
  userDetails: any;
  currentUserDetails: any;
  titleList: any;
  genderList: any;
  dataList: any;
  sourceRoles: any;
  roleList: any;
  targetRoles: any;
  selectedUserId: number;
  display: boolean;
  @ViewChild("filter") filter: ElementRef;
  @ViewChild("dt") table: Table;

  titleControl = new FormControl("", [Validators.required]);
  genderControl = new FormControl("", [Validators.required]);
  nameControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
    Validators.minLength(1),
    Validators.maxLength(50),
  ]);

  surnameControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
    Validators.minLength(1),
    Validators.maxLength(50),
  ]);

  idNumberControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^[0-9]{13}$/), // 13 digits only
    this.southAfricanIdValidator(), // Custom ID validator
  ]);

  emailControl = new FormControl("", [Validators.required, Validators.email]);

  cellNumberControl = new FormControl("", [
    Validators.required
  ]);

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private messageService: MessageService,
    private router: Router,
    private confirmationService: ConfirmationService,
    private renderer: Renderer2,
    private elementRef: ElementRef,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.userDetails = {
      title: "",
      gender: "",
    };

    this.currentUserDetails = this.sessionService.getUserDetailsObject();
    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;
    this.titleList = this.currentUserDetails.titles;
    this.genderList = this.currentUserDetails.genders;
    this.loadAllUsers();
    this.clearUserData();
    this.loadAllRoles();
  }

  updateData(data: any) {
    this.scrollToComponent();
    this.userDetails = {
      title: this.findSelectedOption(data.titles),
      gender: this.findSelectedOption(data.genders),
      id: data.id,
      name: data.name,
      surname: data.surname,
      email: data.email,
      cellNumber: data.cellNumber,
      idNumber: data.rsaId,
    };
  }

  showRolesDialog(data: any) {
    this.display = true;
    this.targetRoles = data.roles;
    this.sourceRoles = this.roleList.filter(
      (role) =>
        !this.targetRoles.some((targetRole) => targetRole.id === role.id)
    );
    this.selectedUserId = data.id;
  }

  clearUserData() {
    this.userDetails = {
      title: "",
      gender: "",
      id: "",
      name: "",
      surname: "",
      email: "",
      cellNumber: "",
      idNumber: "",
    };
    this.titleControl = new FormControl("", [Validators.required]);
    this.genderControl = new FormControl("", [Validators.required]);
    this.nameControl = new FormControl("", [
      Validators.required,
      Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
      Validators.minLength(1),
      Validators.maxLength(50),
    ]);

    this.surnameControl = new FormControl("", [
      Validators.required,
      Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
      Validators.minLength(1),
      Validators.maxLength(50),
    ]);

    this.idNumberControl = new FormControl("", [
      Validators.required,
      Validators.pattern(/^[0-9]{13}$/), // 13 digits only
      this.southAfricanIdValidator(), // Custom ID validator
    ]);

    this.emailControl = new FormControl("", [
      Validators.required,
      Validators.email,
    ]);

    this.cellNumberControl = new FormControl("", [
      Validators.required
    ]);
  }

  findSelectedOption(options: DropDown[] | null | undefined) {
    if (!options || options.length === 0) {
      return null;
    }

    let selected = null;
    options.forEach((item) => {
      if (item && item.selected) {
        selected = item;
      }
    });
    return selected;
  }


  saveUserData() {
    this.userDetailsLoading = true;


    console.info("User Details Request: ", JSON.stringify(this.userDetails));
    this.tertiaryVerifyService
      .registerUser(
        this.userDetails,
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.userDetailsLoading = false;

          let msg = "Registration successful! The user will receive an email containing their login details.";

          if (this.userDetails.id) {
            msg = "Profile updated successfully! The user’s details have been saved.";
          }

          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail: msg,
          });

          this.clearUserData();
          this.loadAllUsers();
        },
        (error) => {
          console.error("Error: ", error);
          this.userDetailsLoading = false;
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

  updateRoles() {

    this.userRolesLoading = true;
    let request = {
      userId: this.selectedUserId,
      roleIds: this.targetRoles.map((role: any) => role.id)
    };

    console.info("Update Roles Request: ", JSON.stringify(request));

    this.tertiaryVerifyService
      .updateUserRoles(
        request,
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.userRolesLoading = false;

          let msg = "User roles updated successfully!";

          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail: msg,
          });
           this.loadAllRoles();
           this.display =false;
        },
        (error) => {
          console.error("Error: ", error);
          this.userRolesLoading = false;
          this.display = false;
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

  deleteUser(event: Event, data: any) {
    console.info("Delete Data: ", data);
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want delete this user?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.tertiaryVerifyService
          .tertiaryGenericDelete(
            this.sessionService.getLoginObject().token,
            "delete_user_by_id/" + data.id
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                key: "tst",
                severity: "warn",
                summary: "Deleted",
                detail: "The user has been deleted.",
              });
              this.loadAllUsers();
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
        //Rejected
      },
    });
  }

  loadAllUsers() {
    this.tertiaryVerifyService
      .userGenericGet(this.token, "find_all_user_details")
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

  loadAllRoles() {
    this.tertiaryVerifyService
      .userGenericGet(this.token, "find_all_roles")
      .subscribe(
        (response) => {
          this.roleList = response;
          this.cdr.markForCheck();
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

  scrollToComponent() {
    const element = this.elementRef.nativeElement.querySelector("#mainForm");
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  }

  southAfricanIdValidator() {
    return (control: FormControl) => {
      const idNumber = control.value;

      if (!idNumber) {
        return null; // If no value, don't validate
      }

      // Extracting components from the ID number
      const dobPart = idNumber.substring(0, 6);
      const genderPart = idNumber.substring(6, 10);
      const citizenPart = idNumber.charAt(10);
      const checksum = idNumber.charAt(12);

      // Check the date of birth part (YYMMDD)
      const dobYear = parseInt(dobPart.substring(0, 2), 10);
      const dobMonth = parseInt(dobPart.substring(2, 4), 10);
      const dobDay = parseInt(dobPart.substring(4, 6), 10);

      if (
        dobYear < 0 ||
        dobYear > 99 ||
        dobMonth < 1 ||
        dobMonth > 12 ||
        dobDay < 1 ||
        dobDay > 31
      ) {
        return { southAfricanId: true }; // Invalid date of birth
      }

      // Check the gender part
      const gender = parseInt(genderPart, 10);
      if (gender < 0 || gender > 9999) {
        return { southAfricanId: true }; // Invalid gender
      }

      // Check the citizenship part
      const citizen = parseInt(citizenPart, 10);
      if (![0, 1, 2].includes(citizen)) {
        return { southAfricanId: true }; // Invalid citizenship status
      }

      // Check the Luhn algorithm for checksum
      if (!this.isLuhnValid(idNumber)) {
        return { southAfricanId: true }; // Invalid checksum
      }

      return null; // ID number is valid
    };
  }

  isLuhnValid(value: string): boolean {
    let sum = 0;
    let alternate = false;

    for (let i = value.length - 1; i >= 0; i--) {
      let digit = parseInt(value.charAt(i), 10);

      if (alternate) {
        digit *= 2;
        if (digit > 9) {
          digit -= 9;
        }
      }

      sum += digit;
      alternate = !alternate;
    }

    return sum % 10 === 0;
  }
}
