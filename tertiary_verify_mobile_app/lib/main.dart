import 'package:flutter/material.dart';
import 'package:tertiary_verify/pages/institution_details_page.dart';
import 'package:tertiary_verify/pages/report_course_page.dart';
import 'package:tertiary_verify/pages/check_status_page.dart';
import 'package:tertiary_verify/pages/contact_page.dart';
import 'package:tertiary_verify/pages/home_page.dart';
import 'package:tertiary_verify/pages/report_institution_page.dart';
import 'package:tertiary_verify/pages/search_course.dart';
import 'package:tertiary_verify/pages/search_institution.dart';
import 'package:tertiary_verify/pages/welcome.dart';
import 'package:provider/provider.dart';
import 'service/session_provider.dart';

// Import your SessionProvider class here

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => SessionProvider(),
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      initialRoute: "/",
      routes: {
        "search_course": (context) => const SearchCourse(),
        "/": (context) => const Welcome(),
        "search_institution": (context) => const SearchInstitution(),
        "institution_details": (context) => const InstitutionDetailsPage(),
        "home": (context) => const HomePage(),
        "contact_us": (context) => const ContactPage(),
        "check_status": (context) => const CheckStatusPage(),
        "report_institution": (context) => const ReportInstitutionPage(),
        "report_course": (context) => const ReportCoursePage(),
      },
      color: Colors.white,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSwatch(
          primarySwatch: Colors.green,
        ).copyWith(
          primary: Colors.orange,
        ),
      ),
    );
  }
}



