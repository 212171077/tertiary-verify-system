class ReportedInstitution {
  bool success;
  String institutionName;
  String reportType;
  String reportedDate;
  String refNumber;
  String investigatorFeedback;
  String status;
  String lastUpdatedDate;
  String errorMessage;

  ReportedInstitution(
      {required this.success,
      required this.institutionName,
      required this.reportType,
      required this.reportedDate,
      required this.refNumber,
      required this.investigatorFeedback,
      required this.status,
      required this.lastUpdatedDate,
      required this.errorMessage});
}
