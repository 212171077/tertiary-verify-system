import 'package:tertiary_verify/model/institution.dart';

class CourseDetail {
  String image;
  String name;
  String type;
  String level;
  List<Institution> institutions;

  CourseDetail(
      {required this.image,
      required this.name,
      required this.type,
      required this.level,
      required this.institutions});
}
