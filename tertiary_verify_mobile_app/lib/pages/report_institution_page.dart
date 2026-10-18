import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/pages/success_bottom_sheet.dart';
import 'package:tertiary_verify/service/session_provider.dart';

import '../service/tertiary_verify_service.dart';
import '../validator/common_validator.dart';
import 'common.dart';
import 'failure_bottom_sheet.dart';

class ReportInstitutionPage extends StatefulWidget {
  const ReportInstitutionPage({super.key});

  @override
  _ReportInstitutionPageState createState() => _ReportInstitutionPageState();
}

class _ReportInstitutionPageState extends State<ReportInstitutionPage> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _nameController;
  late TextEditingController _surnameController;
  late TextEditingController _emailController;
  late TextEditingController _cellController;

  late TextEditingController _institutionController;
  late TextEditingController _knownAsController;
  late TextEditingController _additionalInfo;
  late TextEditingController _address1Controller;
  late TextEditingController _address2Controller;
  late TextEditingController _address3Controller;
  late TextEditingController _suburbController;
  late TextEditingController _postalCodeController;

  final List<String> _provinces = [
    'Eastern Cape',
    'Free State',
    'Gauteng',
    'KwaZulu-Natal',
    'Limpopo',
    'Mpumalanga',
    'North West',
    'Northern Cape',
    'Western Cape'
  ];
  String _province = '';
  int _currentPageIndex = 0;
  bool _isProvinceSelected = false;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController();
    _surnameController = TextEditingController();
    _emailController = TextEditingController();
    _cellController = TextEditingController();

    _institutionController = TextEditingController();
    _knownAsController = TextEditingController();
    _additionalInfo = TextEditingController();
    _address1Controller = TextEditingController();
    _address2Controller = TextEditingController();
    _address3Controller = TextEditingController();
    _suburbController = TextEditingController();
    _postalCodeController = TextEditingController();
  }

  @override
  void dispose() {
    _nameController.dispose();
    _surnameController.dispose();
    _emailController.dispose();
    _cellController.dispose();

    _institutionController.dispose();
    _knownAsController.dispose();
    _additionalInfo.dispose();
    _address1Controller.dispose();
    _address2Controller.dispose();
    _address3Controller.dispose();
    _suburbController.dispose();
    _postalCodeController.dispose();

    super.dispose();
  }

  Future<void> _submitForm() async {
    final sessionProvider = Provider.of<SessionProvider>(context);
    TertiaryVerifyService.saveStatistic(
        title: 'Report Institution',
        description: 'Report Institution, Submit Button Clicked',
        sessionId: sessionProvider.sessionId);

    if (_formKey.currentState!.validate()) {
      bool isSent = await TertiaryVerifyService.reportBogusInstitution(
          userName: _nameController.text,
          userSurname: _surnameController.text,
          userEmail: _emailController.text,
          userCellNumber: _cellController.text,
          instName: _institutionController.text,
          instKnownAs: _knownAsController.text,
          instAddressLine1: _address1Controller.text,
          instAddressLine2: _address2Controller.text,
          instAddressLine3: _address3Controller.text,
          provinceName: _province,
          instSuburb: _suburbController.text,
          instPostalCode: _postalCodeController.text,
          additionalInfo: _additionalInfo.text);

      if (isSent) {
        showModalBottomSheet(
          context: context,
          builder: (BuildContext context) {
            return const SuccessBottomSheet(
              title: "Institution reported",
              message: "Thank you for reporting this institution. "
                  "We appreciate your efforts in helping us maintain "
                  "the standards and legitimacy of institutions in our system. "
                  "An acknowledgement email will be sent to you shortly with "
                  "further information regarding your report.",
              navTo: "home",
              eventTitle: 'Report Institution',
              eventDesc:
                  "Success Bottom Sheet, Close Button Clicked On Report Institution Page",
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
              eventTitle: 'Report Institution',
              eventDesc:
                  "Failure Bottom Sheet, Close Button Clicked On Report Institution",
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
              "Report Bogus Institution",
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
                        'Institution details',
                        style: TextStyle(
                          fontSize: 25.0,
                          fontWeight: FontWeight.bold,
                          fontFamily: "DefaultFont",
                          color: Common.primaryColor,
                        ),
                      ),
                    if (_currentPageIndex == 1) const SizedBox(height: 8.0),
                    if (_currentPageIndex == 1)
                      TextFormField(
                        controller: _institutionController,
                        decoration: Common.buildInputDecoration(
                            'Institution Name', 'Enter institution name'),
                        validator: (value) {
                          if (value == null ||
                              value.isEmpty ||
                              value.trim().isEmpty) {
                            return 'Please enter institution name';
                          }
                          return null;
                        },
                      ),
                    if (_currentPageIndex == 1) const SizedBox(height: 20),
                    if (_currentPageIndex == 1)
                      TextFormField(
                        controller: _knownAsController,
                        decoration: Common.buildInputDecoration(
                            'Known as', 'Enter know as name'),
                        validator: (value) {
                          if (value == null ||
                              value.isEmpty ||
                              value.trim().isEmpty) {
                            return 'Please enter institution name';
                          }
                          return null;
                        },
                      ),
                    if (_currentPageIndex == 1) const SizedBox(height: 20),
                    if (_currentPageIndex == 1)
                      TextFormField(
                        controller: _additionalInfo,
                        decoration: Common.buildInputDecoration(
                            'Additional Info', 'Enter additional information'),
                        validator: (value) {
                          if (value == null ||
                              value.isEmpty ||
                              value.trim().isEmpty) {
                            return 'Please enter additional information';
                          }
                          return null;
                        },
                      ),
                    if (_currentPageIndex == 2)
                      const Text(
                        'Institution address',
                        style: TextStyle(
                          fontSize: 25.0,
                          fontWeight: FontWeight.bold,
                          fontFamily: "DefaultFont",
                          color: Common.primaryColor,
                        ),
                      ),
                    if (_currentPageIndex == 2) const SizedBox(height: 15.0),
                    if (_currentPageIndex == 2)
                      TextFormField(
                        controller: _address1Controller,
                        decoration: Common.buildInputDecoration(
                            'Address line 1', 'Enter address line 1'),
                        validator: (value) {
                          if (value == null ||
                              value.isEmpty ||
                              value.trim().isEmpty) {
                            return 'Please enter address line 1';
                          }
                          return null;
                        },
                      ),
                    if (_currentPageIndex == 2) const SizedBox(height: 20),
                    if (_currentPageIndex == 2)
                      TextFormField(
                        controller: _address2Controller,
                        decoration: Common.buildInputDecoration(
                            'Address line 2', 'Enter address line 2'),
                        validator: (value) {
                          if (value == null ||
                              value.isEmpty ||
                              value.trim().isEmpty) {
                            return 'Please enter address line 2';
                          }
                          return null;
                        },
                      ),
                    if (_currentPageIndex == 2) const SizedBox(height: 20),
                    if (_currentPageIndex == 2)
                      TextFormField(
                        controller: _address3Controller,
                        decoration: Common.buildInputDecoration(
                            'Address line 3', 'Enter address line 3'),
                        validator: (value) {
                          return null;
                        },
                      ),
                    if (_currentPageIndex == 2) const SizedBox(height: 20),
                    if (_currentPageIndex == 2)
                      Autocomplete<String>(
                        optionsBuilder: (textEditingValue) {
                          return _provinces.where((option) => option
                              .toLowerCase()
                              .contains(textEditingValue.text.toLowerCase()));
                        },
                        onSelected: (String selection) {
                          setState(() {
                            _province = selection;
                            _isProvinceSelected = true;
                          });
                        },
                        fieldViewBuilder: (BuildContext context,
                            TextEditingController textEditingController,
                            FocusNode focusNode,
                            VoidCallback onFieldSubmitted) {
                          return Column(
                            children: [
                              TextFormField(
                                  controller: textEditingController,
                                  focusNode: focusNode,
                                  onFieldSubmitted: (_) => onFieldSubmitted(),
                                  decoration: InputDecoration(
                                    hintText: 'Search for province',
                                    labelText: 'Province',
                                    filled: true,
                                    fillColor: Colors.grey[50],
                                    labelStyle: const TextStyle(
                                      color: Common.primaryColor,
                                      fontFamily: "DefaultFont",
                                    ),
                                    focusedBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(10),
                                      borderSide: const BorderSide(
                                          color: Colors.orange),
                                    ),
                                    enabledBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(10),
                                      borderSide:
                                          const BorderSide(color: Colors.black),
                                    ),
                                  ),
                                  validator: (value) {
                                    if (!_isProvinceSelected) {
                                      return 'Please select a province'; // Return the error message if institution is not selected
                                    }
                                    return null; // Return null if there are no validation errors
                                  }),
                            ],
                          );
                        },
                        displayStringForOption: (option) => option,
                      ),
                    if (_currentPageIndex == 2) const SizedBox(height: 20),
                    if (_currentPageIndex == 2)
                      TextFormField(
                        controller: _suburbController,
                        decoration: Common.buildInputDecoration(
                            'Suburb/Area', 'Enter suburb/area'),
                        validator: (value) {
                          if (value == null ||
                              value.isEmpty ||
                              value.trim().isEmpty) {
                            return 'Please enter suburb/area';
                          }
                          return null;
                        },
                      ),
                    if (_currentPageIndex == 2) const SizedBox(height: 20),
                    if (_currentPageIndex == 2)
                      TextFormField(
                        controller: _postalCodeController,
                        decoration: Common.buildInputDecoration(
                            'Postal code', 'Enter postal code'),
                        validator: CommonValidator.validatePostalCode,
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
                                  title: 'Report Institution',
                                  description:
                                      'Report Institution, Previous Button Clicked',
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
                        if (_currentPageIndex < 2)
                          ElevatedButton(
                            style: Common.getWizardButtonStyle(),
                            onPressed: () {
                              TertiaryVerifyService.saveStatistic(
                                  title: 'Report Institution',
                                  description:
                                      'Report Institution, Next Button Clicked',
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
                        if (_currentPageIndex == 2)
                          ElevatedButton(
                            style:  Common.getWizardSubmitButtonStyle(),
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
}
