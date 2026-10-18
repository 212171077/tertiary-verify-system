import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { MenuItem } from "primeng/api";
import { LoginResponse } from "src/app/model/LoginResponse";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-manage-institution",
  templateUrl: "./manage-institution.component.html",
})
export class ManageInstitutionComponent {
  routeItems: MenuItem[];
  activeIndex: number = 0;
  loginResponse: LoginResponse;
  token: string;
  uuid: string;

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private router: Router,
    private activatedRoute: ActivatedRoute
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
          console.info("Setting Institution Details Data to Session..");
          this.tertiaryVerifyService.setManageInstDetails(response);
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

    this.routeItems = [
      {
        label: "Institution Details",
        routerLink: "/pages/manage-institution/institution-details",
      },
      {
        label: "Address Details",
        routerLink: "/pages/manage-institution/address-details",
      },
      {
        label: "Courses Details",
        routerLink: "/pages/manage-institution/courses-details",
      },
      {
        label: "Confirm Details",
        routerLink: "/pages/manage-institution/confirm-details",
      },
    ];
  }
  chan() {
    this.activeIndex += 1;
    console.info("Active Index: "+this.activeIndex);
  }

}
