import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/pages/common.dart';
import 'package:tertiary_verify/service/session_provider.dart';

import '../model/course_detail.dart';
import '../model/institution.dart';
import '../service/tertiary_verify_service.dart';

class SearchCourse extends StatefulWidget {
  const SearchCourse({super.key});

  @override
  _SearchCourseState createState() => _SearchCourseState();
}

class _SearchCourseState extends State<SearchCourse> {
  // create a list to store the items
  late List<CourseDetail> items;
  bool _isLoading = true;

  // create a text controller for the search input field
  final TextEditingController _searchController = TextEditingController();

  // create a filtered list that will change based on the search input
  List<CourseDetail> _filteredList = [];

  // function to filter the list based on the search input
  void _filterList(String value) {
    setState(() {
      _filteredList = items.where((item) {
        String lowerCaseValue = value.toLowerCase();
        return item.name.toLowerCase().contains(lowerCaseValue) ||
            item.type.toLowerCase().contains(lowerCaseValue) ||
            item.level.toLowerCase().contains(lowerCaseValue);
      }).toList();
    });
  }

  @override
  void initState() {
    fetchCourseDetails();
    super.initState();
  }

  void fetchCourseDetails() async {
    items = await TertiaryVerifyService.fetchAccreditedCourses();
    _filteredList = items;
    _searchController.addListener(() {
      _filterList(_searchController.text);
    });
    setState(() {
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final sessionProvider = Provider.of<SessionProvider>(context);
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: Common.buildAppBar(context),
      body: Column(
        children: <Widget>[
          Common.buildHomeCard(
              "Accredited Courses",
              "Below are registered institutions offering accredited courses",
              "assets/images/search_course.png"),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: !_isLoading
                ? TextField(
                    controller: _searchController,
                    decoration: InputDecoration(
                      hintText: "Enter course name...",
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
                      border: const OutlineInputBorder(
                        borderRadius: BorderRadius.all(Radius.circular(10.0)),
                      ),
                      prefixIcon: const Icon(Icons.search),
                      suffixIcon: IconButton(
                        icon: const Icon(Icons.info),
                        onPressed: () {
                          TertiaryVerifyService.saveStatistic(
                              title: 'Search Courses',
                              description:
                                  'Search Course Info Icon Clicked on Accredited Courses',
                              sessionId: sessionProvider.sessionId);
                          showDialog(
                            context: context,
                            builder: (context) => Dialog(
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(12.0),
                              ),
                              child: Container(
                                padding: const EdgeInsets.all(16.0),
                                child: Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    const Text(
                                      "Search Information",
                                      style: TextStyle(
                                        fontSize: 20.0,
                                        fontWeight: FontWeight.bold,
                                        fontFamily: "DefaultFont",
                                        color: Common.primaryColor,
                                      ),
                                    ),
                                    const SizedBox(height: 12.0),
                                    const Text(
                                      "Search by course name, course type, or course level to find the desired information. "
                                      "Simply enter your search query in the search bar to filter "
                                      "the results accordingly. Happy searching!",
                                      style: TextStyle(
                                          fontSize: 16.0,
                                          fontFamily: "DefaultFont",
                                          color: Colors.black),
                                      textAlign: TextAlign.center,
                                    ),
                                    const SizedBox(height: 12.0),
                                    OutlinedButton(
                                      onPressed: () {
                                        TertiaryVerifyService.saveStatistic(
                                            title: 'Search Courses',
                                            description:
                                                'Search Course Info Dialog, OK button Clicked',
                                            sessionId:
                                                sessionProvider.sessionId);
                                        Navigator.pop(context);
                                      },
                                      style: OutlinedButton.styleFrom(
                                        side: const BorderSide(
                                            color: Common.primaryColor),
                                        shape: RoundedRectangleBorder(
                                          borderRadius:
                                              BorderRadius.circular(20.0),
                                        ),
                                      ),
                                      child: const Text(
                                        'OK',
                                        style: TextStyle(
                                          color: Common.primaryColor,
                                          fontFamily: "DefaultFont",
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          );
                        },
                      ),
                    ),
                    style: const TextStyle(
                      fontSize: 18,
                      color: Colors.black,
                    ),
                  )
                : null,
          ),
          Center(
              child: _isLoading
                  ? Padding(
                      padding: const EdgeInsets.fromLTRB(0, 30, 0, 0),
                      child: Common.buildProgressBar())
                  : null),
          Expanded(
            child: _filteredList.isEmpty && !_isLoading
                ? Center(
                    child: Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(8),
                        color: Colors.grey[300],
                      ),
                      child: Text(
                        "Course not found",
                        style: TextStyle(
                          fontSize: 18,
                          fontFamily: "DefaultFont",
                          fontWeight: FontWeight.bold,
                          color: Colors.grey[850],
                        ),
                      ),
                    ),
                  )
                : ListView.builder(
                    itemCount: _filteredList.length,
                    itemBuilder: (context, index) {
                      return Card(
                        elevation: 2,
                        margin: const EdgeInsets.all(8),
                        child: GestureDetector(
                          onTap: () {
                            TertiaryVerifyService.saveStatistic(
                                title: 'Search Courses',
                                description:
                                    'Expansion Tile Clicked on Accredited Courses, Viewing Course: ${_filteredList[index].name}',
                                sessionId: sessionProvider.sessionId);
                          },
                          child: ExpansionTile(
                            leading: Image.asset(
                              _filteredList[index].image,
                              width: 50,
                              height: 50,
                            ),
                            title: Text(_filteredList[index].name.trim(),
                                style: const TextStyle(
                                    fontWeight: FontWeight.bold)),
                            subtitle: Text(_filteredList[index].type.trim(),
                                style: TextStyle(
                                  fontWeight: FontWeight.normal,
                                  color: Colors.grey[850],
                                )),
                            children: <Widget>[
                              Padding(
                                padding: const EdgeInsets.all(8),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: <Widget>[
                                    const SizedBox(height: 8),
                                    const Divider(
                                      color: Colors.grey,
                                      thickness: 1,
                                      indent: 0,
                                      endIndent: 0,
                                    ),
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.start,
                                      children: <Widget>[
                                        const Text("Course name : ",
                                            style: TextStyle(
                                                fontSize: 14,
                                                fontWeight: FontWeight.bold)),
                                        Text(_filteredList[index].name.trim(),
                                            style: TextStyle(
                                              fontSize: 14,
                                              color: Colors.grey[850],
                                            )),
                                      ],
                                    ),
                                    const SizedBox(height: 8),
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.start,
                                      children: <Widget>[
                                        const Text("Course type   : ",
                                            style: TextStyle(
                                                fontSize: 14,
                                                fontWeight: FontWeight.bold)),
                                        Text(_filteredList[index].type.trim(),
                                            style: TextStyle(
                                              fontSize: 14,
                                              color: Colors.grey[850],
                                            )),
                                      ],
                                    ),
                                    const SizedBox(height: 8),
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.start,
                                      children: <Widget>[
                                        const Text("Course Level : ",
                                            style: TextStyle(
                                                fontSize: 14,
                                                fontWeight: FontWeight.bold)),
                                        Text(_filteredList[index].level.trim(),
                                            style: TextStyle(
                                              fontSize: 14,
                                              color: Colors.grey[850],
                                            )),
                                      ],
                                    ),
                                    const SizedBox(height: 15),
                                     Text(
                                      "The Institution(s) below are accredited to offer this course: ",
                                      style: TextStyle(
                                        color: Colors.grey[850],
                                        fontFamily: "DefaultFont",
                                        fontSize: 12,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                    const SizedBox(height: 10),
                                    HorizontalInstCard(
                                      institutions:
                                          _filteredList[index].institutions,
                                      onClick: (inst) {
                                        TertiaryVerifyService.saveStatistic(
                                            title: 'Search Courses',
                                            description:
                                                'Institution Card Clicked, Course: ${_filteredList[index].name}, Institution: ${inst.name}',
                                            sessionId:
                                                sessionProvider.sessionId);
                                        Navigator.pushNamed(
                                          context,
                                          'institution_details',
                                          arguments: inst,
                                        );
                                      },
                                    ),
                                    const SizedBox(height: 8),
                                  ],
                                ),
                              )
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }
}

class HorizontalInstCard extends StatelessWidget {
  final List<Institution> institutions;
  final Function(Institution) onClick;

  const HorizontalInstCard(
      {super.key, required this.institutions, required this.onClick});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 100,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: institutions.length,
        itemBuilder: (context, subIndex) {
          return InkWell(
            onTap: () => onClick(institutions[subIndex]),
            child: Container(
              margin: const EdgeInsets.only(right: 0),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                border: Border.all(
                  color: Colors.white, // Adjust border color as needed
                  width: 1, // Adjust border width as needed
                ),
              ),
              child: Card(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(5),
                ),
                child: Container(
                  width: 100,
                  padding:  const EdgeInsets.all(0),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(5),
                    border: Border.all(
                      color: const Color.fromARGB(255, 48, 48, 48),
                      width: 1,
                    ),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                      Container(
                        height: 65,
                        width: 65,
                        decoration: BoxDecoration(
                          shape: BoxShape.rectangle,
                          image: DecorationImage(
                            fit: BoxFit.cover,
                            image: institutions[subIndex]
                                    .image
                                    .contains("assets/images/")
                                ? AssetImage(institutions[subIndex].image)
                                : NetworkImage(institutions[subIndex].image)
                                    as ImageProvider<Object>,
                          ),
                        ),
                      ),
                      const SizedBox(height: 3),
                      Flexible(
                        child: Text(
                          institutions[subIndex].name,
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 8,
                          ),
                        ),
                      ),
                      const SizedBox(height: 3),
                    ],
                  ),
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}
