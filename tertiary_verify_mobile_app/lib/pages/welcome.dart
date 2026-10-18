import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/model/welcome_data.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:tertiary_verify/service/tertiary_verify_service.dart';
import 'package:url_launcher/url_launcher_string.dart';

import 'common.dart';

class Welcome extends StatefulWidget {
  const Welcome({super.key});

  @override
  _WelcomeState createState() => _WelcomeState();
}

class _WelcomeState extends State<Welcome> {
  late Future<WelcomeData> _welcomeData;

  @override
  void initState() {
    super.initState();
    _welcomeData = TertiaryVerifyService.getWelcomeData();
  }

  // Function to launch the URL
  Future<void> launchURL(String url) async {
    print("URL: " + url);
    if (await canLaunchUrlString(url)) {
      await launchUrlString(url);
    } else {
      throw 'Could not launch $url';
    }
  }

  @override
  Widget build(BuildContext context) {
    final sessionProvider = Provider.of<SessionProvider>(context);

    return Scaffold(
      backgroundColor: Colors.white,
      appBar: Common.buildHomeAppBar(context),
      body: Column(
        children: [
          const Expanded(
            child: Center(
              child: SingleChildScrollView(
                child: Center(
                  child: Padding(
                    padding: EdgeInsets.all(8.0),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(
                          "Welcome!",
                          textAlign: TextAlign.center,
                          style: TextStyle(
                            fontSize: 35,
                            color: Common.primaryColor,
                            fontWeight: FontWeight.bold,
                            fontFamily: "DefaultFont",
                          ),
                        ),
                        SizedBox(
                          height: 15,
                        ),
                        Text(
                          "Tertiary Verify App assists with the "
                          "verification of higher learning institutions",
                          textAlign: TextAlign.center,
                          style: TextStyle(
                            color: Common.primaryColor,
                            fontFamily: "DefaultFont",
                          ),
                        ),
                        SizedBox(
                          height: 15,
                        ),
                        CircleAvatar(
                          radius: 150,
                          backgroundColor: Colors.transparent,
                          backgroundImage: AssetImage(
                            "assets/images/welcome.png",
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ),
          ElevatedButton(
            style: Common.getButtonStyle(),
            onPressed: () {
              TertiaryVerifyService.saveStatistic(
                  title: 'Welcome',
                  description: 'Get started button Clicked',
                  sessionId: sessionProvider.sessionId);

              Navigator.pushNamed(context, "home");
            },
            child: Text(
              "GET STARTED",
              style: Common.getButtonTextStyle(),
            ),
          ),
          const SizedBox(
            height: 15,
          ),
          FutureBuilder<WelcomeData>(
            future: _welcomeData,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Text(
                  'LOADING...',
                  style: TextStyle(
                    color: Colors.grey,
                    fontFamily: "DefaultFont",
                    fontSize: 14,
                  ),
                  textAlign: TextAlign.center,
                );
              } else if (snapshot.hasError) {
                return const Text(
                  'Error loading data privacy URL',
                  style: TextStyle(
                    color: Colors.red,
                    fontFamily: "DefaultFont",
                    fontSize: 14,
                  ),
                  textAlign: TextAlign.center,
                );
              } else {
                final String dataPrivacyUrl = snapshot.data!.dataPrivacyUrl;
                return TextButton(
                  onPressed: () {
                    TertiaryVerifyService.saveStatistic(
                        title: 'Data privacy',
                        description: 'Data privacy link opened',
                        sessionId: sessionProvider.sessionId);
                    launchURL(dataPrivacyUrl);
                  },
                  child: const Text(
                    "DATA PRIVACY",
                    style: TextStyle(
                      color: Colors.blue,
                      fontFamily: "DefaultFont",
                      fontSize: 14,
                    ),
                  ),
                );
              }
            },
          ),
          const SizedBox(
            height: 15,
          ),
        ],
      ),
      bottomNavigationBar: Common.buildFooter,
    );
  }
}
