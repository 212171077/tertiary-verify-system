import 'package:flutter/material.dart';
import 'package:tertiary_verify/pages/common.dart';

class FailureBottomSheet extends StatefulWidget {
  final String title;
  final String message;
  final String navTo;
  final String eventDesc;
  final String eventTitle;
  const FailureBottomSheet({
    super.key,
    required this.title,
    required this.message,
    required this.navTo,
    required this.eventDesc,
     required this.eventTitle,
  });

  @override
  _FailureBottomSheetState createState() => _FailureBottomSheetState();
}

class _FailureBottomSheetState extends State<FailureBottomSheet>
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
    double messageHeight = widget.message.length * 3.5;
    if (messageHeight < 200) {
      messageHeight = 200;
    }
    return SlideTransition(
      position: _animation,
      child: Container(
        margin: const EdgeInsets.fromLTRB(2, 2, 2, 2),
        height: messageHeight,
        decoration: BoxDecoration(
          color: Colors.deepOrange.withOpacity(0.1),
          borderRadius: const BorderRadius.only(
            topLeft: Radius.circular(25),
            topRight: Radius.circular(25),
          ),
          border: Border.all(color: Colors.red),
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
                    Navigator.pop(context);
                  },
                ),
              ),
            ),
            Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Text(
                    widget.title,
                    style: const TextStyle(
                      fontSize: 20,
                      fontFamily: "DefaultFont",
                    ),
                  ),
                ),
                ListView(
                  shrinkWrap: true,
                  children: [
                    ListTile(
                      leading:
                          const Icon(Icons.error_outline, color: Colors.red),
                      title: Text(
                        widget.message,
                        style: const TextStyle(
                          fontFamily: "DefaultFont",
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(
                  height: 10,
                ),
                Common.buildCloseOutlinedButton(context, widget.navTo,widget.eventTitle,widget.eventDesc),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
