import { Component, OnInit } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ConfirmationService, MessageService } from "primeng/api";
import { DropDown } from "src/app/model/DropDown";
import { MessageResponse } from "src/app/model/MessageResponse";
import { UserDetails } from "src/app/model/UserDetails";
import { SessionService } from "src/app/service/session.service";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";

@Component({
  selector: "app-address-details",
  templateUrl: "./address-details.component.html",
  providers: [MessageService, ConfirmationService],
})
export class AddressDetailsComponent implements OnInit {
  userDetails: UserDetails;
  residentialAddress: any;
  sameAsResAddress: any;
  sameAsResAddressOptions: any;
  residentialAddressLoading = false;
  institutionDetails: any;
  loading: boolean = true;
  addressList: any;
  updateLoading: boolean;
  cityList: any;
  provinceList: any;
  token: string;

  /**
   * Residential Address Controllers/validators
   * */
  ressAddressLine1Control = new FormControl("", [Validators.required]);
  ressAddressLine2Control = new FormControl("", [Validators.required]);
  ressAddressLine3Control = new FormControl("", [Validators.required]);
  ressAddressexamCenterNumControl = new FormControl("", [Validators.required]);
  ressAddressPostalCodeControl = new FormControl("", [
    Validators.required,
    Validators.pattern("\\d{4}"),
  ]);
  ressAddressCityControl = new FormControl("", [Validators.required]);
  ressAddressProvinceControl = new FormControl("", [Validators.required]);

  serviceUnavailableMssg =
    "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router
  ) {}

  ngOnInit() {

    this.token = this.sessionService.getLoginObject().token;
    
    this.loadCities();
    this.loadProvince();

    this.userDetails = this.sessionService.getUserDetailsObject();
    //this.sameAsResAddress = "NO";
    this.sameAsResAddressOptions = [
      { name: "NO", value: 1 },
      { name: "YES", value: 2 },
    ];
    this.sameAsResAddress = { name: "", value: 0 };

    this.tertiaryVerifyService.manageInstDetails$.subscribe((data) => {
      this.institutionDetails = data;
      this.clearAddressDetails();
      this.buildAddressList(this.institutionDetails.addressList);

      this.loading = false;
    });
  }

  loadCities() {
    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_cities")
      .subscribe(
        (response) => {
          this.cityList = response;
          this.loading = false;
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
  }

  loadProvince() {
    this.tertiaryVerifyService
      .lookupGenericGet(this.token, "find_all_provinces")
      .subscribe(
        (response) => {
          this.provinceList = response;
          this.loading = false;
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

  updateResidentialAddress() {
    this.residentialAddressLoading = true;

    this.tertiaryVerifyService
      .updateAddresDetails(
        this.residentialAddress,
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.residentialAddressLoading = false;
          this.showSuccessViaToast(
            "Institution residential address details have been successfully updated."
          );
        },
        (error) => {
          console.error("Error: ", error);
          this.residentialAddressLoading = false;
          this.showErrorViaToast(this.serviceUnavailableMssg);
        }
      );
  }

  discardData(event: Event, addressType: string) {
    console.info("Discard Data...");
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message: "Are you sure that you want discard the changes?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        if (this.residentialAddress.id) {
          this.addressList.push(this.residentialAddress);
        }
        this.clearAddressDetails();
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

  showErrorViaToast(mssg: string) {
    this.messageService.add({
      key: "tst",
      severity: "error",
      summary: "Error Message",
      detail: mssg,
    });
  }

  showSuccessViaToast(mssg: string) {
    this.messageService.add({
      key: "tst",
      severity: "success",
      summary: "Success Message",
      detail: mssg,
    });
  }

  saveAddress() {
    this.updateLoading = true;
    this.tertiaryVerifyService
      .tertiaryGenericPost(
        this.residentialAddress,
        this.sessionService.getLoginObject().token,
        "save_institution_address"
      )
      .subscribe(
        (response) => {
          this.updateLoading = false;
          const messageResponse: MessageResponse = JSON.parse(
            JSON.stringify(response)
          ) as MessageResponse;
          this.residentialAddress.id = messageResponse.data.addressId;
          this.addressList.push(this.residentialAddress);
          this.clearAddressDetails();

          this.messageService.add({
            key: "tst",
            severity: "info",
            summary: "Success",
            detail: "Address successfully updated",
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
  }

  nextPage() {
    this.router.navigate(["/pages/manage-institution/courses-details"]);
  }

  updateAddressData(data: any) {
    this.residentialAddress = {
      id: data.id,
      institutionId: this.institutionDetails.id,
      addressLine1: data.addressLine1,
      addressLine2: data.addressLine2,
      addressLine3: data.addressLine3,
      addressLine4: data.addressLine4,
      code: data.code,
      examinationNumber: data.examinationNumber,
      city: data.city,
      province: data.province,
    };

    this.removeAddressFromList(data);
  }

  deleteAddress(event: Event, data: any) {
    this.confirmationService.confirm({
      key: "confirm2",
      target: event.target,
      message:
        "Only address that are not linked to a course can be deleted. Are you sure that you want delete this address?",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.tertiaryVerifyService
          .tertiaryGenericDelete(
            this.sessionService.getLoginObject().token,
            "delete_address/" + data.id
          )
          .subscribe(
            (response) => {

              console.info("response: ", JSON.stringify(response ));

              this.messageService.add({
                key: "tst",
                severity: "info",
                summary: "Success",
                detail: "Address successfully deleted",
              });
              this.removeAddressFromList(data);
            },
            (error) => {
              console.error("Error: ", error);
              if (error.status == 422) {
                this.messageService.add({
                  key: "tst",
                  severity: "warn",
                  summary: "Error",
                  detail: "Address is in use and cannot be deleted.",
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
      },
      reject: () => {
        //Address not deleted
      },
    });
  }

  clearAddressDetails() {
    this.residentialAddress = {
      institutionId: this.institutionDetails.id,
      addressLine1: "",
      addressLine2: "",
      addressLine3: "",
      addressLine4: "",
      code: "",
      examinationNumber: "",
      city: null,
      province: null,
    };

    this.ressAddressLine1Control = new FormControl("", [Validators.required]);
    this.ressAddressLine2Control = new FormControl("", [Validators.required]);
    this.ressAddressLine3Control = new FormControl("", [Validators.required]);
    this.ressAddressexamCenterNumControl = new FormControl("", [Validators.required]);
    this.ressAddressPostalCodeControl = new FormControl("", [
      Validators.required,
      Validators.pattern("\\d{4}"),
    ]);
    this.ressAddressCityControl = new FormControl("", [Validators.required]);
    this.ressAddressProvinceControl = new FormControl("", [Validators.required]);
  
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

  removeAddressFromList(data: any) {
    const addressIndex = this.addressList.findIndex(
      (address) => address.id === data.id
    );
    if (addressIndex !== -1) {
      this.addressList.splice(addressIndex, 1);
    }
  }
}
