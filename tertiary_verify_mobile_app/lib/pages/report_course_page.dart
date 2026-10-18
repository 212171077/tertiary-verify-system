import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/pages/success_bottom_sheet.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:tertiary_verify/validator/common_validator.dart';

import '../model/course.dart';
import '../model/institution.dart';
import '../service/tertiary_verify_service.dart';
import 'common.dart';
import 'failure_bottom_sheet.dart';

class ReportCoursePage extends StatefulWidget {
  const ReportCoursePage({super.key});

  @override
  _ReportCoursePageState createState() => _ReportCoursePageState();
}

class _ReportCoursePageState extends State<ReportCoursePage> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _nameController;
  late TextEditingController _surnameController;
  late TextEditingController _emailController;
  late TextEditingController _cellController;
  late TextEditingController _courseController;
  late TextEditingController _additionalInfoController;

  late List<Course> _courseList;
  late List<Institution> _institutionList;

  late Course _course;
  late Institution _institution;
  int _currentPageIndex = 0;

  bool _isInstitutionSelected = false;
  bool _isCourseSelected = false;

  @override
  void initState() {
    fetchData();
    super.initState();
    _nameController = TextEditingController();
    _surnameController = TextEditingController();
    _emailController = TextEditingController();
    _cellController = TextEditingController();
    _courseController = TextEditingController();
    _additionalInfoController = TextEditingController();
  }

  void fetchData() async {
    _courseList = await TertiaryVerifyService.findAllCourses();
    _institutionList = await TertiaryVerifyService.findAllInstitutions();
  }

  @override
  void dispose() {
    _nameController.dispose();
    _surnameController.dispose();
    _emailController.dispose();
    _cellController.dispose();
    _courseController.dispose();
    _additionalInfoController.dispose();

    super.dispose();
  }

  Future<void> _submitForm() async {
    final sessionProvider = Provider.of<SessionProvider>(context);
    TertiaryVerifyService.saveStatistic(
        title: 'Report course',
        description: 'Report Course, Submit Button Clicked',
        sessionId: sessionProvider.sessionId);

    if (_formKey.currentState!.validate()) {
      String courseDesc = '';
      String courseId = '';
      String courseType = '';
      String courseLevel = '';
      if (_isCourseSelected) {
        courseId = _course.id.toString();
        courseType = _course.type;
        courseLevel = _course.level;
        courseDesc = _course.name;
      } else {
        courseDesc = _courseController.text;
      }

      bool isSent = await TertiaryVerifyService.reportBogusCourse(
          userName: _nameController.text,
          userSurname: _surnameController.text,
          userEmail: _emailController.text,
          instId: _institution.id,
          instName: _institution.name,
          instKnownAs: _institution.knownAs,
          courseId: int.parse(courseId),
          courseType: courseType,
          userCellNumber: _cellController.text,
          courseLevel: courseLevel,
          courseDesc: courseDesc);

      if (isSent) {
        showModalBottomSheet(
          context: context,
          builder: (BuildContext context) {
            return const SuccessBottomSheet(
              title: "Bogus course reported",
              message: "Thank you for reporting this bogus course. "
                  "We appreciate your efforts in helping us maintain "
                  "the standards and legitimacy of institutions in our system. "
                  "An acknowledgement email will be sent to you shortly with "
                  "further information regarding your report.",
              navTo: "home",
              eventTitle: 'Report course',
              eventDesc:
                  "Success Bottom Sheet, Close Button Clicked on Report Course Page",
            );
          },
        );
      } else {
        showModalBottomSheet(
          context: context,
          builder: (BuildContext context) {
            return const FailureBottomSheet(
              title: "Service unavailable",
              message: "We apologize for the inconvenience, but we "
                  "are unable to submit your report at this time. "
                  "Please try again later or contact our support team for assistance.",
              navTo: "home",
              eventTitle: 'Report course',
              eventDesc:
                  "Failure Bottom Sheet, Close Button Clicked On Report course page",
            );
          },
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final sessionProvider = Provider.of<SessionProvider>(context);
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: Common.buildAppBar(context),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Common.buildFormCardHeader(
              "Report Bogus Course",
              "We deploy all efforts to protect the private nature of your personal information.",
              "assets/images/report.png",
            ),
            Container(
              padding: const EdgeInsets.all(16.0),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    if (_currentPageIndex == 0)
                      const Text(
                        'Personal details',
                        style: TextStyle(
                          fontSize: 25.0,
                          fontWeight: FontWeight.bold,
                          fontFamily: "DefaultFont",
                          color: Common.primaryColor,
                        ),
                      ),
                    if (_currentPageIndex == 0) const SizedBox(height: 15.0),
                    if (_currentPageIndex == 0)
                      TextFormField(
                        controller: _nameController,
                        decoration:
                            Common.buildInputDecoration('Name', 'Enter name'),
                        validator: CommonValidator.validateName,
                      ),
                    if (_currentPageIndex == 0) const SizedBox(height: 20),
                    if (_currentPageIndex == 0)
                      TextFormField(
                        controller: _surnameController,
                        decoration: Common.buildInputDecoration(
                            'Surname', 'Enter surname'),
                        validator: CommonValidator.validateSurname,
                      ),
                    if (_currentPageIndex == 0) const SizedBox(height: 20),
                    if (_currentPageIndex == 0)
                      TextFormField(
                        controller: _emailController,
                        decoration: Common.buildInputDecoration(
                            'Email address', 'Enter email address'),
                        validator: CommonValidator.validateEmail,
                      ),
                    if (_currentPageIndex == 0) const SizedBox(height: 20),
                    if (_currentPageIndex == 0)
                      TextFormField(
                        controller: _cellController,
                        decoration: Common.buildInputDecoration(
                            'Cell number', 'Enter cell number'),
                        validator: CommonValidator.validateCellNumber,
                      ),
                    if (_currentPageIndex == 1)
                      const Text(
                        'Course details',
                        style: TextStyle(
                          fontSize: 25.0,
                          fontWeight: FontWeight.bold,
                          fontFamily: "DefaultFont",
                          color: Common.primaryColor,
                        ),
                      ),
                    if (_currentPageIndex == 1) const SizedBox(height: 20),
                    if (_currentPageIndex == 1)
                      Autocomplete<Institution>(
                        optionsBuilder: (TextEditingValue textEditingValue) {
                          return _institutionList.where((inst) =>
                              inst.name.toLowerCase().contains(
                                  textEditingValue.text.toLowerCase()) ||
                              inst.accreditedNumber.toLowerCase().contains(
                                  textEditingValue.text.toLowerCase()) ||
                              inst.cellNumber.toLowerCase().contains(
                                  textEditingValue.text.toLowerCase()));
                        },
                        onSelected: (Institution selection) {
                          setState(() {
                            _institution = selection;
                            _isInstitutionSelected = true;
                          });
                        },
                        fieldViewBuilder: (BuildContext context,
                            TextEditingController textEditingController,
                            FocusNode focusNode,
                            VoidCallback onFieldSubmitted) {
                          return TextFormField(
                              controller: textEditingController,
                              focusNode: focusNode,
                              onFieldSubmitted: (_) => onFieldSubmitted(),
                              decoration: buildAutoCompleteDecoration(
                                  'Institution', 'Search for an institution'),
                              validator: (value) {
                                if (!_isInstitutionSelected) {
                                  return 'Please select an institution'; // Return the error message if institution is not selected
                                }
                                return null; // Return null if there are no validation errors
                              });
                        },
                        displayStringForOption: (Institution inst) => inst.name,
                        optionsViewBuilder: (BuildContext context,
                            AutocompleteOnSelected<Institution> onSelected,
                            Iterable<Institution> options) {
                          return buildInstitutionAutoCompletePadding(
                            options,
                            onSelected,
                          );
                        },
                      ),
                    if (_currentPageIndex == 1) const SizedBox(height: 20),
                    if (_currentPageIndex == 1)
                      Autocomplete<Course>(
                        optionsBuilder: (TextEditingValue textEditingValue) {
                          return _courseList.where((course) =>
                              course.name.toLowerCase().contains(
                                  textEditingValue.text.toLowerCase()) ||
                              course.type.toLowerCase().contains(
                                  textEditingValue.text.toLowerCase()) ||
                              course.level.toLowerCase().contains(
                                  textEditingValue.text.toLowerCase()));
                        },
                        onSelected: (Course selection) {
                          setState(() {
                            _course = selection;
                            _isCourseSelected = true;
                          });
                        },
                        fieldViewBuilder: (BuildContext context,
                            TextEditingController textEditingController,
                            FocusNode focusNode,
                            VoidCallback onFieldSubmitted) {
                          return TextFormField(
                            controller: textEditingController,
                            focusNode: focusNode,
                            onFieldSubmitted: (_) => onFieldSubmitted(),
                            decoration: buildAutoCompleteDecoration(
                                'Course', 'Search for a course'),
                          );
                        },
                        displayStringForOption: (Course course) => course.name,
                        optionsViewBuilder: (BuildContext context,
                            AutocompleteOnSelected<Course> onSelected,
                            Iterable<Course> options) {
                          return buildCourseAutoCompletePadding(
                            options,
                            onSelected,
                          );
                        },
                      ),
                    if (_currentPageIndex == 1) const SizedBox(height: 10),
                    if (_currentPageIndex == 1)
                      const Text(
                        "If you don't see the course you're looking for listed above, "
                        "kindly specify the course details and we'll do our best to help you out.",
                        style: TextStyle(
                          fontSize: 10.0,
                          fontWeight: FontWeight.bold,
                          fontFamily: "DefaultFont",
                          color: Colors.grey,
                        ),
                      ),
                    if (_currentPageIndex == 1) const SizedBox(height: 10),
                    if (_currentPageIndex == 1)
                      TextFormField(
                        controller: _courseController,
                        decoration: Common.buildInputDecoration(
                            'Bogus course details',
                            'Specify course details if it is not available on the above list'),
                        validator: (value) {
                          if ((value == null || value.isEmpty) &&
                              !_isCourseSelected) {
                            return 'Specify course details or select one from the above list';
                          }
                          return null;
                        },
                      ),
                    const SizedBox(height: 16.0),
                    if (_currentPageIndex == 1) const SizedBox(height: 10),
                    if (_currentPageIndex == 1)
                      TextFormField(
                        controller: _additionalInfoController,
                        decoration: Common.buildInputDecoration(
                            'Additional Info', 'Enter additional information'),
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return 'Please enter additional information';
                          }
                          return null;
                        },
                      ),
                    const SizedBox(height: 16.0),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        if (_currentPageIndex > 0)
                          ElevatedButton(
                            style: Common.getWizardButtonStyle(),
                            onPressed: () {
                              TertiaryVerifyService.saveStatistic(
                                  title: 'Report course',
                                  description:
                                      'Report Course, Previous Button Clicked',
                                  sessionId: sessionProvider.sessionId);
                              setState(() {
                                _currentPageIndex--;
                              });
                            },
                            child: Text(
                              'PREVIOUS',
                              style: Common.getWizardButtonTextStyle(),
                            ),
                          ),
                        if (_currentPageIndex < 1)
                          ElevatedButton(
                            style: Common.getWizardButtonStyle(),
                            onPressed: () {
                              TertiaryVerifyService.saveStatistic(
                                  title: 'Report course',
                                  description:
                                      'Report Course, Next Button Clicked',
                                  sessionId: sessionProvider.sessionId);
                              if (_formKey.currentState!.validate()) {
                                setState(() {
                                  _currentPageIndex++;
                                });
                              }
                            },
                            child: Text(
                              'NEXT',
                              style: Common.getWizardButtonTextStyle(),
                            ),
                          ),
                        if (_currentPageIndex == 1)
                          ElevatedButton(
                            style: Common.getWizardSubmitButtonStyle(),
                            onPressed: _submitForm,
                            child: Text(
                              'SUBMIT',
                              style: Common.getWizardSubmitButtonTextStyle(),
                            ),
                          ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Padding buildCourseAutoCompletePadding(
      Iterable<Course> options, AutocompleteOnSelected<Course> onSelected) {
    return Padding(
      padding: const EdgeInsets.only(left: 0.0, right: 30.0),
      child: Material(
        child: Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(8.0),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.1),
                blurRadius: 4.0,
                spreadRadius: 2.0,
              ),
            ],
          ),
          constraints: const BoxConstraints(
            maxHeight: 200.0,
          ),
          child: ListView.builder(
            padding: EdgeInsets.zero,
            itemCount: options.length,
            itemBuilder: (BuildContext context, int index) {
              final option = options.elementAt(index);
              return GestureDetector(
                onTap: () {
                  onSelected(option);
                },
                child: ListTile(
                  leading: Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      image: DecorationImage(
                        image: AssetImage(option.image),
                        fit: BoxFit.cover,
                      ),
                    ),
                  ),
                  title: Text(
                    option.name,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Type: ${option.type}',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[600],
                        ),
                      ),
                      Text(
                        'Level: ${option.level}',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                  trailing: Icon(
                    Icons.arrow_forward,
                    color: Colors.grey[600],
                  ),
                ),
              );
            },
          ),
        ),
      ),
    );
  }

  Padding buildInstitutionAutoCompletePadding(Iterable<Institution> options,
      AutocompleteOnSelected<Institution> onSelected) {
    return Padding(
      padding: const EdgeInsets.only(left: 0.0, right: 30.0),
      child: Material(
        child: Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(8.0),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.1),
                blurRadius: 4.0,
                spreadRadius: 2.0,
              ),
            ],
          ),
          constraints: const BoxConstraints(
            maxHeight: 200.0,
          ),
          child: ListView.builder(
            padding: EdgeInsets.zero,
            itemCount: options.length,
            itemBuilder: (BuildContext context, int index) {
              final option = options.elementAt(index);
              return GestureDetector(
                onTap: () {
                  onSelected(option);
                },
                child: ListTile(
                  leading: Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      image: DecorationImage(
                        image: option.image.contains("assets/images/")
                            ? AssetImage(option.image)
                            : NetworkImage(option.image)
                                as ImageProvider<Object>,
                        fit: BoxFit.cover,
                      ),
                    ),
                  ),
                  title: Text(
                    option.name,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Cell Number: ${option.cellNumber}',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[600],
                        ),
                      ),
                      Text(
                        'Accreditation Number: ${option.accreditedNumber}',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                  trailing: Icon(
                    Icons.arrow_forward,
                    color: Colors.grey[600],
                  ),
                ),
              );
            },
          ),
        ),
      ),
    );
  }

  Padding buildAutoCompletePadding(
      Iterable<String> options, AutocompleteOnSelected<String> onSelected) {
    return Padding(
      padding: const EdgeInsets.only(left: 0.0, right: 30.0),
      child: Material(
        child: Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(8.0),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.1),
                blurRadius: 4.0,
                spreadRadius: 2.0,
              ),
            ],
          ),
          constraints: const BoxConstraints(
            maxHeight: 200.0,
          ),
          child: ListView.builder(
            padding: EdgeInsets.zero,
            itemCount: options.length,
            itemBuilder: (BuildContext context, int index) {
              final option = options.elementAt(index);
              return GestureDetector(
                onTap: () {
                  onSelected(option);
                },
                child: ListTile(
                  title: Text(option),
                  trailing: const Icon(Icons.arrow_forward),
                ),
              );
            },
          ),
        ),
      ),
    );
  }

  InputDecoration buildAutoCompleteDecoration(String label, String hint) {
    return InputDecoration(
      hintText: hint,
      labelText: label,
      filled: true,
      fillColor: Colors.grey[50],
      labelStyle: const TextStyle(
        color: Common.primaryColor,
        fontFamily: "DefaultFont",
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: const BorderSide(color: Colors.orange),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: const BorderSide(color: Colors.black),
      ),
    );
  }
}
