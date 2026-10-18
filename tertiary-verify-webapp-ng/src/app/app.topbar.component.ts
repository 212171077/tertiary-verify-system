import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { AppComponent } from "./app.component";
import { AppMainComponent } from "./app.main.component";
import { TertiaryVerifyService } from "./service/tertiary-verify.service";
import { SessionService } from "./service/session.service";

@Component({
  selector: "app-topbar",
  templateUrl: "./app.topbar.component.html",
})
export class AppTopBarComponent implements OnInit {
  tasks: any;
  animateShake = false;
  totalTasks = 0;
  constructor(
    public app: AppComponent,
    public appMain: AppMainComponent,
    private tertiaryVerifyService: TertiaryVerifyService,
    private sessionService: SessionService,
    private router: Router
  ) {}

  logout(event?: Event) {
    event?.preventDefault();
    this.sessionService.clearSessionData();
    this.router.navigate(["/login"]);
  }

  ngOnInit(): void {
    this.tertiaryVerifyService
      .startPollingTasks(
        this.sessionService.getLoginObject().id,
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.tasks = response;
          if (this.tasks != null && this.tasks.length > 0) {
            this.totalTasks = this.tasks.length;
            this.animateShake = true;
            setTimeout(() => {
              this.animateShake = false;
            }, 1000);
          } else {
            this.totalTasks = 0;
          }
        },
        (error) => {
          console.error("Polling Error:", error);
        }
      );
  }
}
