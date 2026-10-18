import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/pages/common.dart';
import 'package:tertiary_verify/pages/success_bottom_sheet.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:tertiary_verify/validator/common_validator.dart';

import '../service/tertiary_verify_service.dart';
import 'failure_bottom_sheet.dart';

class ContactPage extends StatefulWidget {
  const ContactPage({super.key});

  @override
  _ContactPageState createState() => _ContactPageState();
}

class _ContactPageState extends State<ContactPage> {
  final _formKey = GlobalKey<FormState>();
  late String _name;
  late String _email;
  late String _phone;
  late String _message;

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
                "Contact us",
                "Your questions, suggestions, and comments are important to us. We'd love to hear from you.",
                "assets/images/contact_us.png"),
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(height: 20),
                    TextFormField(
                       decoration: Common.buildInputDecoration(
                          'Full Name', 'Enter full name'),
                      validator: CommonValidator.validateFullName,
                      onSaved: (value) {
                        _name = value!;
                      },
                    ),
                    const SizedBox(height: 20),
                    TextFormField(
                     decoration: Common.buildInputDecoration(
                          'Email', 'Enter email address'),
                      keyboardType: TextInputType.emailAddress,
                      validator: CommonValidator.validateEmail,
                      onSaved: (value) {
                        _email = value!;
                      },
                    ),
                    const SizedBox(height: 20),
                    TextFormField(
                      decoration: Common.buildInputDecoration(
                          'Phone Number', 'Enter phone number'),
                      keyboardType: TextInputType.phone,
                      validator: CommonValidator.validateCellNumber,
                      onSaved: (value) {
                        _phone = value!;
                      },
                    ),
                    const SizedBox(height: 20),
                    TextFormField(
                       decoration: Common.buildInputDecoration(
                          'Message', 'Enter message'),
                      maxLines: 3,
                      validator: CommonValidator.validateMessage,
                      onSaved: (value) {
                        _message = value!;
                      },
                    ),
                    const SizedBox(height: 20),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        style: Common.getButtonStyle(),
                        onPressed: () async {
                          TertiaryVerifyService.saveStatistic(
                            title: 'Contact us',
                              description: 'Contact us, Submit Button Clicked',
                              sessionId: sessionProvider.sessionId);

                          if (_formKey.currentState!.validate()) {
                            _formKey.currentState!.save();

                            bool isSent = await TertiaryVerifyService
                                .sendContactUsMessage(
                                    name: _name,
                                    email: _email,
                                    phone: _phone,
                                    message: _message);

                            if (isSent) {
                              showModalBottomSheet(
                                context: context,
                                builder: (BuildContext context) {
                                  return const SuccessBottomSheet(
                                    title: "Message sent",
                                    message:
                                        "Message send successfully, thank you.",
                                    navTo: "home",
                                    eventTitle: 'Contact us',
                                    eventDesc: "Success Bottom Sheet, Close Button Clicked On Contact us page",
                                  );
                                },
                              );
                            } else {
                              showModalBottomSheet(
                                context: context,
                                builder: (BuildContext context) {
                                  return const FailureBottomSheet(
                                    title: "Service unavailable",
                                    message:
                                        "We are unable to send messages at this time, please try again later.",
                                    navTo: "home",
                                    eventTitle: 'Contact us',
                                    eventDesc: "Failure Bottom Sheet, Close Button Clicked On Contact us page",
                                  );
                                },
                              );
                            }

                            // Send data to backend or display confirmation message
                          }
                        },
                        child: Text(
                          'SUBMIT',
                          style: Common.getButtonTextStyle(),
                        ),
                        // Set the overlay color of the button to red
                      ),
                    ),
                    const SizedBox(height: 20),
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
