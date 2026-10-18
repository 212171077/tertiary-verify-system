import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:tertiary_verify/model/welcome_data.dart';
import '../model/course.dart';
import '../model/institution.dart';
import '../model/course_detail.dart';
import '../model/reported_institution.dart';

class TertiaryVerifyService {
  //static String hostName = "http://localhost:8081/api/tertiary_verify";
  static String hostName = "https://tertiaryverify.dedicated.co.za:8082/api/tertiary_verify";
  

  static Future<List<CourseDetail>> fetchAccreditedCourses() async {
    final response =
        await http.get(Uri.parse('$hostName/find_accredited_courses'));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      final List<CourseDetail> courses = [];

      for (var courseData in data) {
        final course = CourseDetail(
            image: "assets/images/course.png",
            name: courseData['name'],
            type: courseData['type'],
            level: courseData['level'],
            institutions: []);

        for (var inst in courseData['institutionList']) {
          final institution = Institution(
              id: inst['id'],
              name: inst['name'],
              knownAs: inst['knownAs'],
              cellNumber: inst['cellNumber'],
              email: inst['email'],
              accreditedNumber: inst['accreditedNumber'],
              addressList: inst['addressList'].cast<String>(),
              image: inst['image'],
              accreditedStartDate: inst['accreditedStartDate'],
              accreditedEndDate: inst['accreditedEndDate'],
              applyLink: inst['applyLink'],
              showApplyLink: inst['prioritize'] == true &&
                  inst['applyLink'] != null &&
                  inst['applyLink'].isNotEmpty,
              status: inst['status'],
              statusDescription: inst['statusDescription'],
              prioritize: inst['prioritize']);

          course.institutions.add(institution);
        }
        courses.add(course);
      }

      return courses;
    } else {
      throw Exception('Failed to load courses');
    }
  }

  static Future<List<Course>> findAllCourses() async {
    final response = await http.get(Uri.parse('$hostName/find_all_courses'));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      final List<Course> courses = [];

      for (var cs in data) {
        final course = Course(
            id: cs['id'],
            image: "assets/images/course.png",
            name: cs['description'],
            type: cs['courseType'],
            level: cs['courseLevel']);

        courses.add(course);
      }
      return courses;
    } else {
      throw Exception('Failed to load courses');
    }
  }

  static Future<List<Institution>> fetchAccreditedInstitutions() async {
    final response =
        await http.get(Uri.parse('$hostName/find_accredited_institutions'));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      final List<Institution> institutions = [];

      for (var inst in data) {
        final institution = Institution(
            id: inst['id'],
            name: inst['name'],
            knownAs: inst['knownAs'],
            cellNumber: inst['cellNumber'],
            email: inst['email'],
            accreditedNumber: inst['accreditedNumber'],
            addressList: inst['addressList'].cast<String>(),
            image: inst['image'],
            accreditedStartDate: inst['accreditedStartDate'],
            accreditedEndDate: inst['accreditedEndDate'],
            applyLink: inst['applyLink'],
            showApplyLink: inst['prioritize'] == true &&
                inst['applyLink'] != null &&
                inst['applyLink'].isNotEmpty,
            status: inst['status'],
            statusDescription: inst['statusDescription'],
            prioritize: inst['prioritize']);
        institutions.add(institution);
      }

      return institutions;
    } else {
      throw Exception('Failed to load institutions');
    }
  }

  static Future<List<Institution>> findAllInstitutions() async {
    final response =
        await http.get(Uri.parse('$hostName/find_all_institutions'));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      final List<Institution> institutions = [];

      for (var inst in data) {
        final institution = Institution(
            id: inst['id'],
            name: inst['institutionName'],
            knownAs: '',
            cellNumber: inst['contactNumber'],
            email: '',
            accreditedNumber: inst['accreditationNumber'],
            addressList: [],
            image: inst['image'],
            accreditedStartDate: '',
            accreditedEndDate: '',
            applyLink: inst['applyLink'],
            showApplyLink: inst['prioritize'] == true &&
                inst['applyLink'] != null &&
                inst['applyLink'].isNotEmpty,
            status: inst['status'],
            statusDescription: inst['statusDescription'],
            prioritize: inst['prioritize']);
        institutions.add(institution);
      }

      return institutions;
    } else {
      throw Exception('Failed to load institutions');
    }
  }

  static Future<List<Course>> fetchInstitutionCourses(int instID) async {
    final response =
        await http.get(Uri.parse('$hostName/find_institution_course/$instID'));

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      final List<Course> courses = [];

      for (var cs in data) {
        final course = Course(
            id: cs['id'],
            image: 'assets/images/course.png',
            name: cs['description'],
            type: cs['courseType'],
            level: cs['courseLevel']);
        courses.add(course);
      }

      return courses;
    } else {
      throw Exception('Failed to load courses');
    }
  }

  static Future<bool> sendContactUsMessage({
    required String name,
    required String email,
    required String phone,
    required String message,
  }) async {
    final url = Uri.parse('$hostName/contact_us');
    final data = {
      'cellNumber': phone,
      'email': email,
      'fullName': name,
      'message': message
    };

    final response = await http.post(url,
        headers: {'Content-Type': 'application/json'}, body: json.encode(data));

    if (response.statusCode == 200) {
      return true;
    } else {
      return false;
    }
  }

  static Future<ReportedInstitution> findReportedInstitution(
      String refNumber) async {
    final response = await http
        .get(Uri.parse('$hostName/find_reported_institution/$refNumber'));

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      if (data['success']) {
        return ReportedInstitution(
            success: true,
            institutionName: data['institutionName'],
            reportType: data['reportType'],
            reportedDate: data['reportedDate'],
            refNumber: data['refNumber'],
            investigatorFeedback: data['investigatorFeedback'],
            status: data['status'],
            lastUpdatedDate: data['lastUpdatedDate'],
            errorMessage: '');
      } else {
        return ReportedInstitution(
            success: false,
            institutionName: '',
            reportType: '',
            reportedDate: '',
            refNumber: '',
            investigatorFeedback: '',
            status: '',
            lastUpdatedDate: '',
            errorMessage: data['errorMessage']);
      }
    } else {
      throw Exception('Service unavailable');
    }
  }

  static Future<bool> reportBogusInstitution({
    required String userName,
    required String userSurname,
    required String userEmail,
    required String userCellNumber,
    required String instName,
    required String instKnownAs,
    required String instAddressLine1,
    required String instAddressLine2,
    required String instAddressLine3,
    required String provinceName,
    required String instSuburb,
    required String instPostalCode,
    required String additionalInfo,
  }) async {
    final url = Uri.parse('$hostName/report_bogus_institution');
    final data = {
      'userName': userName,
      'userSurname': userSurname,
      'userEmail': userEmail,
      'userCellNumber': userCellNumber,
      'instName': instName,
      'instKnownAs': instKnownAs,
      'instAddressLine1': instAddressLine1,
      'instAddressLine2': instAddressLine2,
      'instAddressLine3': instAddressLine3,
      'provinceName': provinceName,
      'instSuburb': instSuburb,
      'instPostalCode': instPostalCode,
      'additionalInfo': additionalInfo
    };

    try {
      final response = await http.post(url,
          headers: {'Content-Type': 'application/json'},
          body: json.encode(data));

      if (response.statusCode == 200) {
        return true;
      } else {
        return false;
      }
    } catch (e) {
      print("An error occurred: $e");
      return false;
    }
  }

  static Future<bool> reportBogusCourse({
    required String userName,
    required String userSurname,
    required String userEmail,
    required int instId,
    required String instName,
    required String instKnownAs,
    required int courseId,
    required String courseType,
    required String userCellNumber,
    required String courseLevel,
    required String courseDesc,
  }) async {
    final url = Uri.parse('$hostName/report_bogus_course');
    final data = {
      'userName': userName,
      'userSurname': userSurname,
      'userEmail': userEmail,
      'userCellNumber': userCellNumber,
      'instId': instId,
      'instName': instName,
      'instKnownAs': instKnownAs,
      'courseId': courseId,
      'courseType': courseType,
      'courseLevel': courseLevel,
      'courseDesc': courseDesc
    };

    try {
      final response = await http.post(url,
          headers: {'Content-Type': 'application/json'},
          body: json.encode(data));
      if (response.statusCode == 200) {
        return true;
      } else {
        return false;
      }
    } catch (e) {
      print("An error occurred: $e");
      return false;
    }
  }

  static void saveStatistic(
      {required String title,
      required String description,
      required String sessionId}) async {
    final url = Uri.parse('$hostName/save_statistic');
    final data = {
      'title': title,
      'echoRef': sessionId,
      'description': description,
      'channel': 'MOBILE APP',
    };

    await http
        .post(url,
            headers: {'Content-Type': 'application/json'},
            body: json.encode(data))
        .then((response) {
      if (response.statusCode == 200) {
        // Request was successful.
        print('Statistic saved successfully.');
      } else {
        // Handle the error.
        print('Failed to save statistic: ${response.statusCode}');
      }
    }).catchError((error) {
      // Handle any exceptions.
      print('Error: $error');
    });
  }


 static Future<WelcomeData> getWelcomeData() async {
  try {
    final response = await http.get(Uri.parse('$hostName/get_welcome_data'));

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      if (data['success']) {
        return WelcomeData(
            dataPrivacyUrl: data['dataPrivacyUrl'], errorMessage: '');
      } else {
        return WelcomeData(
            dataPrivacyUrl: '', errorMessage: data['errorMessage']);
      }
    } else {
      throw Exception('Service unavailable');
    }
  } catch (e) {
    return WelcomeData(dataPrivacyUrl: '', errorMessage: e.toString());
  }
}

}
