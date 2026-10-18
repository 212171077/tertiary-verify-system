import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: StatusScreen(),
    );
  }
}

class StatusScreen extends StatelessWidget {
  final String status = 'approved';

  const StatusScreen({super.key}); // Change this to 'orange' to see the orange status

  String getStatusDescription() {
    if (status == 'approved') {
      return 'This status is approved.';
    } else {
      return 'This status is orange.';
    }
  }

  @override
  Widget build(BuildContext context) {
    Color statusColor = (status == 'approved') ? Colors.green : Colors.orange;
    IconData statusIcon = (status == 'approved') ? Icons.check_circle : Icons.error;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Status Example'),
      ),
      body: Center(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Chip(
              backgroundColor: statusColor,
              label: Row(
                children: [
                  Icon(
                    statusIcon,
                    color: Colors.white,
                  ),
                  const SizedBox(width: 8.0),
                  Text(
                    status,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 16.0,
                    ),
                  ),
                ],
              ),
            ),
            IconButton(
              icon: const Icon(Icons.info),
              onPressed: () {
                showDialog(
                  context: context,
                  builder: (context) {
                    return AlertDialog(
                      title: const Text('Status Description'),
                      content: Text(getStatusDescription()),
                      actions: [
                        TextButton(
                          onPressed: () {
                            Navigator.of(context).pop();
                          },
                          child: const Text('Close'),
                        ),
                      ],
                    );
                  },
                );
              },
              tooltip: 'Status Info',
            ),
          ],
        ),
      ),
    );
  }
}
