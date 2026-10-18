class Institution {
  int id;
  String name;
  String knownAs;
  String cellNumber;
  String email;
  String accreditedNumber;
  List<String> addressList;
  String image;
  String accreditedStartDate;
  String accreditedEndDate;
  String applyLink;
  bool showApplyLink;
  String status;
  String statusDescription;
  bool prioritize;

  Institution(
      {required this.id,
      required this.name,
      required this.knownAs,
      required this.cellNumber,
      required this.email,
      required this.accreditedNumber,
      required this.addressList,
      required this.image,
      required this.accreditedStartDate,
      required this.accreditedEndDate,
      required this.applyLink,
      required this.showApplyLink,
      required this.status,
      required this.statusDescription,
      required this.prioritize});
}
