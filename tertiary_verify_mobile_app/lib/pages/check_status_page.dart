import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/pages/check_status_bottom_sheet.dart';
import 'package:tertiary_verify/pages/common.dart';
import 'package:tertiary_verify/service/session_provider.dart';

import '../model/reported_institution.dart';
import '../service/tertiary_verify_service.dart';
import 'failure_bottom_sheet.dart';

class CheckStatusPage extends StatefulWidget {
  const CheckStatusPage({super.key});

  @override
  _CheckStatusPageState createState() => _CheckStatusPageState();
}

class _CheckStatusPageState extends State<CheckStatusPage> {
  final _formKey = GlobalKey<FormState>();
  late String _refNumber;

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
              "Check status",
              "Use Reference number to view progress report",
              "assets/images/check_status.png",
            ),
            Column(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Padding(
                  padding: const EdgeInsets.fromLTRB(8, 10, 8, 8),
                  child: Form(
                    key: _formKey,
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Expanded(
                          child: Padding(
                            padding: const EdgeInsets.only(right: 8.0),
                            child: TextFormField(
                              decoration: InputDecoration(
                                labelText: 'Reference Number',
                                labelStyle: const TextStyle(
                                  color: Common.primaryColor,
                                  fontFamily: "DefaultFont",
                                ),
                                focusedBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(10),
                                  borderSide:
                                      const BorderSide(color: Colors.orange),
                                ),
                                enabledBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(10),
                                  borderSide:
                                      const BorderSide(color: Colors.black),
                                ),
                                fillColor: Colors.grey[50],
                                filled: true,
                                suffixIcon: GestureDetector(
                                  onTap: () {
                                    TertiaryVerifyService.saveStatistic(
                                        title: 'Check status',
                                        description:
                                            'Reference number dialog, Info Button Clicked',
                                        sessionId: sessionProvider.sessionId);
                                    showDialog(
                                      context: context,
                                      builder: (context) => Dialog(
                                        shape: RoundedRectangleBorder(
                                          borderRadius:
                                              BorderRadius.circular(12.0),
                                        ),
                                        child: Container(
                                          padding: const EdgeInsets.all(16.0),
                                          child: Column(
                                            mainAxisSize: MainAxisSize.min,
                                            children: [
                                              const Text(
                                                "Reference number",
                                                style: TextStyle(
                                                  fontSize: 20.0,
                                                  fontWeight: FontWeight.bold,
                                                  fontFamily: "DefaultFont",
                                                  color: Common.primaryColor,
                                                ),
                                              ),
                                              const SizedBox(height: 12.0),
                                              const Text(
                                                "A reference number is a unique identifier assigned to "
                                                "any bogus institution or bogus course reported by individual.",
                                                style: TextStyle(
                                                  fontSize: 16.0,
                                                  fontFamily: "DefaultFont",
                                                  color: Colors.black,
                                                ),
                                                textAlign: TextAlign.center,
                                              ),
                                              const SizedBox(height: 12.0),
                                              const Text(
                                                "Reference number is provided to you "
                                                "after you have completed bogus institution or bogus "
                                                "course  report form. This number is unique to your bogus report.",
                                                style: TextStyle(
                                                  fontSize: 16.0,
                                                  fontFamily: "DefaultFont",
                                                  color: Colors.black,
                                                ),
                                                textAlign: TextAlign.center,
                                              ),
                                              const SizedBox(height: 12.0),
                                              OutlinedButton(
                                                onPressed: () {
                                                  TertiaryVerifyService
                                                      .saveStatistic(
                                                          title: 'Check status',
                                                          description:
                                                              'Reference number dialog, Ok Button Clicked',
                                                          sessionId:
                                                              sessionProvider
                                                                  .sessionId);
                                                  Navigator.pop(context);
                                                },
                                                style: OutlinedButton.styleFrom(
                                                  side: const BorderSide(
                                                      color:
                                                          Common.primaryColor),
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            20.0),
                                                  ),
                                                ),
                                                child: const Text(
                                                  'OK',
                                                  style: TextStyle(
                                                    color: Common.primaryColor,
                                                    fontFamily: "DefaultFont",
                                                  ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ),
                                    );
                                  },
                                  child: const Icon(
                                    Icons.info_outline,
                                    color: Common.primaryColor,
                                  ),
                                ),
                              ),
                              validator: (value) {
                                if (value!.isEmpty) {
                                  return 'Please enter your reference number';
                                }
                                return null;
                              },
                              onSaved: (value) {
                                _refNumber = value!;
                              },
                            ),
                          ),
                        ),
                        SizedBox(
                          width: 75,
                          height: 60,
                          child: Container(
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(10.0),
                              color: Colors.orange,
                              border: Border.all(
                                color: Common.primaryColor
                              ),
                            ),
                            child: Material(
                              color: Colors.transparent,
                              child: InkWell(
                                borderRadius: BorderRadius.circular(10.0),
                                // Apply the same border radius as the container
                                onTap: () async {
                                  TertiaryVerifyService.saveStatistic(
                                      title: 'Check status',
                                      description: 'Check status',
                                      sessionId: sessionProvider.sessionId);

                                  if (_formKey.currentState!.validate()) {
                                    _formKey.currentState!.save();

                                    ReportedInstitution reportedInstitution =
                                        await TertiaryVerifyService
                                            .findReportedInstitution(
                                                _refNumber);

                                    if (reportedInstitution.success) {
                                      showModalBottomSheet(
                                        context: context,
                                        builder: (BuildContext context) {
                                          return CheckStatusBottomSheet(
                                            reportedInstitution:
                                                reportedInstitution,
                                          );
                                        },
                                      );
                                    } else {
                                      showModalBottomSheet(
                                        context: context,
                                        builder: (BuildContext context) {
                                          return FailureBottomSheet(
                                            title: "Error",
                                            message: reportedInstitution
                                                .errorMessage,
                                            navTo: "home",
                                            eventTitle: 'Check Status',
                                            eventDesc:
                                                "Failure Bottom Sheet, Close Button Clicked On Check Status page",
                                          );
                                        },
                                      );
                                    }
                                  }
                                },
                                child: Container(
                                  padding: const EdgeInsets.all(16.0),
                                  child: const Icon(
                                    Icons.search_rounded,
                                    size: 40,
                                    color: Common.primaryColor,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
