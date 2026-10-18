import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/model/reported_institution.dart';
import 'package:tertiary_verify/pages/common.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:tertiary_verify/service/tertiary_verify_service.dart';

class CheckStatusBottomSheet extends StatefulWidget {
  final ReportedInstitution reportedInstitution;

  const CheckStatusBottomSheet({super.key, required this.reportedInstitution});

  @override
  _CheckStatusBottomSheetState createState() => _CheckStatusBottomSheetState();
}

class _CheckStatusBottomSheetState extends State<CheckStatusBottomSheet>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<Offset> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    );
    _animation = Tween<Offset>(
      begin: const Offset(0, 1),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.easeOut,
    ));
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final sessionProvider = Provider.of<SessionProvider>(context);
    return SlideTransition(
      position: _animation,
      child: Container(
        margin: const EdgeInsets.fromLTRB(2, 2, 2, 2),
        height: 550,
        decoration: BoxDecoration(
          color: Common.primaryColor.withOpacity(0.1),
          borderRadius: const BorderRadius.only(
            topLeft: Radius.circular(25),
            topRight: Radius.circular(25),
          ),
          border: Border.all(color: Common.primaryColor),
        ),
        child: Stack(
          children: [
            Align(
              alignment: Alignment.topRight,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () {
                    TertiaryVerifyService.saveStatistic(
                      title: 'Check status',
                        description:
                            'Check status bottom sheet, Close Icon Clicked',
                        sessionId: sessionProvider.sessionId);
                    Navigator.pop(context);
                  },
                ),
              ),
            ),
            Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Padding(
                  padding: EdgeInsets.all(10.0),
                  child: Text(
                    'Status details',
                    style: TextStyle(
                      fontSize: 20,
                      fontFamily: "DefaultFont",
                    ),
                  ),
                ),
                ListView(
                  shrinkWrap: true,
                  children: [
                    ListTile(
                      leading: const Icon(Icons.check_box_outlined),
                      title: const Text(
                        'Status',
                        style: TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                      subtitle: Text(
                        widget.reportedInstitution.status,
                        style: const TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                    ),
                    ListTile(
                      leading: const Icon(Icons.house_outlined),
                      title: const Text(
                        'Institution name',
                        style: TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                      subtitle: Text(
                        widget.reportedInstitution.institutionName,
                        style: const TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                    ),
                    ListTile(
                      leading: const Icon(Icons.cancel_outlined),
                      title: const Text(
                        'Report type',
                        style: TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                      subtitle: Text(
                        widget.reportedInstitution.reportType,
                        style: const TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                    ),
                    ListTile(
                      leading: const Icon(Icons.numbers_outlined),
                      title: const Text(
                        'Reference number',
                        style: TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                      subtitle: Text(
                        widget.reportedInstitution.refNumber,
                        style: const TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                    ),
                    const ListTile(
                      leading: Icon(
                        Icons.info_outline,
                        color: Colors.blue,
                      ),
                      subtitle: Text(
                        "We have sent an email containing detailed information regarding the "
                        "status of the reported institution. Please check your email.",
                        style: TextStyle(
                          fontFamily: "DefaultFont",
                          color: Colors.blue,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(
                  height: 10,
                ),
                Common.buildCloseOutlinedButton(context, "home","Check Status Bottom Sheet", "Check Status Bottom Sheet, Close Button Clicked"),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
