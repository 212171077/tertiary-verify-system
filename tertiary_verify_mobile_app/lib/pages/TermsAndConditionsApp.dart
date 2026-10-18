import 'package:flutter/material.dart';

void main() {
  runApp(const DataPrivacyPage());
}

class DataPrivacyPage extends StatelessWidget {
  const DataPrivacyPage({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Data Privacy'),
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: DataPrivacyContent(),
        ),
      ),
    );
  }
}

class DataPrivacyContent extends StatelessWidget {
  final List<String> informationItems = [
    'Mashita Technologies (Pty) Ltd collects your information so that the investigators may reach you for more information should the information logged not be enough.',
    'Mashita Technologies (Pty) Ltd collects your information in order to send you a unique reference number for your report.',
    'Mashita Technologies (Pty) Ltd collects your information to send you feedback on the report you previously logged.',
    'Mashita Technologies (Pty) Ltd collects your information so that when you send information on contact us, we know who is sending and if feedback needs to be sent back we are able to do so.'
  ];

  DataPrivacyContent({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Date: 24 August 2023',
          style: TextStyle(
            fontSize: 16.0,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16.0),
        const Text(
          'Introduction:',
          style: TextStyle(
            fontSize: 16.0,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 8.0),
        const Text(
          'Mashita Technologies (Pty) Ltd treats the app/website users\' confidential information with the utmost discretion. The Protection of Personal Information Act 4 of 2013 (POPIA), which operationalizes the constitutional right to privacy, further strengthens our resolve to protect our app/website users’ information. POPIA promotes the fair and transparent use of personal information and requires us to safeguard it appropriately. Personal information as defined by POPIA is any information that can be used to identify you as an individual.',
          style: TextStyle(fontSize: 16.0),
        ),
        const SizedBox(height: 16.0),
        const Text(
          'Why we collect your information?',
          style: TextStyle(
            fontSize: 16.0,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 8.0),
        ListView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: informationItems.length,
          itemBuilder: (context, index) {
            return ListTile(
              contentPadding: const EdgeInsets.all(0),
              leading: const Icon(Icons.check_circle),
              title: Text(
                informationItems[index],
                style: const TextStyle(fontSize: 16.0),
              ),
            );
          },
        ),
      ],
    );
  }
}


