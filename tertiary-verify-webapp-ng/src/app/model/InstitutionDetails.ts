import { DropDown } from "./DropDown";
import { InstitutionAddress } from "./InstitutionAddress";
import { InstitutionCourse } from "./InstitutionCourse";

export interface InstitutionDetails {
  id: number;
  createDate: Date;
  lastUpdateDate: Date;
  creatorUserID: number;
  supervisorID: number;
  institutionTypeList: DropDown[];
  institutionName: string;
  contactNumber: string;
  alternativeContactNumber: string;
  email: string;
  accreditationStartDate: Date;
  accreditationEndDate: Date;
  accreditationNumber: string;
  status: string;
  qrCode: string;
  image: string;
  prioritize: boolean;
  uuid: string;
  applyLink:string,
  addressList: InstitutionAddress[];
  courseList: InstitutionCourse[];
}
