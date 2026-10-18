import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:html/parser.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:tertiary_verify/service/tertiary_verify_service.dart';

class Common {
  static const Color primaryColor = Color.fromARGB(255, 13, 51, 29);
  static const Color grey = Color.fromARGB(255, 128, 128, 128);

  static const Color dividerGreyColor = Color.fromARGB(62, 128, 128, 128);

  static ButtonStyle getButtonStyle() {
    return ElevatedButton.styleFrom(
      foregroundColor: Colors.white,
      backgroundColor: Colors.orange,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10.0),
        side: const BorderSide(color: primaryColor),
      ),
      elevation: 8,
      //padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 15),
    );
  }

static ButtonStyle getWizardButtonStyle() {
  return ElevatedButton.styleFrom(
    foregroundColor: Colors.orange,
    backgroundColor: primaryColor,
    padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 15),
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(15.0),
      side: const BorderSide(color: Colors.orange),
    ),
    elevation: 5,
    minimumSize: const Size(100, 0), // Set the minimum width (150 in this case)
  );
}

static ButtonStyle getWizardSubmitButtonStyle() {
  return ElevatedButton.styleFrom(
    foregroundColor: primaryColor,
    backgroundColor:  Colors.orange,
    padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 15),
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(15.0),
      side: const BorderSide(color: primaryColor),
    ),
    elevation: 5,
    minimumSize: const Size(100, 0), // Set the minimum width (150 in this case)
  );
}


  static InputDecoration buildInputDecoration(String label, String hint) {
    return InputDecoration(
      labelText: label,
      hintText: hint,
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

  static TextStyle getButtonTextStyle() {
    return const TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
        fontFamily: "DefaultFont",
        color: primaryColor);
  }

  static TextStyle getWizardButtonTextStyle() {
    return const TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
        fontFamily: "DefaultFont",
        color: Colors.orange,);
  }

   static TextStyle getWizardSubmitButtonTextStyle() {
    return const TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
        fontFamily: "DefaultFont",
        color: primaryColor,);
  }

  static Column buildHomeCard(String title, String subTitle, String imagePath) {
    return Column(
      children: [
        Card(
          margin: const EdgeInsets.fromLTRB(2, 0, 2, 8),
          clipBehavior: Clip.antiAliasWithSaveLayer,
          elevation: 4.0,
          shape: const RoundedRectangleBorder(
            borderRadius: BorderRadius.only(
              bottomLeft: Radius.circular(25.0),
              bottomRight: Radius.circular(25.0),
            ),
          ),
          child: Column(
            children: [
              Container(
                height: 180,
                width: double.infinity,
                decoration: BoxDecoration(
                  image: DecorationImage(
                    image: AssetImage(imagePath),
                    fit: BoxFit.contain,
                  ),
                ),
                child: BackdropFilter(
                  filter: ImageFilter.blur(sigmaX: 0, sigmaY: 0),
                  child: Container(
                    color: Common.primaryColor.withOpacity(0.3),
                  ),
                ),
              ),
              Container(
                width: double.infinity,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [
                      Common.primaryColor
                          .withOpacity(0.3), // Transparent at the top
                      Common.primaryColor
                          .withOpacity(1.0), // Your color with full opacity
                    ],
                  ),
                ),
                child: Center(
                  child: Column(
                    children: [
                      Padding(
                        padding: const EdgeInsets.fromLTRB(0, 5, 0, 0),
                        child: Text(
                          title,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 25,
                            fontFamily: "DefaultFont",
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.fromLTRB(0, 1, 0, 5),
                        child: Center(
                          child: Text(
                            subTitle,
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 10,
                              fontFamily: "DefaultFont",
                            ),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      ),
                      const SizedBox(
                        height: 10,
                      )
                    ],
                  ),
                ),
              )
            ],
          ),
        ),
      ],
    );
  }

  static Column buildFormCardHeader(
      String title, String subTitle, String imagePath) {
    return Column(
      children: [
        Card(
          margin: const EdgeInsets.fromLTRB(2, 0, 2, 8),
          clipBehavior: Clip.antiAliasWithSaveLayer,
          elevation: 4.0,
          shape: const RoundedRectangleBorder(
            borderRadius: BorderRadius.only(
              bottomLeft: Radius.circular(25.0),
              bottomRight: Radius.circular(25.0),
            ),
          ),
          child: Column(
            children: [
              Container(
                height: 120,
                width: double.infinity,
                decoration: BoxDecoration(
                  image: DecorationImage(
                    image: AssetImage(imagePath),
                    fit: BoxFit.contain,
                  ),
                ),
                child: BackdropFilter(
                  filter: ImageFilter.blur(sigmaX: 0, sigmaY: 0),
                  child: Container(
                    color: Common.primaryColor.withOpacity(0.5),
                  ),
                ),
              ),
              Container(
                width: double.infinity,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [
                      Common.primaryColor
                          .withOpacity(0.5), // Transparent at the top
                      Common.primaryColor
                          .withOpacity(1.0), // Your color with some opacity
                    ],
                  ),
                ),
                child: Center(
                  child: Column(
                    children: [
                      Padding(
                        padding: const EdgeInsets.fromLTRB(0, 5, 0, 0),
                        child: Text(
                          title,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 25,
                            fontFamily: "DefaultFont",
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.fromLTRB(0, 1, 0, 5),
                        child: Center(
                          child: Text(
                            subTitle,
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 10,
                              fontFamily: "DefaultFont",
                            ),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      ),
                      const SizedBox(
                        height: 10,
                      )
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  static BottomAppBar get buildFooter {
    const String companyName = "Mashita Technologies (Pty) Ltd";

    String copyrightText =
        "© ${DateTime.now().year} $companyName. All rights reserved. Patents and copyrights protected.";

    return BottomAppBar(
      color: Colors.white,
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 10),
        child: Row(
          children: [

            Expanded(
              flex: 1,
              child: Container(
                margin: const EdgeInsets.symmetric(vertical: 2),
                child: Image.asset(
                  'assets/images/dheat.png',
                  fit: BoxFit.fill,
                ),
              ),
            ),
            Container(
              width: 1,
              color: Colors.grey,
              height: 60,
              margin: const EdgeInsets.symmetric(horizontal: 10),
            ),
            Expanded(
              child: Text(
                copyrightText,
                style: const TextStyle(
                  fontFamily: "DefaultFont",
                  fontSize: 10,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  static AppBar buildHomeAppBar(BuildContext context) {
    return AppBar(
      backgroundColor: Common.primaryColor,
      title: const Text(
        "Tertiary Verify",
        style: TextStyle(
          fontFamily: "DefaultFont", // Use a custom font like "Pacifico"
          fontWeight: FontWeight.bold,
          color: Colors.white,
        ),
      ),
      centerTitle: true,
      leading: const Icon(
        Icons.school_rounded,
        color: Colors.white,
      ),
      actions: [
        IconButton(
          onPressed: () {
            Navigator.pushNamed(context, "home");
          },
          icon: const Icon(
            Icons.home_rounded,
            color: Colors.white,
          ),
        ),
      ],
    );
  }

  static AppBar buildAppBar(BuildContext context) {
    return AppBar(
      backgroundColor: Common.primaryColor,
      title: const Text(
        "Tertiary Verify",
        style: TextStyle(
          color: Colors.white,
          fontFamily: "DefaultFont",
          fontWeight: FontWeight.bold,
        ),
      ),
      centerTitle: true,
      leading: IconButton(
        icon: const Icon(
          Icons.arrow_back_rounded,
          color: Colors.white,
        ),
        onPressed: () {
          Navigator.pop(context);
        },
      ),
      actions: [
        IconButton(
          onPressed: () {
            Navigator.pushNamed(context, "home");
          },
          icon: const Icon(
            Icons.home_rounded,
            color: Colors.white,
          ),
        ),
      ],
    );
  }

  static Column buildProgressBar() {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: <Widget>[
        Container(
          width: 100.0,
          height: 100.0,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(50.0),
            gradient: const LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors: [
                Common.primaryColor,
                Colors.orange,
              ],
            ),
          ),
          child: Center(
            child: SpinKitDoubleBounce(
              itemBuilder: (BuildContext context, int index) {
                return DecoratedBox(
                  decoration: BoxDecoration(
                    color: index.isEven ? Colors.orange : Common.primaryColor,
                    shape: BoxShape.circle,
                  ),
                );
              },
            ),
          ),
        ),
        const SizedBox(height: 20.0),
        const Text(
          'Loading...',
          style: TextStyle(fontSize: 20.0, fontWeight: FontWeight.bold),
        ),
      ],
    );
  }

  static String parseHtmlString(String htmlString) {
    final document = parse(htmlString);
    final String? parsedString =
        parse(document.body?.text).documentElement?.text;
    if (parsedString == null) {
      return htmlString;
    } else {
      return parsedString;
    }
  }

  static OutlinedButton buildCloseOutlinedButton(
      BuildContext context, String navTo, title, eventDesc) {
    final sessionProvider = Provider.of<SessionProvider>(context);
    return OutlinedButton(
      onPressed: () {
        TertiaryVerifyService.saveStatistic(
            title: title,
            description: eventDesc,
            sessionId: sessionProvider.sessionId);
        if (navTo.isEmpty) {
          Navigator.pop(context);
        } else {
          Navigator.pushNamed(context, navTo);
        }
      },
      style: OutlinedButton.styleFrom(
        side: const BorderSide(color: primaryColor),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20.0),
        ),
      ),
      child: const Text(
        'CLOSE',
        style: TextStyle(color: primaryColor),
      ),
    );
  }
}
