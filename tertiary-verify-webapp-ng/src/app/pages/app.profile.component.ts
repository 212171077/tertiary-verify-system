import { Component } from "@angular/core";
import { MessageService } from "primeng/api";
import { UserDetails } from "../model/UserDetails";
import { SessionService } from "../service/session.service";
import { FormControl, Validators } from "@angular/forms";
import { DropDown } from "../model/DropDown";
import { TertiaryVerifyService } from "../service/tertiary-verify.service";

@Component({
  selector: "app-profile",
  templateUrl: "./app.profile.component.html",
  providers: [MessageService],
})
export class ProfileComponent {
  userDetails: UserDetails;
  updatedUser: any;
  profileDraft: any;
  loginDetails: any;
  displayProfileDetails = false;
  displayLoginDetails = false;
  userDetailsLoading = false;
  passwordLoading = false;
  uploadingImage = false;
  serviceUnavailableMssg =
    "Sorry, our service is currently unavailable. Our team is working to fix the issue. Please try again later.";
  readonly maxProfileImageSize = 5 * 1024 * 1024;

  nameControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
    Validators.minLength(2),
    Validators.maxLength(50),
  ]);
  surnameControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(?=.*[a-zA-Z])(?!\d+$).{2,}$/),
    Validators.minLength(2),
    Validators.maxLength(50),
  ]);
  emailControl = new FormControl("", [Validators.required, Validators.email]);
  cellNumberControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(\+27|0)[6-8]\d{8}$/),
  ]);
  titleControl = new FormControl<DropDown | null>(null, [Validators.required]);
  genderControl = new FormControl<DropDown | null>(null, [Validators.required]);
  oldPasswordControl = new FormControl("", [Validators.required]);
  newPasswordControl = new FormControl("", [
    Validators.required,
    Validators.pattern(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$/),
  ]);
  confirmPasswordControl = new FormControl("", [Validators.required]);

  constructor(
    private sessionService: SessionService,
    private tertiaryVerifyService: TertiaryVerifyService,
    private messageService: MessageService,
  ) {}

  ngOnInit() {
    this.userDetails = this.sessionService.getUserDetailsObject();
    this.loginDetails = this.sessionService.getLoginObject();
    this.updatedUser = {
      cellNumber: this.userDetails.cellNumber,
      email: this.userDetails.email,
      id: this.userDetails.id,
      name: this.userDetails.name,
      surname: this.userDetails.surname,
      title: this.findSelectedOption(this.userDetails.titles),
      gender: this.findSelectedOption(this.userDetails.genders),
    };
    this.profileDraft = { ...this.updatedUser };
  }

  findSelectedOption(options: DropDown[] | null | undefined) {
    if (!options || options.length === 0) {
      return null;
    }

    return options.find((item) => item?.selected) || null;
  }


  openProfileDetails() {
    this.profileDraft = { ...this.updatedUser };
    this.nameControl.reset(this.profileDraft.name || "");
    this.surnameControl.reset(this.profileDraft.surname || "");
    this.emailControl.reset(this.profileDraft.email || "");
    this.cellNumberControl.reset(this.profileDraft.cellNumber || "");
    this.genderControl.reset(this.profileDraft.gender || null);
    this.titleControl.reset(this.profileDraft.title || null);
    this.displayProfileDetails = true;
  }

  isProfileFormValid(): boolean {
    return (
      this.nameControl.valid &&
      this.surnameControl.valid &&
      this.emailControl.valid &&
      this.cellNumberControl.valid &&
      this.genderControl.valid &&
      this.titleControl.valid
    );
  }

  openLoginDetails() {
    this.oldPasswordControl.reset("");
    this.newPasswordControl.reset("");
    this.confirmPasswordControl.reset("");
    this.displayLoginDetails = true;
  }

  get passwordsMatch(): boolean {
    return this.newPasswordControl.value === this.confirmPasswordControl.value;
  }

  isPasswordFormValid(): boolean {
    return (
      this.oldPasswordControl.valid &&
      this.newPasswordControl.valid &&
      this.confirmPasswordControl.valid &&
      this.passwordsMatch
    );
  }

  updatePassword() {
    if (!this.isPasswordFormValid()) {
      this.oldPasswordControl.markAsTouched();
      this.newPasswordControl.markAsTouched();
      this.confirmPasswordControl.markAsTouched();
      return;
    }

    const request = {
      userId: this.userDetails.id,
      oldPassword: this.oldPasswordControl.value,
      newPassword: this.newPasswordControl.value,
      confirmPassword: this.confirmPasswordControl.value,
    };

    this.passwordLoading = true;
    this.tertiaryVerifyService
      .updatePassword(request, this.loginDetails.token)
      .subscribe(
        () => {
          this.passwordLoading = false;
          this.displayLoginDetails = false;
          this.oldPasswordControl.reset("");
          this.newPasswordControl.reset("");
          this.confirmPasswordControl.reset("");
          this.showSuccessViaToast("Your password has been successfully updated.");
        },
        (error) => {
          console.error("Error updating password:", error);
          this.passwordLoading = false;
          this.showErrorViaToast(
            error?.error?.message || this.serviceUnavailableMssg
          );
        }
      );
  }

  onProfileImageChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = "";

    if (!file) {
      return;
    }

    if (!["image/jpeg", "image/png", "image/gif"].includes(file.type)) {
      this.showErrorViaToast("Choose a JPEG, PNG, or GIF image.");
      return;
    }

    if (file.size > this.maxProfileImageSize) {
      this.showErrorViaToast("Profile images must be 5 MB or smaller.");
      return;
    }

    this.uploadingImage = true;
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result !== "string") {
        this.uploadingImage = false;
        this.showErrorViaToast("The selected image could not be read.");
        return;
      }

      const imageData = reader.result;
      const image = imageData.substring(imageData.indexOf(",") + 1);
      this.tertiaryVerifyService
        .updateProfileImage(image, file.type, this.loginDetails.token)
        .subscribe(
          (response: any) => {
            const uploadedImage =
              typeof response?.data === "string" ? response.data : imageData;
            this.userDetails = {
              ...this.userDetails,
              imageUrl: uploadedImage,
            };
            this.sessionService.setUserDetailsObject(this.userDetails);
            this.uploadingImage = false;
            this.showSuccessViaToast("Your profile image has been updated.");
          },
          (error) => {
            console.error("Error updating profile image:", error);
            this.uploadingImage = false;
            this.showErrorViaToast(
              error?.error?.message || this.serviceUnavailableMssg
            );
          }
        );
    };
    reader.onerror = () => {
      this.uploadingImage = false;
      this.showErrorViaToast("The selected image could not be read.");
    };
    reader.readAsDataURL(file);
  }

  updateUserDetails() {
    if (!this.isProfileFormValid()) {
      this.nameControl.markAsTouched();
      this.surnameControl.markAsTouched();
      this.emailControl.markAsTouched();
      this.cellNumberControl.markAsTouched();
      this.genderControl.markAsTouched();
      this.titleControl.markAsTouched();
      return;
    }

    const request = {
      id: this.userDetails.id,
      name: this.profileDraft.name,
      surname: this.profileDraft.surname,
      email: this.profileDraft.email,
      cellNumber: this.profileDraft.cellNumber,
      title: this.profileDraft.title,
      gender: this.profileDraft.gender,
    };

    this.userDetailsLoading = true;
    this.tertiaryVerifyService
      .updateUserDetails(
        request,
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.userDetailsLoading = false;
          this.updatedUser = { ...this.updatedUser, ...request };
          this.userDetails = {
            ...this.userDetails,
            name: request.name,
            surname: request.surname,
            email: request.email,
            cellNumber: request.cellNumber,
            titles: this.userDetails.titles?.map((title) => ({
              ...title,
              selected: title.id === request.title.id,
            })),
            genders: this.userDetails.genders?.map((gender) => ({
              ...gender,
              selected: gender.id === request.gender.id,
            })),
          };
          this.sessionService.setUserDetailsObject(this.userDetails);
          this.displayProfileDetails = false;
          this.showSuccessViaToast(
            "Your user details have been successfully updated."
          );
        },
        (error) => {
          console.error("Error: ", error);
          this.userDetailsLoading = false;
          this.showErrorViaToast(this.serviceUnavailableMssg);
        }
      );
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

}
