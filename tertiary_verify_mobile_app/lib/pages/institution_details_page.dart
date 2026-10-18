import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:url_launcher/url_launcher_string.dart';

import '../model/course.dart';
import '../model/institution.dart';
import '../service/tertiary_verify_service.dart';
import 'common.dart';

class InstitutionDetailsPage extends StatefulWidget {
  const InstitutionDetailsPage({super.key});

  @override
  State<InstitutionDetailsPage> createState() => _InstitutionDetailsPageState();
}

class _InstitutionDetailsPageState extends State<InstitutionDetailsPage> {
  // create a list to store the items
  List<Course> items = [];
  bool _isLoading = true;
  Institution? institution;

  // create a text controller for the search input field
  final TextEditingController _searchController = TextEditingController();

  // create a filtered list that will change based on the search input
  List<Course> _filteredList = [];

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
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (institution != null) {
        fetchInstitutionCourses(institution!.id);
      }
    });
  }

  void fetchInstitutionCourses(int instID) async {
    items = await TertiaryVerifyService.fetchInstitutionCourses(instID);
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
    institution = ModalRoute.of(context)!.settings.arguments as Institution;
    String status = institution!.status;
    Color statusColor =
        (status.toLowerCase() == 'approved') ? Colors.green : Colors.amber;

    String statusDescription = institution!.statusDescription;

    return Scaffold(
      backgroundColor: Colors.white,
      appBar: Common.buildAppBar(context),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Stack(
              children: [
                Card(
                  margin: const EdgeInsets.fromLTRB(2, 0, 2, 8),
                  elevation: 4,
                  shadowColor: Colors.black,
                  shape: const RoundedRectangleBorder(
                    borderRadius: BorderRadius.only(
                      bottomLeft: Radius.circular(25),
                      bottomRight: Radius.circular(25),
                    ),
                    side: BorderSide(
                      color: Common.primaryColor,
                      width: 1.0,
                    ),
                  ),
                  child: Hero(
                    tag: institution!.accreditedNumber,
                    child: ClipRRect(
                      borderRadius: const BorderRadius.only(
                        bottomLeft: Radius.circular(25.0),
                        bottomRight: Radius.circular(25.0),
                      ),
                      child: Stack(
                        children: [
                          SizedBox(
                            height: 140,
                            width: double.infinity,
                            child: Stack(
                              children: [
                                institution!.image.contains("assets/images/")
                                    ? Image.asset(
                                        institution!.image,
                                        width: double.infinity,
                                        height: 140,
                                        fit: BoxFit.contain,
                                      )
                                    : Image.network(
                                        institution!.image,
                                        width: double.infinity,
                                        height: 140,
                                        fit: BoxFit.contain,
                                      ),
                                Container(
                                  width: double.infinity,
                                  height: 140,
                                  color: Common.primaryColor.withOpacity(
                                      0.5), // Adjust the opacity as needed
                                ),
                                BackdropFilter(
                                  filter:
                                      ImageFilter.blur(sigmaX: 2, sigmaY: 2),
                                  // Adjust the blur intensity
                                  child: Container(
                                    color: Colors
                                        .transparent, // Make sure this is transparent
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Positioned(
                            bottom: 0,
                            left: 0,
                            right: 0,
                            child: Container(
                              padding: const EdgeInsets.all(16.0),
                              decoration: BoxDecoration(
                                gradient: LinearGradient(
                                  begin: Alignment.topCenter,
                                  end: Alignment.bottomCenter,
                                  colors: [
                                    Common.primaryColor.withOpacity(0.0),
                                    // Transparent at the top
                                    Common.primaryColor.withOpacity(1.0),
                                    // Your color with some opacity
                                  ],
                                ),
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                children: [
                                  Text(
                                    institution!.name,
                                    style: const TextStyle(
                                      color: Colors.white,
                                      fontSize: 24.0,
                                      fontWeight: FontWeight.bold,
                                      fontFamily: 'DefaultFont',
                                    ),
                                    textAlign: TextAlign.center,
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8.0),
            SizedBox(
              height: 1000,
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 5),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Card(
                          elevation: 2,
                          child: ListTile(
                            title: const Text('Institution Details'),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                ListTile(
                                  title: const Text(
                                    'Status',
                                    style: TextStyle(
                                        fontSize: 14.0), // Adjust the font size
                                  ),
                                  subtitle: Row(
                                    children: [
                                      Center(
                                        child: Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.center,
                                          children: <Widget>[
                                            Chip(
                                              backgroundColor: statusColor,
                                              label: Row(
                                                children: [
                                                  Text(
                                                    status,
                                                    style: const TextStyle(
                                                      color: Colors.white,
                                                      fontSize:
                                                          8.0, // Adjust the font size
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              padding: const EdgeInsets
                                                  .symmetric(
                                                  horizontal: 2.0,
                                                  vertical:
                                                      1.0), // Adjust the padding
                                            ),
                                            const SizedBox(
                                              width: 25,
                                            ),
                                            IconButton(
                                              icon: const Icon(Icons.info),
                                              onPressed: () {
                                                TertiaryVerifyService.saveStatistic(
                                                    title:
                                                        'Institution Details',
                                                    description:
                                                        'Status Description Dialog, Info Button Clicked',
                                                    sessionId: sessionProvider
                                                        .sessionId);
                                                showDialog(
                                                  context: context,
                                                  builder: (context) => Dialog(
                                                    shape:
                                                        RoundedRectangleBorder(
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              12.0),
                                                    ),
                                                    child: Container(
                                                      padding:
                                                          const EdgeInsets.all(
                                                              16.0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.min,
                                                        children: [
                                                          const Text(
                                                            "Status description",
                                                            style: TextStyle(
                                                              fontSize: 20.0,
                                                              fontWeight:
                                                                  FontWeight
                                                                      .bold,
                                                              fontFamily:
                                                                  "DefaultFont",
                                                              color: Common
                                                                  .primaryColor,
                                                            ),
                                                          ),
                                                          const SizedBox(
                                                              height: 12.0),
                                                          Text(
                                                            statusDescription,
                                                            style:
                                                                const TextStyle(
                                                              fontSize: 16.0,
                                                              fontFamily:
                                                                  "DefaultFont",
                                                              color:
                                                                  Colors.black,
                                                            ),
                                                            textAlign: TextAlign
                                                                .center,
                                                          ),
                                                          const SizedBox(
                                                              height: 12.0),
                                                          OutlinedButton(
                                                            onPressed: () {
                                                              TertiaryVerifyService.saveStatistic(
                                                                  title:
                                                                      'Institution Details',
                                                                  description:
                                                                      'Status Description Dialog, Ok Button Clicked',
                                                                  sessionId:
                                                                      sessionProvider
                                                                          .sessionId);
                                                              Navigator.pop(
                                                                  context);
                                                            },
                                                            style:
                                                                OutlinedButton
                                                                    .styleFrom(
                                                              side: const BorderSide(
                                                                  color: Common
                                                                      .primaryColor),
                                                              shape:
                                                                  RoundedRectangleBorder(
                                                                borderRadius:
                                                                    BorderRadius
                                                                        .circular(
                                                                            20.0),
                                                              ),
                                                            ),
                                                            child: const Text(
                                                              'OK',
                                                              style: TextStyle(
                                                                color: Common
                                                                    .primaryColor,
                                                                fontFamily:
                                                                    "DefaultFont",
                                                              ),
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                );
                                              },
                                              tooltip: 'Status Info',
                                            ),
                                          ],
                                        ),
                                      ),
                                    ],
                                  ),
                                  leading: const Icon(Icons.beenhere_outlined),
                                ),
                                const Divider(
                                    height: 1, color: Common.dividerGreyColor),
                                ListTile(
                                  title: const Text(
                                    'Accreditation number',
                                    style: TextStyle(
                                        fontSize: 14.0), // Adjust the font size
                                  ),
                                  subtitle: Text(
                                    institution!.accreditedNumber,
                                    style:  TextStyle(
                                      fontWeight: FontWeight.normal,
                                      color: Colors.grey[850],
                                    ),
                                  ),
                                  leading:
                                      const Icon(Icons.fact_check_outlined),
                                ),
                                const Divider(
                                    height: 1, color: Common.dividerGreyColor),
                                ListTile(
                                  title: const Text(
                                    'Accreditation dates',
                                    style: TextStyle(
                                        fontSize: 14.0), // Adjust the font size
                                  ),
                                  subtitle: Text(
                                    "Start Date : ${institution!.accreditedStartDate}\nEnd Date   : ${institution!.accreditedEndDate}",
                                    style:  TextStyle(
                                      fontWeight: FontWeight.normal,
                                      color: Colors.grey[850],
                                    ),
                                  ),
                                  leading: const Icon(Icons.calendar_today),
                                ),
                                const Divider(
                                    height: 1, color: Common.dividerGreyColor),
                                ListTile(
                                  title: const Text(
                                    'Contact details',
                                    style: TextStyle(
                                        fontSize: 14.0), // Adjust the font size
                                  ),
                                  subtitle: Text(
                                    "Cell # : ${institution!.cellNumber}\nEmail : ${institution!.email}",
                                    style:  TextStyle(
                                      fontWeight: FontWeight.normal,
                                      color: Colors.grey[850],
                                    ),
                                  ),
                                  leading:
                                      const Icon(Icons.contact_phone_outlined),
                                ),
                                Visibility(
                                  visible: institution!.showApplyLink,
                                  child: const Divider(
                                      height: 1,
                                      color: Common.dividerGreyColor),
                                ),
                                Visibility(
                                  visible: institution!.showApplyLink,
                                  child: GestureDetector(
                                    onTap: () async {
                                      String applyNowUrl =
                                          institution!.applyLink;
                                      if (await canLaunchUrlString(
                                          applyNowUrl)) {
                                        await launchUrlString(applyNowUrl);
                                      } else {
                                        // ignore: use_build_context_synchronously
                                        showDialog(
                                          context: context,
                                          builder: (context) => Dialog(
                                            shape: RoundedRectangleBorder(
                                              borderRadius:
                                                  BorderRadius.circular(12.0),
                                            ),
                                            child: Container(
                                              padding:
                                                  const EdgeInsets.all(16.0),
                                              child: Column(
                                                mainAxisSize: MainAxisSize.min,
                                                children: [
                                                  const Text(
                                                    "Error",
                                                    style: TextStyle(
                                                      fontSize: 20.0,
                                                      fontWeight:
                                                          FontWeight.bold,
                                                      fontFamily: "DefaultFont",
                                                      color:
                                                          Common.primaryColor,
                                                    ),
                                                  ),
                                                  const SizedBox(height: 12.0),
                                                  const Text(
                                                    "Failed to open the link.",
                                                    style: TextStyle(
                                                      fontSize: 16.0,
                                                      fontFamily: "DefaultFont",
                                                      color: Colors.black,
                                                    ),
                                                    textAlign: TextAlign.center,
                                                  ),
                                                  const SizedBox(height: 12.0),
                                                  OutlinedButton(
                                                    onPressed: () {
                                                      Navigator.pop(context);
                                                    },
                                                    style: OutlinedButton
                                                        .styleFrom(
                                                      side: const BorderSide(
                                                          color: Common
                                                              .primaryColor),
                                                      shape:
                                                          RoundedRectangleBorder(
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(20.0),
                                                      ),
                                                    ),
                                                    child: const Text(
                                                      'OK',
                                                      style: TextStyle(
                                                        color:
                                                            Common.primaryColor,
                                                        fontFamily:
                                                            "DefaultFont",
                                                      ),
                                                    ),
                                                  ),
                                                ],
                                              ),
                                            ),
                                          ),
                                        );
                                      }
                                    },
                                    child: const ListTile(
                                      title: Text(
                                        'Apply Now',
                                        style: TextStyle(
                                          fontSize: 14.0,
                                          color: Colors.blue,
                                        ),
                                      ),
                                      leading: Icon(Icons.link_outlined),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                    Card(
                      elevation: 2,
                      child: ListTile(
                        title: const Text('Address Details'),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: institution!.addressList.map((address) {
                            return ListTile(
                              contentPadding: const EdgeInsets.only(
                                  left: 14, right: 0, top: 0, bottom: 0),
                              leading: Padding(
                                padding: const EdgeInsets.only(
                                    left: 0), // Add space at the beginning
                                child: Icon(
                                  Icons.pin_drop_outlined,
                                  size: 30, // Increase the icon size
                                  color: Colors.grey[850],
                                ),
                              ),
                              title: Text(
                                address,
                                style: TextStyle(
                                  fontSize: 14,
                                  color: Colors.grey[850],
                                ),
                              ),
                            );
                          }).toList(),
                        ),
                      ),
                    ),
                    Expanded(
                      child: Card(
                        elevation: 2,
                        child: ListTile(
                          title: const Text('Accredited Courses'),
                          subtitle: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Padding(
                                padding: const EdgeInsets.fromLTRB(0, 10, 0, 4),
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
                                            borderRadius:
                                                BorderRadius.circular(10),
                                            borderSide: const BorderSide(
                                                color: Colors.orange),
                                          ),
                                          enabledBorder: OutlineInputBorder(
                                            borderRadius:
                                                BorderRadius.circular(10),
                                            borderSide: const BorderSide(
                                                color: Colors.black),
                                          ),
                                          border: const OutlineInputBorder(
                                            borderRadius: BorderRadius.all(
                                                Radius.circular(10.0)),
                                          ),
                                          prefixIcon: const Icon(Icons.search),
                                          suffixIcon: IconButton(
                                            icon: const Icon(Icons.info),
                                            onPressed: () {
                                              TertiaryVerifyService.saveStatistic(
                                                  title: 'Institution Details',
                                                  description:
                                                      'Search Course Info Dialog on Institution Details, Info Button Clicked',
                                                  sessionId: sessionProvider
                                                      .sessionId);
                                              showDialog(
                                                context: context,
                                                builder: (context) => Dialog(
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            12.0),
                                                  ),
                                                  child: Container(
                                                    padding:
                                                        const EdgeInsets.all(
                                                            16.0),
                                                    child: Column(
                                                      mainAxisSize:
                                                          MainAxisSize.min,
                                                      children: [
                                                        const Text(
                                                          "Search Information",
                                                          style: TextStyle(
                                                            fontSize: 20.0,
                                                            fontWeight:
                                                                FontWeight.bold,
                                                            fontFamily:
                                                                "DefaultFont",
                                                            color: Common
                                                                .primaryColor,
                                                          ),
                                                        ),
                                                        const SizedBox(
                                                            height: 12.0),
                                                        const Text(
                                                          "Search by course name, course type, or course level to find the desired information. "
                                                          "Simply enter your search query in the search bar to filter "
                                                          "the results accordingly. Happy searching!",
                                                          style: TextStyle(
                                                              fontSize: 16.0,
                                                              fontFamily:
                                                                  "DefaultFont",
                                                              color:
                                                                  Colors.black),
                                                          textAlign:
                                                              TextAlign.center,
                                                        ),
                                                        const SizedBox(
                                                            height: 12.0),
                                                        OutlinedButton(
                                                          onPressed: () {
                                                            TertiaryVerifyService.saveStatistic(
                                                                title:
                                                                    'Institution Details',
                                                                description:
                                                                    'Search Course Info Dialog on Institution Details, Ok Button Clicked',
                                                                sessionId:
                                                                    sessionProvider
                                                                        .sessionId);
                                                            Navigator.pop(
                                                                context);
                                                          },
                                                          style: OutlinedButton
                                                              .styleFrom(
                                                            side: const BorderSide(
                                                                color: Common
                                                                    .primaryColor),
                                                            shape:
                                                                RoundedRectangleBorder(
                                                              borderRadius:
                                                                  BorderRadius
                                                                      .circular(
                                                                          20.0),
                                                            ),
                                                          ),
                                                          child: const Text(
                                                            'OK',
                                                            style: TextStyle(
                                                              color: Common
                                                                  .primaryColor,
                                                              fontFamily:
                                                                  "DefaultFont",
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
                                          padding: const EdgeInsets.fromLTRB(
                                              0, 30, 0, 0),
                                          child: Common.buildProgressBar())
                                      : null),
                              Expanded(
                                child: ListView.builder(
                                  itemCount: _filteredList.length,
                                  itemBuilder: (context, index) {
                                    return Card(
                                      elevation: 2,
                                      margin:
                                          const EdgeInsets.fromLTRB(0, 8, 0, 8),
                                      child: GestureDetector(
                                        onTap: () {
                                          TertiaryVerifyService.saveStatistic(
                                              title: 'Institution Details',
                                              description:
                                                  'Expansion Tile Clicked on Institution Details, Viewing Course: ${_filteredList[index].name}',
                                              sessionId:
                                                  sessionProvider.sessionId);
                                        },
                                        child: ExpansionTile(
                                          leading: Image.asset(
                                            _filteredList[index].image,
                                            width: 50,
                                            height: 50,
                                          ),
                                          title: Text(_filteredList[index].name.trim(),
                                              style: const TextStyle(
                                                fontWeight: FontWeight.bold,
                                              )),
                                          subtitle: Text(
                                              _filteredList[index].type.trim(),
                                              style:  TextStyle(
                                                fontWeight: FontWeight.normal,
                                                color: Colors.grey[850],
                                              )),
                                          children: <Widget>[
                                            Padding(
                                              padding: const EdgeInsets.all(8),
                                              child: Column(
                                                crossAxisAlignment:
                                                    CrossAxisAlignment.start,
                                                children: <Widget>[
                                                  const SizedBox(height: 8),
                                                   Divider(
                                                    color: Colors.grey[850],
                                                    thickness: 1,
                                                    indent: 0,
                                                    endIndent: 0,
                                                  ),
                                                  Row(
                                                    mainAxisAlignment:
                                                        MainAxisAlignment.start,
                                                    children: <Widget>[
                                                      const Text(
                                                          "Course name : ",
                                                          style: TextStyle(
                                                              fontSize: 14,
                                                              fontWeight:
                                                                  FontWeight
                                                                      .bold)),
                                                      Flexible(child: Text(
                                                          _filteredList[index]
                                                              .name.trim(),
                                                          style: TextStyle(
                                                              color: Colors.grey[850],
                                                              fontSize: 14))),
                                                    ],
                                                  ),
                                                  const SizedBox(height: 8),
                                                  Row(
                                                    mainAxisAlignment:
                                                        MainAxisAlignment.start,
                                                    children: <Widget>[
                                                      const Text(
                                                          "Course type   : ",
                                                          style: TextStyle(
                                                              fontSize: 14,
                                                              fontWeight:
                                                                  FontWeight
                                                                      .bold)),
                                                      Flexible(child: Text(
                                                          _filteredList[index]
                                                              .type.trim(),
                                                          style: TextStyle(
                                                              color: Colors.grey[850],
                                                              fontSize: 14))),
                                                    ],
                                                  ),
                                                  const SizedBox(height: 8),
                                                  Row(
                                                    mainAxisAlignment:
                                                        MainAxisAlignment.start,
                                                    children: <Widget>[
                                                      const Text(
                                                          "Course Level : ",
                                                          style: TextStyle(
                                                              fontSize: 14,
                                                              fontWeight:
                                                                  FontWeight
                                                                      .bold)),
                                                      Flexible(child: Text(
                                                          _filteredList[index]
                                                              .level.trim(),
                                                          style: TextStyle(
                                                              color: Colors.grey[850],
                                                              fontSize: 14))),
                                                    ],
                                                  ),
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
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
