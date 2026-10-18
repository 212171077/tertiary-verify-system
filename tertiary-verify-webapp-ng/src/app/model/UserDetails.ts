import { DropDown } from "./DropDown";

export interface UserDetails {
    id: number;
    name?: string;
    surname?: string;
    email?: string;
    roles?: any;
    createDate?: Date | string;
    lastLoginDate?: Date | string;
    status?: string;
    rsaId?: string;
    dateOfBirth?: string;
    cellNumber?: string;
    titles?: DropDown[];
    genders?: DropDown[];
    residentialAddressLine1?: string;
    residentialAddressLine2?: string;
    residentialAddressLine3?: string;
    residentialAddressLine4?: string;
    residentialAddressCities?: DropDown[];
    residentialAddressSuburb?: string;
    residentialCode?: string;
    residentialAddressProvinces?: DropDown[];
    postalAddressLine1?: string;
    postalAddressLine2?: string;
    postalAddressLine3?: string;
    postalAddressLine4?: string;
    postalAddressCities?: DropDown[];
    postalAddressSuburb?: string;
    postalAddressCode?: string;
    postalAddressProvinces?: DropDown[];
    imageUrl?: string;
    menus?: any;
  }