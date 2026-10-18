import 'package:flutter/material.dart';

import 'common.dart';

class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  _SearchPageState createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  final _formKey = GlobalKey<FormState>();
  String _institutionName = "";
  bool _isLoading = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: Common.buildAppBar(context),
      body: Column(
        children: [
          Container(
            height: 200,
            width: double.infinity,
            decoration: const BoxDecoration(
              image: DecorationImage(
                image: NetworkImage('https://picsum.photos/id/1005/200/200'),
                fit: BoxFit.cover,
              ),
            ),
          ),
          Container(
            width: double.infinity,
            color: Common.primaryColor,
            child: const Center(
              child: Column(
                children: [
                  Padding(
                    padding: EdgeInsets.fromLTRB(0, 5, 0, 0),
                    child: Text(
                      'Search by institution name',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 25,
                        fontFamily: "DefaultFont",
                      ),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.fromLTRB(0, 1, 0, 5),
                    child: Text(
                      'Enter an institution name or a "known as name" ',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 16,
                        fontFamily: "DefaultFont",
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          Container(
            padding: const EdgeInsets.all(20.0),
            child: Form(
              key: _formKey,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const SizedBox(
                    height: 20.0,
                  ),
                  TextFormField(
                    decoration: InputDecoration(
                      labelText: "Institution Name",
                      labelStyle: const TextStyle(
                        color: Common.primaryColor,
                        fontWeight: FontWeight.bold,
                      ),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(10.0),
                      ),
                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(10.0),
                        borderSide: const BorderSide(
                          color: Common.primaryColor,
                          width: 2.0,
                        ),
                      ),
                      focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(10.0),
                        borderSide: const BorderSide(
                          color: Common.primaryColor,
                          width: 2.0,
                        ),
                      ),
                    ),
                    validator: (value) {
                      if (value != null && value.isEmpty) {
                        return "Please enter a valid institution name";
                      }
                      return null;
                    },
                    onSaved: (value) {
                      _institutionName = value!;
                    },
                  ),
                  const SizedBox(
                    height: 20.0,
                  ),
                  ElevatedButton(
                    child: const Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          Icons.search,
                          color: Colors.white,
                        ),
                        SizedBox(width: 10.0),
                        Text(
                          "Search",
                          style: TextStyle(
                            color: Colors.white,
                          ),
                        ),
                      ],
                    ),
                    onPressed: () {
                      if (_formKey.currentState!.validate()) {
                        _formKey.currentState!.save();
                        setState(() {
                          _isLoading = true;
                        });
                        print("Institution name: $_institutionName");
                      }
                    },
                  ),
                  const SizedBox(
                    height: 20.0,
                  ),
                  _isLoading
                      ? const Center(
                          child: CircularProgressIndicator(),
                        )
                      : Container(),
                ],
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: Common.buildFooter,
    );
  }
}
