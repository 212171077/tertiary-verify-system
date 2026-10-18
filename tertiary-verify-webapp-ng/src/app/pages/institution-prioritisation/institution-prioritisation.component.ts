import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DropDown } from 'src/app/model/DropDown';
import { LoginResponse } from 'src/app/model/LoginResponse';
import { SessionService } from 'src/app/service/session.service';
import { TertiaryVerifyService } from 'src/app/service/tertiary-verify.service';

@Component({
  selector: 'app-institution-prioritisation',
  templateUrl: './institution-prioritisation.component.html',
  styleUrls: ["../../../assets/demo/badges.scss"],
  providers: [MessageService, ConfirmationService],

})
export class InstitutionPrioritisationComponent {
  token: string;
  uuid: string;
  loginResponse: LoginResponse;
  institutionDetails: any;
  loading: boolean = true;
  title: string;
  message:string;
  isPrioritise: boolean;
  taskId:number;

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {}


  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.uuid = params["uuid"];
      this.taskId = params["task-id"];
      if(params["action"] == 'prioritise'){
        this.title= "Prioritise Institution";
        this.message = "By marking this institution as a priority, you're ensuring it gets the best possible attention and resources. This means it will receive premium support and tailored solutions";
        this.isPrioritise = true;
      }else {
        this.title= "De-prioritise Institution";
        this.message = "By marking this institution as a de-priority means it will no longer receive preferential treatment but can still access standard services.";
        this.isPrioritise = false;
      }
    });

    this.loginResponse = this.sessionService.getLoginObject();
    this.token = this.loginResponse.token;

    this.tertiaryVerifyService
      .tertiaryGenericGet(this.token, "find_institution_by_uuid/" + this.uuid)
      .subscribe(
        (response) => {
         this.loading = false;
              this.institutionDetails = response;
              console.info(this.institutionDetails);
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

  approveInstPrioritisation(event: Event) {
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure you want approve?",
      accept: () => {
        const request = {
          taskId: this.taskId,
          userId: this.sessionService.getLoginObject().id,
          institutionId: this.institutionDetails.id,
          prioritise: this.isPrioritise,
        };

        this.tertiaryVerifyService
          .tertiaryGenericPost(
            request,
            this.sessionService.getLoginObject().token,
            "approve_prioritisation"
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                severity: "info",
                summary: "Success",
                detail: "Institution prioritisation approval successful",
              });
              this.router.navigate(["/"]);
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

  rejectInstPrioritisation(event: Event) {
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure you want reject?",
      accept: () => {
        const request = {
          taskId: this.taskId,
          userId: this.sessionService.getLoginObject().id,
          institutionId: this.institutionDetails.id,
          prioritise: this.isPrioritise,
        };


        this.tertiaryVerifyService
          .tertiaryGenericPost(
            request,
            this.sessionService.getLoginObject().token,
            "reject_prioritisation"
          )
          .subscribe(
            (response) => {
              this.messageService.add({
                severity: "info",
                summary: "Success",
                detail: "Institution prioritisation rejecation successful",
              });
              this.router.navigate(["/"]);
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
