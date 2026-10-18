import { ApplicationConfiguarationComponent } from './pages/lookup/application-configuaration/application-configuaration.component';
import {RouterModule} from '@angular/router';
import {NgModule} from '@angular/core';
import {DashboardDemoComponent} from './demo/view/dashboarddemo.component';
import {FormLayoutDemoComponent} from './demo/view/formlayoutdemo.component';
import {FloatLabelDemoComponent} from './demo/view/floatlabeldemo.component';
import {InvalidStateDemoComponent} from './demo/view/invalidstatedemo.component';
import {PanelsDemoComponent} from './demo/view/panelsdemo.component';
import {OverlaysDemoComponent} from './demo/view/overlaysdemo.component';
import {MediaDemoComponent} from './demo/view/mediademo.component';
import {MessagesDemoComponent} from './demo/view/messagesdemo.component';
import {MiscDemoComponent} from './demo/view/miscdemo.component';
import {EmptyDemoComponent} from './demo/view/emptydemo.component';
import {ChartsDemoComponent} from './demo/view/chartsdemo.component';
import {FileDemoComponent} from './demo/view/filedemo.component';
import {DocumentationComponent} from './demo/view/documentation.component';
import {AppMainComponent} from './app.main.component';
import {AppNotfoundComponent} from './pages/app.notfound.component';
import {AppErrorComponent} from './pages/app.error.component';
import {AppAccessdeniedComponent} from './pages/app.accessdenied.component';
import {AppLoginComponent} from './pages/app.login.component';
import {InputDemoComponent} from './demo/view/inputdemo.component';
import {ButtonDemoComponent} from './demo/view/buttondemo.component';
import {TableDemoComponent} from './demo/view/tabledemo.component';
import {ListDemoComponent} from './demo/view/listdemo.component';
import {TreeDemoComponent} from './demo/view/treedemo.component';
import {IconsComponent} from './utilities/icons.component';
import {AppCrudComponent} from './pages/app.crud.component';
import {AppCalendarComponent} from './pages/app.calendar.component';
import {AppTimelineDemoComponent} from './pages/app.timelinedemo.component';
import {AppInvoiceComponent} from './pages/app.invoice.component';
import {AppHelpComponent} from './pages/app.help.component';
import {BlocksComponent} from './blocks/blocks/blocks.component';
import { ProfileComponent } from './pages/app.profile.component';
import { AllReportedInstitutionsComponent } from './pages/all-reported-institutions/all-reported-institutions.component';
import { InstitutionRegistrationComponent } from './pages/institution-registration/institution-registration.component';
import { UsersComponent } from './pages/users/users.component';
import { TitlesComponent } from './pages/lookup/titles/titles.component';
import { RejectReasonsComponent } from './pages/lookup/reject-reasons/reject-reasons.component';
import { ProvincesComponent } from './pages/lookup/provinces/provinces.component';
import { NewsComponent } from './pages/lookup/news/news.component';
import { InstitutionTypesComponent } from './pages/lookup/institution-types/institution-types.component';
import { GendersComponent } from './pages/lookup/genders/genders.component';
import { FacultiesComponent } from './pages/lookup/faculties/faculties.component';
import { EventsComponent } from './pages/lookup/events/events.component';
import { CourseTypesComponent } from './pages/lookup/course-types/course-types.component';
import { CourseLevelsComponent } from './pages/lookup/course-levels/course-levels.component';
import { CoursesComponent } from './pages/lookup/courses/courses.component';
import { CitiesComponent } from './pages/lookup/cities/cities.component';
import { PendingInstitutionsComponent } from './pages/pending-institutions/pending-institutions.component';
import { MyReportedInstitutionsComponent } from './pages/my-reported-institutions/my-reported-institutions.component';
import { ConfirmInstitutionDetailsComponent } from './pages/manage-institution/confirm-institution-details/confirm-institution-details.component';
import { PendingApprovalViewComponent } from './pages/pending-approval-view/pending-approval-view.component';
import { InstitutionPrioritisationComponent } from './pages/institution-prioritisation/institution-prioritisation.component';

@NgModule({
    imports: [
        RouterModule.forRoot([
            {
                path: '', component: AppMainComponent,
                children: [
                    {path: '', component: DashboardDemoComponent},
                    {path: 'uikit/formlayout', component: FormLayoutDemoComponent},
                    {path: 'uikit/floatlabel', component: FloatLabelDemoComponent},
                    {path: 'uikit/invalidstate', component: InvalidStateDemoComponent},
                    {path: 'uikit/input', component: InputDemoComponent},
                    {path: 'uikit/button', component: ButtonDemoComponent},
                    {path: 'uikit/table', component: TableDemoComponent},
                    {path: 'uikit/list', component: ListDemoComponent},
                    {path: 'uikit/tree', component: TreeDemoComponent},
                    {path: 'uikit/panel', component: PanelsDemoComponent},
                    {path: 'uikit/overlay', component: OverlaysDemoComponent},
                    {path: 'uikit/menu', loadChildren: () => import('./demo/view/menus/menus.module').then(m => m.MenusModule)},
                    {path: 'uikit/media', component: MediaDemoComponent},
                    {path: 'uikit/message', component: MessagesDemoComponent},
                    {path: 'uikit/misc', component: MiscDemoComponent},
                    {path: 'uikit/charts', component: ChartsDemoComponent},
                    {path: 'uikit/file', component: FileDemoComponent},
                    {path: 'utilities/icons', component: IconsComponent},
                    {path: 'pages/crud', component: AppCrudComponent},
                    {path: 'pages/calendar', component: AppCalendarComponent},
                    {path: 'pages/timeline', component: AppTimelineDemoComponent},
                    {path: 'pages/invoice', component: AppInvoiceComponent},
                    {path: 'pages/help', component: AppHelpComponent},
                    {path: 'pages/empty', component: EmptyDemoComponent},
                    {path: 'documentation', component: DocumentationComponent},
                    {path: 'blocks', component: BlocksComponent},
                    {path: 'pages/profile', component: ProfileComponent},
                    {path: 'pages/lookup/app-config', component: ApplicationConfiguarationComponent},
                    {path: 'pages/lookup/cities', component: CitiesComponent},
                    {path: 'pages/lookup/courses', component: CoursesComponent},
                    {path: 'pages/lookup/course-levels', component: CourseLevelsComponent},
                    {path: 'pages/lookup/course-types', component: CourseTypesComponent},
                    {path: 'pages/lookup/events', component: EventsComponent},
                    {path: 'pages/lookup/faculties', component: FacultiesComponent},
                    {path: 'pages/lookup/genders', component: GendersComponent},
                    {path: 'pages/lookup/institution-types', component: InstitutionTypesComponent},
                    {path: 'pages/lookup/news', component: NewsComponent},
                    {path: 'pages/lookup/provinces', component: ProvincesComponent},
                    {path: 'pages/lookup/reject-reasons', component: RejectReasonsComponent},
                    {path: 'pages/lookup/titles', component: TitlesComponent},
                    {path: 'pages/users', component: UsersComponent},
                    {path: 'pages/institution-registration', component: InstitutionRegistrationComponent},
                    {path: 'pages/all-reported-institutions', component: AllReportedInstitutionsComponent},
                    {path: 'pages/pending-institutions', component: PendingInstitutionsComponent},
                    {path: 'pages/my-reported-institutions', component: MyReportedInstitutionsComponent},
                    {path: 'pages/institution-prioritisation', component: InstitutionPrioritisationComponent},
                    {path: 'pages/pending-approval-view', component: PendingApprovalViewComponent},
                    {path: 'pages/manage-institution', loadChildren: () => import('./pages/manage-institution/manage-institution/manage-institution.module').then(m => m.ManageInstitutionModule)},
                ]
            },
            {path: 'error', component: AppErrorComponent},
            {path: 'access', component: AppAccessdeniedComponent},
            {path: 'notfound', component: AppNotfoundComponent},
            {path: 'login', component: AppLoginComponent},
            {path: '**', redirectTo: '/notfound'},
        ], {scrollPositionRestoration: 'enabled'})
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}


/*** ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
        ,
         */