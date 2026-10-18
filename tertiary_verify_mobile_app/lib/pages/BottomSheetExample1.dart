import 'package:flutter/material.dart';
import 'package:tertiary_verify/pages/common.dart';

class BottomSheetExample extends StatefulWidget {
  const BottomSheetExample({super.key});

  @override
  _BottomSheetExampleState createState() => _BottomSheetExampleState();
}

class _BottomSheetExampleState extends State<BottomSheetExample>
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
        margin: const EdgeInsets.fromLTRB(2, 2, 2, 2),
        height: 250,
        decoration: BoxDecoration(
          color: Common.primaryColor.withOpacity(0.2),
          borderRadius: const BorderRadius.only(
            topLeft: Radius.circular(25),
            topRight: Radius.circular(25),
          ),
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
                    style: TextStyle(fontSize: 20),
                  ),
                ),
                ListView(
                  shrinkWrap: true,
                  children: const [
                    ListTile(
                      leading: Icon(Icons.info_outline),
                      title: Text('Option 1'),
                    ),
                    ListTile(
                      leading: Icon(Icons.settings),
                      title: Text('Option 2'),
                    ),
                    ListTile(
                      leading: Icon(Icons.help_outline),
                      title: Text('Option 3'),
                    ),
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
