import { DropDown } from "./DropDown";
import { InstitutionAddress } from "./InstitutionAddress";

export interface InstitutionCourse {
  id: number;
  description: string;
  courseTypeList: DropDown[];
  courseLevelList: DropDown[];
  addressList: InstitutionAddress[];
  }