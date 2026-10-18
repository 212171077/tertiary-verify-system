import { Injectable } from "@angular/core";
import { LoginResponse } from "../model/LoginResponse";
import jwt_decode, { JwtPayload } from "jwt-decode";
import { Observable, Subject } from "rxjs";
import { UserDetails } from "../model/UserDetails";
import { InstitutionDetails } from "../model/InstitutionDetails";

@Injectable({
  providedIn: "root",
})
export class SessionService {
 
  private timer: any;
  private tokenExpiredSubject: Subject<boolean> = new Subject<boolean>();

  private userDetailsSubject = new Subject<any>();

  constructor() {}

  getUserDetailsChanges(): Observable<any> {
    return this.userDetailsSubject.asObservable();
  }

  setLoginObject(loginObject: any): void {
    sessionStorage.setItem("loginObject", JSON.stringify(loginObject));
    this.startTokenExpiryTimer();
    console.log("Done Setting Login Object");
  }

  getLoginObject(): any {
    const loginObject = sessionStorage.getItem("loginObject");
    var loginResponse: LoginResponse = JSON.parse(loginObject) as LoginResponse;
    return loginObject ? loginResponse : null;
  }

  clearLoginObject(): void {
    sessionStorage.removeItem("loginObject");
  }

  setUserDetailsObject(userDetails: any): void {
    sessionStorage.setItem("userDetails", JSON.stringify(userDetails));
    this.userDetailsSubject.next(userDetails);
    console.log("Done Setting User Details Object");
  }

  getUserDetailsObject(): any {
    const userDetailsJson = sessionStorage.getItem("userDetails");
    var userDetails: UserDetails = JSON.parse(userDetailsJson) as UserDetails;
    return userDetailsJson ? userDetails : null;
  }

  clearUserDetailsObject(): void {
    sessionStorage.removeItem("userDetails");
  }

  getTokenExpiredObservable(): Observable<boolean> {
    return this.tokenExpiredSubject.asObservable();
  }

  private startTokenExpiryTimer(): boolean {
    this.stopTokenExpiryTimer();

    const loginResponse = this.getLoginObject();
    if (loginResponse != null) {
      const decodedToken = jwt_decode<JwtPayload>(loginResponse.token);
      const tokenExpirationDate = decodedToken.exp;
      const currentTime = Math.floor(Date.now() / 1000);
      const timeToExpiry = (tokenExpirationDate - currentTime) * 1000;

      const oneMinuteInMillis = 1 * 60 * 1000; // 1 minute in milliseconds

      if (timeToExpiry <= oneMinuteInMillis) {
        return true;
      }

      this.timer = setTimeout(() => {
        this.tokenExpiredSubject.next(true);
      }, timeToExpiry - oneMinuteInMillis);
    }

    return false;
  }

  private stopTokenExpiryTimer(): void {
    if (this.timer) {
      clearTimeout(this.timer);
    }
  }

  isTokenExpired(): boolean {
    const loginResponse = this.getLoginObject();
    if (!loginResponse || !loginResponse.token) {
      return true;
    }

    const decodedToken = jwt_decode<JwtPayload>(loginResponse.token);
    const tokenExpirationDate = decodedToken.exp;
    const currentTime = Math.floor(Date.now() / 1000);

    console.log("Token Expiration Date: ", tokenExpirationDate);
    console.log("Current Time: ", currentTime);

    return tokenExpirationDate < currentTime;
  }

  clearSessionData(): void {
    sessionStorage.removeItem("userDetails");
    sessionStorage.removeItem("loginObject");
  }

}
