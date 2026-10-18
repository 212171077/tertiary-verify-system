class InstitutionDetail {
  final String imageUrl;
  final String name;
  final String cellNumber;
  final String email;
  final String accreditedStartDate;
  final String accreditedEndDate;
  final String accreditationNumber;
  final String address;
  final List<String> courses;

  InstitutionDetail({
    required this.imageUrl,
    required this.name,
    required this.cellNumber,
    required this.email,
    required this.accreditedStartDate,
    required this.accreditedEndDate,
    required this.accreditationNumber,
    required this.address,
    required this.courses,
  });
}