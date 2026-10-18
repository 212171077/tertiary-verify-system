import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ManageInstitutionComponent } from '../manage-institution.component';
import { ConfirmInstitutionDetailsComponent } from '../confirm-institution-details/confirm-institution-details.component';
import { AddressDetailsComponent } from '../address-details/address-details.component';
import { InstitutionDetailsComponent } from '../institution-details/institution-details.component';
import { CourseDetailsComponent } from '../course-details/course-details.component';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild([
        {path: '',component: ManageInstitutionComponent, children:[
				{path: '', redirectTo: 'institution-details', pathMatch: 'full'},
        {path: 'institution-details', component: InstitutionDetailsComponent},
        {path: 'address-details', component: AddressDetailsComponent},
        {path: 'courses-details', component: CourseDetailsComponent},
        {path: 'confirm-details', component: ConfirmInstitutionDetailsComponent},
        ],
      }
    ])
  ],
  exports: [RouterModule]
})
export class ManageInstitutionModule { }
