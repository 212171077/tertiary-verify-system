import { Injectable } from "@angular/core";
import { HttpHeaders, HttpClient } from "@angular/common/http";
import { Observable, BehaviorSubject, switchMap, timer } from "rxjs";
import { DatePipe } from "@angular/common";

@Injectable({
  providedIn: "root",
})

export class TertiaryVerifyService {
  //private baseUrl = "http://localhost:8081";
  private baseUrl="https://tertiaryverify.dedicated.co.za:8082";
  private tasksPollInterval = 5000; //5 Seconds
  private dashboardPollInterval = 30000; //30 Seconds
  private authHostName = this.baseUrl + "/api/tertiary_verify/auth/";
  private userHostName = this.baseUrl + "/api/tertiary_verify/user/";
  private lookupHostName = this.baseUrl + "/api/tertiary_verify/lookups/";
  private tertiaryHostName = this.baseUrl + "/api/tertiary_verify/";

  private manageInstDetailsDataSubject: BehaviorSubject<string[]> =
    new BehaviorSubject<string[]>([]);
  public manageInstDetails$: Observable<any> =
    this.manageInstDetailsDataSubject.asObservable();

  constructor(private http: HttpClient,private datePipe: DatePipe) {}

  setManageInstDetails(data: any): void {
    this.manageInstDetailsDataSubject.next(data);
  }

  login(username: string, password: string) {
    const requestBody = {
      username: username,
      password: password,
    };
    return this.http.post(this.authHostName + "login", requestBody);
  }

  resetPassword(email: string, idNumber: string) {
    const requestBody = {
      email: email,
      idNumber: idNumber,
    };
    return this.http.post(this.authHostName + "forgot_password", requestBody);
  }

  changePassword(
    newPassword: string,
    confirmPassword: string,
    userId: number,
    token: string
  ) {
    const requestBody = {
      userId: userId,
      newPassword: newPassword,
      confirmPassword: confirmPassword,
    };

    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(this.userHostName + "change_password", requestBody, {
      headers,
    });
  }

  registerUser(requestBody: any, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(
      this.userHostName + "register_user",
      requestBody,
      {
        headers,
      }
    );
  }

   updateUserRoles(requestBody: any, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(
      this.userHostName + "update_user_roles",
      requestBody,
      {
        headers,
      }
    );
  }

  updateUserDetails(requestBody: any, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(
      this.userHostName + "update_user_details",
      requestBody,
      {
        headers,
      }
    );
  }

  updateAddresDetails(requestBody: any, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(
      this.userHostName + "update_address_details",
      requestBody,
      {
        headers,
      }
    );
  }

  updatePassword(requestBody: any, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(this.userHostName + "update_password", requestBody, {
      headers,
    });
  }

  updateProfileImage(image: string, contentType: string, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(
      this.userHostName + "update_profile_image",
      { image, contentType },
      { headers },
    );
  }

  refreshTokenExpiryTime(email: string, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return this.http.get(
      this.authHostName + "refresh_token_expiry_time/" + email,
      { headers }
    );
  }

  findUserDetailsById(id: number, token: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return this.http.get(this.userHostName + "find_user_details_by_id/" + id, {
      headers,
    });
  }

  /**LOOKUP SERVICES */

  lookupGenericGet(token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return this.http.get(this.lookupHostName + serviceName, {
      headers,
    });
  }

  lookupGenericPost(requestBody: any, token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return this.http.post(this.lookupHostName + serviceName, requestBody, {
      headers,
    });
  }

  lookupGenericDelete(token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.delete(this.lookupHostName + serviceName, {
      headers,
    });
  }

  tertiaryGenericGet(token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return this.http.get(this.tertiaryHostName + serviceName, {
      headers,
    });
  }

  userGenericGet(token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return this.http.get(this.userHostName + serviceName, {
      headers,
    });
  }

  userGenericDelete(token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.delete(this.userHostName + serviceName, {
      headers,
    });
  }

  tertiaryGenericPost(requestBody: any, token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.post(this.tertiaryHostName + serviceName, requestBody, {
      headers,
    });
  }

  tertiaryGenericDelete(token: string, serviceName: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    return this.http.delete(this.tertiaryHostName + serviceName, {
      headers,
    });
  }

  startPollingTasks(userId: number, token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return timer(0, this.tasksPollInterval).pipe(
      switchMap(() =>
        this.http.get(
          this.tertiaryHostName + "find_tasks_by_user_id/" + userId,
          { headers }
        )
      )
    );
  }

  startPollingDashboardCardsReport(token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return timer(0, this.dashboardPollInterval).pipe(
      switchMap(() =>
        this.http.get(this.tertiaryHostName + "dashboard_cards_report", {
          headers,
        })
      )
    );
  }

  startPollingAllMailLogs(token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return timer(0, this.dashboardPollInterval).pipe(
      switchMap(() =>
        this.http.get(this.tertiaryHostName + "find_all_mailLogs", { headers })
      )
    );
  }

  startPollingPendingMail(token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return timer(0, this.dashboardPollInterval).pipe(
      switchMap(() =>
        this.http.get(this.tertiaryHostName + "find_pending_email", { headers })
      )
    );
  }

  startPollingUserTaskChart(userId: number, token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });
    return timer(0, this.dashboardPollInterval).pipe(
      switchMap(() =>
        this.http.get(this.tertiaryHostName + "find_task_chart/" + userId, {
          headers,
        })
      )
    );
  }

  startPollingLineChartStarts(token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    const currentYear = new Date().getFullYear();

    return timer(0, this.dashboardPollInterval).pipe(
      switchMap(() =>
        this.http.get(
          this.tertiaryHostName +
            "get_chart_stats_data_for_year/" +
            currentYear,
          { headers }
        )
      )
    );
  }

  startPollingBarChartStartsForCurrentDay(token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

   const currentDate = this.datePipe.transform(new Date(), 'dd-MM-yyyy');

    return timer(0, this.dashboardPollInterval).pipe(
      switchMap(() =>
        this.http.get(
          this.tertiaryHostName +
            "get_bar_graph_stats_data_for_date/" +
            currentDate,
          { headers }
        )
      )
    );
  }

  startPollingBarChartStartsForCurrentMonth(token: string): Observable<any> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    });

    const currentMonth = new Date().getMonth() + 1;

    return timer(0, this.dashboardPollInterval).pipe(
      switchMap(() =>
        this.http.get(
          this.tertiaryHostName +
            "get_bar_graph_stats_data_for_month/" +
            currentMonth,
          { headers }
        )
      )
    );
  }
}
