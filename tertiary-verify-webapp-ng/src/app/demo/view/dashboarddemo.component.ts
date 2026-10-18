import { Component, OnInit } from "@angular/core";
import { MenuItem } from "primeng/api";
import { EventService } from "../service/eventservice";
import { ProductService } from "../service/productservice";
// @fullcalendar plugins
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import timeGridPlugin from "@fullcalendar/timegrid";
import { TertiaryVerifyService } from "src/app/service/tertiary-verify.service";
import { SessionService } from "src/app/service/session.service";

@Component({
  templateUrl: "./dashboard.component.html",
  styleUrls: ["../../../assets/demo/badges.scss"],
  styles: [
    `
      @media screen and (max-width: 960px) {
        :host ::ng-deep .fc-header-toolbar {
          display: flex;
          flex-wrap: wrap;

          .fc-dayGridMonth-button {
            margin-top: 1rem;
          }
          .fc-timeGridWeek-button {
            margin-top: 1rem;
          }
          .fc-timeGridDay-button {
            margin-top: 1rem;
          }
        }
      }
    `,
  ],
})
export class DashboardDemoComponent implements OnInit {
  cardsReport: any;
  mailLogList: any;
  pendingEmailList: any;
  taskChartData: any;
  chartData: any;
  chartOptions: any;
  loadingMailLogList: boolean;
  loadingCardsReport: boolean;
  loadingPendingEmail: boolean;
  loadingTaskChartData: boolean;
  barGraphStatsOptions: any;
  barGraphStatsData: any;
  currentYear: number;
  barGraphStatsDataForToday: any;
  barGraphStatsDataForCurrentMon: any;
  noYearlyStats: boolean;
  noDailyStats: boolean;
  noMonthlyStats: boolean;

  constructor(
    private tertiaryVerifyService: TertiaryVerifyService,
    private sessionService: SessionService
  ) {}

  ngOnInit() {
    this.loadBarGraphStarts();
    this.initLineChartStarts();
    this.currentYear = new Date().getFullYear();

    this.loadingMailLogList = true;
    this.loadingCardsReport = true;
    this.loadingPendingEmail = true;
    this.loadingTaskChartData = true;
    this.noYearlyStats = true;
    this.noDailyStats = true;
    this.noMonthlyStats = true;

    this.tertiaryVerifyService
      .startPollingDashboardCardsReport(
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.loadingCardsReport = false;
          this.cardsReport = response;
        },
        (error) => {
          console.error("Error getting dashboard cards report:", error);
        }
      );

    this.tertiaryVerifyService
      .startPollingAllMailLogs(this.sessionService.getLoginObject().token)
      .subscribe(
        (response) => {
          this.loadingMailLogList = false;
          this.mailLogList = response;
        },
        (error) => {
          console.error("Error when polling all emails logs:", error);
        }
      );

    this.tertiaryVerifyService
      .startPollingPendingMail(this.sessionService.getLoginObject().token)
      .subscribe(
        (response) => {
          this.loadingPendingEmail = false;
          this.pendingEmailList = response;
        },
        (error) => {
          console.error("Error when polling all pending emails:", error);
        }
      );

    this.tertiaryVerifyService
      .startPollingUserTaskChart(
        this.sessionService.getLoginObject().id,
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.loadingTaskChartData = false;
          this.taskChartData = response;
        },
        (error) => {
          console.error("Error when polling user chart data:", error);
        }
      );

    this.tertiaryVerifyService
      .startPollingLineChartStarts(this.sessionService.getLoginObject().token)
      .subscribe(
        (response) => {
          this.chartData = response;
          this.noYearlyStats = response == null;
        },
        (error) => {
          console.error("Error when polling all line chart stats", error);
          this.noYearlyStats = true;
        }
      );

    this.tertiaryVerifyService
      .startPollingBarChartStartsForCurrentDay(
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.barGraphStatsDataForToday = response;
          this.noDailyStats = response == null;
        },
        (error) => {
          console.error("Error when polling bar graph stats", error);
          this.noDailyStats = true;
        }
      );

    this.tertiaryVerifyService
      .startPollingBarChartStartsForCurrentMonth(
        this.sessionService.getLoginObject().token
      )
      .subscribe(
        (response) => {
          this.barGraphStatsDataForCurrentMon = response;
          this.noMonthlyStats = response == null;
        },
        (error) => {
          console.error("Error when polling bar graph stats", error);
          this.noMonthlyStats = true;
        }
      );
  }

  initLineChartStarts() {
    this.chartData = {
      labels: [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
      ],
    };

    this.chartOptions = {
      responsive: true,
      hover: {
        mode: "index",
      },
      scales: {
        x: {
          display: true,
          scaleLabel: {
            display: true,
            labelString: "Month",
          },
        },
        y: {
          display: true,
          scaleLabel: {
            display: true,
            labelString: "Value",
          },
        },
      },
    };
  }

  loadBarGraphStarts() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue("--text-color");
    const textColorSecondary = documentStyle.getPropertyValue(
      "--text-color-secondary"
    );
    const surfaceBorder = documentStyle.getPropertyValue("--surface-border");

    this.barGraphStatsDataForToday = {
      labels: ["Loading...", "Loading...", "Loading...", "Loading..."],
      datasets: [
        {
          label: "Today's App Usage Statistics",
          data: [0, 0, 0, 0],
          backgroundColor: [
            "rgba(255, 159, 64, 0.2)",
            "rgba(75, 192, 192, 0.2)",
            "rgba(54, 162, 235, 0.2)",
            "rgba(153, 102, 255, 0.2)",
          ],
          borderColor: [
            "rgb(255, 159, 64)",
            "rgb(75, 192, 192)",
            "rgb(54, 162, 235)",
            "rgb(153, 102, 255)",
          ],
          borderWidth: 1,
        },
      ],
    };

    const monthName = new Date().toLocaleString("default", { month: "long" });

    this.barGraphStatsDataForCurrentMon = {
      labels: ["Loading...", "Loading...", "Loading...", "Loading..."],
      datasets: [
        {
          label: monthName + "'s App Usage Statistics",
          data: [0, 0, 0, 0],
          backgroundColor: [
            "rgba(255, 159, 64, 0.2)",
            "rgba(75, 192, 192, 0.2)",
            "rgba(54, 162, 235, 0.2)",
            "rgba(153, 102, 255, 0.2)",
          ],
          borderColor: [
            "rgb(255, 159, 64)",
            "rgb(75, 192, 192)",
            "rgb(54, 162, 235)",
            "rgb(153, 102, 255)",
          ],
          borderWidth: 1,
        },
      ],
    };

    this.barGraphStatsOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: {
          labels: {
            color: textColor,
          },
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
        x: {
          ticks: {
            color: textColorSecondary,
            font: {
              weight: 500,
            },
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
      },
    };
  }
}
