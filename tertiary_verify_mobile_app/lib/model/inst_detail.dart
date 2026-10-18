import 'institution.dart';
import 'course.dart';

class InstDetails {
  Institution institution;
  List<Course> courses;

  InstDetails({
    required this.institution,
    required this.courses,
  });
}
