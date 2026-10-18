import 'package:flutter/material.dart';
import 'package:tertiary_verify/pages/common.dart';

class RefNumInfoBottomSheet extends StatefulWidget {
  const RefNumInfoBottomSheet({super.key});

  @override
  _RefNumInfoBottomSheetState createState() => _RefNumInfoBottomSheetState();
}

class _RefNumInfoBottomSheetState extends State<RefNumInfoBottomSheet>
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
    return SlideTransition(
      position: _animation,
      child: Container(
        margin: const EdgeInsets.fromLTRB(2, 2, 0, 2),
        height: 250,
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
                    Navigator.pop(context);
                  },
                ),
              ),
            ),
            Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Text(
                    'What is a reference number?',
                    style: TextStyle(
                      fontSize: 18,
                      fontFamily: "DefaultFont",
                    ),
                  ),
                ),
                ListView(
                  shrinkWrap: true,
                  children: const [
                    ListTile(
                      title: Text(
                        'A reference number is a unique '
                        'identifier assigned to any bogus '
                        'institution or bogus course reported by individual.',
                        style: TextStyle(
                          color: Colors.blue,
                          fontFamily: "DefaultFont",
                        ),
                      ),
                    ),
                    ListTile(
                      title: Text(
                        'Reference number is provided to you '
                        'after you have completed bogus institution or bogus course '
                        'report form. This number is unique to your bogus report.',
                        style: TextStyle(
                          color: Colors.blue,
                          fontFamily: "DefaultFont",
                        ),
                      ),
                    )
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
