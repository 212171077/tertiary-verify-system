import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/pages/common.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:tertiary_verify/service/tertiary_verify_service.dart';

import '../icon/home_icon.dart';
import '../model/home_option.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    final sessionProvider = Provider.of<SessionProvider>(context);
    List<HomeOption> homeOptions = [
      HomeOption("Search Institution", Icons.account_balance_rounded,
          "search_institution"),
      HomeOption(
          "Search Course", Icons.school_rounded, "search_course"),
      HomeOption("Report Institution", Icons.report_rounded, "report_institution"),
      HomeOption("Report Course", Icons.report_rounded, "report_course"),
      HomeOption("Contact us", Icons.message_rounded, "contact_us"),
      HomeOption("Check Status", Icons.checklist_rtl_rounded, "check_status"),
    ];

    return Scaffold(
      backgroundColor: Colors.white,
      appBar: Common.buildHomeAppBar(context),
      body: Column(
        children: [
          Common.buildHomeCard(
              "How can we help?",
              "Select an option from below",
              "assets/images/how_can_we_help.png"),
          Expanded(
            child: GridView.count(
              crossAxisCount: 3,
              children: List.generate(homeOptions.length, (index) {
                HomeOption option = homeOptions[index];
                return InkWell(
                  onTap: () {
                     TertiaryVerifyService
                                .saveStatistic(
                                  title: option.title,
                                    description: '${option.title} Clicked',
                                    sessionId: sessionProvider.sessionId);
                    Navigator.pushNamed(context, option.route);
                  },
                  child: Card(
                    elevation: 5,
                    margin: const EdgeInsets.all(10),
                    shape: const RoundedRectangleBorder(
                      side: BorderSide(
                          color: Colors.grey, width: 1),
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(25.0),
                        topRight: Radius.circular(25.0),
                        bottomLeft: Radius.circular(25.0),
                        bottomRight: Radius.circular(25.0),
                      ),
                    ),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Center(
                          child: HomeIcon(
                            size: 48,
                            color: Common.primaryColor,
                            label: option.title,
                            icon: option.icon,
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              }),
            ),
          ),
        ],
      ),
      bottomNavigationBar: Common.buildFooter,
    );
  }
}
