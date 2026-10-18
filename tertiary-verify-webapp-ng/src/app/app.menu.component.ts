import { Component, OnInit } from "@angular/core";
import {
  trigger,
  state,
  style,
  transition,
  animate,
} from "@angular/animations";
import { AppComponent } from "./app.component";
import { AppMainComponent } from "./app.main.component";
import { SessionService } from "./service/session.service";
import { UserDetails } from "./model/UserDetails";
import { Router } from "@angular/router";
import { TertiaryVerifyService } from "./service/tertiary-verify.service";
import { MessageResponse } from "./model/MessageResponse";

@Component({
  selector: "app-menu",
  templateUrl: "./app.menu.component.html",
  animations: [
    trigger("inline", [
      state(
        "hidden",
        style({
          height: "0px",
          overflow: "hidden",
        })
      ),
      state(
        "visible",
        style({
          height: "*",
        })
      ),
      state(
        "hiddenAnimated",
        style({
          height: "0px",
          overflow: "hidden",
        })
      ),
      state(
        "visibleAnimated",
        style({
          height: "*",
        })
      ),
      transition(
        "visibleAnimated => hiddenAnimated",
        animate("400ms cubic-bezier(0.86, 0, 0.07, 1)")
      ),
      transition(
        "hiddenAnimated => visibleAnimated",
        animate("400ms cubic-bezier(0.86, 0, 0.07, 1)")
      ),
    ]),
  ],
})
export class AppMenuComponent implements OnInit {
  model: any[];
  userDetails: UserDetails;
  profileImage: string;

  constructor(
    public app: AppComponent,
    public appMain: AppMainComponent,
    private sessionService: SessionService,
    private router: Router,
    private tertiaryVerifyService: TertiaryVerifyService,
  ) {}

  logout() {
    this.sessionService.clearSessionData();
    this.router.navigate(["/login"]);
  }

  ngOnInit() {
    this.userDetails = this.sessionService.getUserDetailsObject();
    this.sessionService.getUserDetailsChanges().subscribe((details) => {
      this.userDetails = details;
    });
    this.model = this.userDetails.menus;
  }

  onMenuClick(event) {
    this.appMain.onMenuClick(event);
  }
}
