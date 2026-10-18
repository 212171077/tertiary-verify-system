import { Component } from "@angular/core";
import { TertiaryVerifyService } from "../service/tertiary-verify.service";
import { SessionService } from "../service/session.service";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginResponse } from "../model/LoginResponse";
import { ErrorResponse } from "../model/ErrorResponse";
import { CommonUtil } from "../common/CommonUtil";
import { FormControl, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";

@Component({
  selector: "app-login",
  templateUrl: "./app.login.component.html",
  providers: [MessageService, ConfirmationService],
})
export class AppLoginComponent {
  displayForm: string = "login";
  username: string;
  password: string;
  errorMssg: string;
  successMssg: string;
  email: string;
  idNumber: string;
  newPassword: string;
  confirmPassword: string;
  errorResponse: ErrorResponse;
  contactUsRequest: any;
  contactUsErrorMssg: string;
  contactUsSuccessMssg: string;
  isLoading: boolean;

  fullNameControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
    Validators.minLength(1),
    Validators.maxLength(100),
  ]);

  messageControl = new FormControl("", [
    Validators.required,
    Validators.minLength(1),
    Validators.maxLength(500),
  ]);

  emailControl = new FormControl("", [Validators.required, Validators.email]);

  cellNumberControl = new FormControl("", [
    Validators.required
  ]);

  constructor(
    private service: TertiaryVerifyService,
    private sessionService: SessionService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit() {
    this.isLoading = false;
    this.activatedRoute.queryParams.subscribe((params) => {
      const dispalyFormParam = params["dispaly-form"];
      if (dispalyFormParam !== null && dispalyFormParam !== undefined) {
        this.displayForm = dispalyFormParam;
      }
    });

    this.contactUsRequest = {
      'cellNumber': '',
      'email': '',
      'fullName': '',
      'message': ''
    };
  }

  sendContactUsMessage(){
    this.service
    .tertiaryGenericPost(
      this.contactUsRequest,
      null,
      "contact_us"
    )
    .subscribe(
      (response) => {
       this.contactUsSuccessMssg ="Message send successfully, Our team will initiate communication should the need arise. thank you.";
      },
      (error) => {
        console.error("Error: ", error);
        this.contactUsErrorMssg = "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";
      }
    );

  }

  switchForm(value) {
    this.errorMssg = null;
    this.successMssg = null;
    this.errorResponse = null;
    this.displayForm = value;
  }

  login() {
    this.isLoading = true;
    this.errorMssg = null;
    this.sessionService.clearSessionData();
    this.service.login(this.username, this.password).subscribe(
      (response) => {
        this.sessionService.setLoginObject(response);
        var loginResponse: LoginResponse = response as LoginResponse;
        if (loginResponse.changePassword) {
          this.displayForm = "change-password";
          this.storeUserDetailsInSession(
            loginResponse.id,
            loginResponse.token,
            false
          );
        } else {
          this.storeUserDetailsInSession(
            loginResponse.id,
            loginResponse.token,
            true
          );
        }
        this.isLoading = false;
      },
      (error) => {
        if (error.status == 401) {
          this.errorMssg =
            "Invalid login details. Please check your username and password and try again.";
        } else if (error.status == 0) {
          this.errorMssg =
            "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";
        } else {
          console.error("Login error:", error);
          this.errorMssg =
            "Sorry, our service is currently unavailable, unkown error.";
        }
        this.isLoading = false;
      }
    );
  }

  storeUserDetailsInSession(
    id: number,
    token: string,
    navToDashboard: boolean
  ) {
    this.service.findUserDetailsById(id, token).subscribe(
      (response) => {
        this.sessionService.setUserDetailsObject(response);
        if (navToDashboard) {
          this.router.navigate(["/"]);
        }
      },
      (error) => {
        console.error("Fetch user details error:", error);
        this.router.navigate(["/error"]);
      }
    );
  }

  resetPassword() {
    this.errorMssg = null;
    this.service.resetPassword(this.email, this.idNumber).subscribe(
      (response) => {
        this.successMssg =
          "Your password has been successfully reset. An email has been sent to the registered email address associated with your account containing the new login details. Please check your email inbox.";
      },
      (error) => {
        if (error.status == 400) {
          this.errorMssg = error.error.message;
        } else if (error.status == 0) {
          console.error("Login error:", error);
          this.errorMssg =
            "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";
        }
      }
    );
  }

  changePassword() {
    this.errorMssg = null;
    this.errorResponse = null;
    this.service
      .changePassword(
        this.newPassword,
        this.confirmPassword,
        this.sessionService.getLoginObject().id,
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.router.navigate(["/"]);
        },
        (error) => {
          console.error("Error Response:", error);
          if (error.status == 400) {
            this.errorResponse = CommonUtil.buildErrorResponse(error);
            if (this.errorResponse == null) {
              this.errorMssg = error.error.message;
            }
          } else if (error.status == 403) {
            this.router.navigate(["/access"]);
          } else {
            console.error("Login error:", error);
            this.errorMssg =
              "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";
          }
        }
      );
  }
}
