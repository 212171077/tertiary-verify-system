import { Component, OnInit } from "@angular/core";
import { PrimeNGConfig } from "primeng/api";
import { SessionService } from "./service/session.service";
import { Router } from "@angular/router";
import { TertiaryVerifyService } from "./service/tertiary-verify.service";
import { LoginResponse } from "./model/LoginResponse";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
})
export class AppComponent implements OnInit {
  layoutMode = "static"; //static overlay horizontal slim

  lightMenu = true; //true or false

  topbarColor = "layout-topbar-teal";

  inlineUser = true; //true or false

  isRTL = false; //true or false (side bar)

  inputStyle = "filled"; //outlined or filled

  ripple = true;

  showPopup = false;

  counter: number = 60;

  intervalId: any;

  constructor(
    private primengConfig: PrimeNGConfig,
    private sessionService: SessionService,
    private router: Router,
    private tertiaryVerifyService: TertiaryVerifyService
  ) {}

  ngOnInit() {
    this.primengConfig.ripple = true;

    this.sessionService
      .getTokenExpiredObservable()
      .subscribe((expired: boolean) => {
        this.showPopup = expired;
        if (expired) {
          this.startCounter();
        }
        console.log("******************* Expired: ", expired);
      });
  }

  sessionExpiredContinue() {
    this.stopCounter();
    const loginResponse = this.sessionService.getLoginObject();
    this.tertiaryVerifyService
      .refreshTokenExpiryTime(loginResponse.email, loginResponse.token)
      .subscribe(
        (response) => {
          console.log("Login successful:", response);
          this.sessionService.setLoginObject(response);
          this.showPopup = false;
        },
        (error) => {
          console.error("Login error:", error);
          this.router.navigate(["/login"]);
        }
      );
  }

  sessionExpiredExit() {
    this.showPopup = false;
    this.stopCounter();
    this.sessionService.clearLoginObject();
    this.router.navigate(["/login"]);
    
  }

  startCounter() {
    this.counter = 60;
    this.intervalId = setInterval(() => {
      this.counter--;
      if (this.counter === 0) {
        this.stopCounter();
        this.sessionExpiredExit();
      }
    }, 1000);
  }

  stopCounter() {
    clearInterval(this.intervalId);
  }

  getAnimationDuration(): string {
    return this.counter + "s";
  }
}
