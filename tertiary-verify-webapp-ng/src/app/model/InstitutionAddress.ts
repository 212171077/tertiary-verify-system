import { DropDown } from "./DropDown";

export interface InstitutionAddress {
  id: number;
  addressLine1: string;
  addressLine2: string;
  addressLine3: string;
  addressLine4: string;
  suburb: string;
  code: string;
  latitude: number;
  longitude: number;
  createDate: Date;
  lastUpdateDate: Date;
  examinationNumber: string;
  provinceList: DropDown[];
  cityList: DropDown[];
}
