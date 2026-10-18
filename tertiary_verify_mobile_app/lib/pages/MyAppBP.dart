import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Modern IconButton Example'),
        ),
        body: Center(
          child: Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8), // Adjust the border radius as needed
              border: Border.all(
                color: Colors.blue, // Border color
                width: 2.0, // Border width
              ),
            ),
            child: Material(
              color: Colors.transparent, // Transparent background
              child: Ink(
                decoration: BoxDecoration(
                  color: Colors.blue, // Button background color
                  borderRadius: BorderRadius.circular(6), // Adjust the inner border radius as needed
                ),
                child: InkWell(
                  onTap: () {
                    // Add your onPressed logic here
                    // This function will be executed when the button is pressed
                    print('IconButton pressed!');
                  },
                  child: const Padding(
                    padding: EdgeInsets.all(10.0),
                    child: Icon(
                      Icons.favorite, // Specify the icon
                      color: Colors.white, // Icon color
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
