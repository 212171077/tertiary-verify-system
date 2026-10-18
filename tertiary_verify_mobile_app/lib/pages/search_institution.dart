import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tertiary_verify/pages/common.dart';
import 'package:tertiary_verify/service/session_provider.dart';
import 'package:url_launcher/url_launcher_string.dart';

import '../model/institution.dart';
import '../service/tertiary_verify_service.dart';

class SearchInstitution extends StatefulWidget {
  const SearchInstitution({super.key});

  @override
  _SearchInstitutionState createState() => _SearchInstitutionState();
}

class _SearchInstitutionState extends State<SearchInstitution> {
  // create a list to store the items
  late List<Institution> items;
  bool _isLoading = true;

  // create a text controller for the search input field
  final TextEditingController _searchController = TextEditingController();

  // create a filtered list that will change based on the search input
  List<Institution> _filteredList = [];

  void _filterList(String value) {
    setState(() {
      _filteredList = items.where((item) {
        String lowerCaseValue = value.toLowerCase();
        return item.name.toLowerCase().contains(lowerCaseValue) ||
            item.knownAs.toLowerCase().contains(lowerCaseValue) ||
            item.accreditedNumber.toLowerCase().contains(lowerCaseValue) ||
            item.addressList.any(
                (address) => address.toLowerCase().contains(lowerCaseValue));
      }).toList();
    });
  }

  @override
  void initState() {
    fetchInstitutions();
    super.initState();
  }

  void fetchInstitutions() async {
    items = await TertiaryVerifyService.fetchAccreditedInstitutions();
    _filteredList = items;
    _searchController.addListener(() {
      _filterList(_searchController.text);
    });
    setState(() {
      _isLoading = false;
    });
  }

  String applyLink = '';

  _launchApplyLink() async {
    if (await canLaunchUrlString(applyLink)) {
      await launchUrlString(applyLink);
    } else {
      throw 'Could not launch $applyLink';
    }
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
              "Accredited Institutions",
              "The institutions below are registered with the "
                  "Department of Higher Education and Training",
              "assets/images/search_inst.png"),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: !_isLoading
                ? Column(
                    children: [
                      Padding(
                        padding: const EdgeInsets.only(top: 8),
                        child: TextField(
                          controller: _searchController,
                          decoration: InputDecoration(
                            hintText: "Enter keyword...",
                            filled: true,
                            fillColor: Colors.grey[50],
                            labelStyle: const TextStyle(
                              color: Common.primaryColor,
                              fontFamily: "DefaultFont",
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(10),
                              borderSide:
                                  const BorderSide(color: Colors.orange),
                            ),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(10),
                              borderSide: const BorderSide(color: Colors.black),
                            ),
                            border: const OutlineInputBorder(
                              borderRadius:
                                  BorderRadius.all(Radius.circular(10.0)),
                            ),
                            prefixIcon: const Icon(Icons.search),
                            suffixIcon: IconButton(
                              icon: const Icon(Icons.info),
                              onPressed: () {
                                TertiaryVerifyService.saveStatistic(
                                    title: 'Search Institution',
                                    description:
                                        'Search Institution Info Icon Clicked on Accredited Institutions',
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
                                            "Search by institution name, institution known as, accredited number, "
                                            "province, or address. Simply enter your search query in the "
                                            "search bar to filter the results accordingly. Happy searching!",
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
                                              TertiaryVerifyService.saveStatistic(
                                                  title: 'Search Institution',
                                                  description:
                                                      'Search Institution Info Dialog, OK button Clicked',
                                                  sessionId: sessionProvider
                                                      .sessionId);
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
                        ),
                      ),
                    ],
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
                        "Institution not found",
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
                        child: Container(
                          margin: const EdgeInsets.all(10),
                          child: GestureDetector(
                            onTap: () {
                              TertiaryVerifyService.saveStatistic(
                                  title: 'Search Institution',
                                  description:
                                      'Expansion Tile Clicked, Viewing Institution: ${_filteredList[index].name}',
                                  sessionId: sessionProvider.sessionId);
                            },
                            child: ExpansionTile(
                              leading: _filteredList[index]
                                      .image
                                      .contains("assets/images/")
                                  ? Image.asset(
                                      _filteredList[index].image,
                                      width: 50,
                                      height: 50,
                                    )
                                  : Image.network(
                                      _filteredList[index].image,
                                      width: 50,
                                      height: 50,
                                    ),
                              title: Text(_filteredList[index].name.trim(),
                                  style: const TextStyle(
                                      fontWeight: FontWeight.bold)),
                              subtitle: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Row(
                                    children: [
                                      const Text(
                                        "Accredited start date :",
                                        style: TextStyle(
                                          fontWeight: FontWeight.bold,
                                          fontFamily: "DefaultFont",
                                          fontSize: 12,
                                          color: Colors.black,
                                        ),
                                      ),
                                      const SizedBox(width: 4),
                                      Text(
                                        _filteredList[index]
                                            .accreditedStartDate.trim(),
                                        style: TextStyle(
                                          fontSize: 12,
                                          fontWeight: FontWeight.normal,
                                          color: Colors.grey[850],
                                        ),
                                      ),
                                    ],
                                  ),
                                  Row(
                                    children: [
                                      const Text(
                                        "Accredited end date   :",
                                        style: TextStyle(
                                          fontWeight: FontWeight.bold,
                                          fontFamily: "DefaultFont",
                                          fontSize: 12,
                                          color: Colors.black,
                                        ),
                                      ),
                                      const SizedBox(width: 4),
                                      Text(
                                        _filteredList[index].accreditedEndDate.trim(),
                                        style: TextStyle(
                                          fontSize: 12,
                                          fontWeight: FontWeight.normal,
                                          color: Colors.grey[850],
                                        ),
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                              children: <Widget>[
                                Padding(
                                  padding: const EdgeInsets.all(8),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: <Widget>[
                                      const Divider(
                                        color: Colors.white60,
                                        thickness: 1,
                                        indent: 0,
                                        endIndent: 0,
                                      ),
                                      ListView(
                                        shrinkWrap: true,
                                        children: [
                                          Row(
                                            children: [
                                              Expanded(
                                                child: ListTile(
                                                  title: const Text(
                                                    'Contact Number(s)',
                                                    style: TextStyle(
                                                        fontFamily:
                                                            "DefaultFont",
                                                        fontSize: 12,
                                                        fontWeight:
                                                            FontWeight.bold),
                                                  ),
                                                  subtitle: Text(
                                                    _filteredList[index]
                                                        .cellNumber.trim(),
                                                    style: TextStyle(
                                                        fontWeight:
                                                            FontWeight.normal,
                                                        fontFamily:
                                                            "DefaultFont",
                                                        fontSize: 12,
                                                        color:
                                                            Colors.grey[850]),
                                                  ),
                                                ),
                                              ),
                                              Expanded(
                                                child: ListTile(
                                                  title: const Text(
                                                    'Accredited Number',
                                                    style: TextStyle(
                                                        fontFamily:
                                                            "DefaultFont",
                                                        fontSize: 12,
                                                        fontWeight:
                                                            FontWeight.bold),
                                                  ),
                                                  subtitle: Text(
                                                    _filteredList[index]
                                                        .accreditedNumber.trim(),
                                                    style: TextStyle(
                                                        fontWeight:
                                                            FontWeight.normal,
                                                        fontFamily:
                                                            "DefaultFont",
                                                        fontSize: 12,
                                                        color:
                                                            Colors.grey[850]),
                                                  ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ],
                                      ),
                                      const SizedBox(height: 8),
                                      Padding(
                                        padding: const EdgeInsets.fromLTRB(
                                            13, 0, 13, 0),
                                        child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: <Widget>[
                                            const Text(
                                              "Address(s):",
                                              style: TextStyle(
                                                fontSize: 14,
                                                fontFamily: "DefaultFont",
                                                fontWeight: FontWeight.bold,
                                                color: Colors.black,
                                              ),
                                            ),
                                            const SizedBox(height: 12),
                                            Column(
                                              crossAxisAlignment:
                                                  CrossAxisAlignment.start,
                                              children: _filteredList[index]
                                                  .addressList
                                                  .map((address) {
                                                return Padding(
                                                  padding:
                                                      const EdgeInsets.only(
                                                          bottom: 12),
                                                  child: Row(
                                                    crossAxisAlignment:
                                                        CrossAxisAlignment
                                                            .start,
                                                    children: [
                                                      Icon(
                                                        Icons.pin_drop_outlined,
                                                        size: 16,
                                                        color: Colors.grey[850],
                                                      ),
                                                      const SizedBox(width: 4),
                                                      Expanded(
                                                        child: Text(
                                                          address.trim(),
                                                          style: TextStyle(
                                                            fontWeight:
                                                                FontWeight
                                                                    .normal,
                                                            fontSize: 12,
                                                            color: Colors
                                                                .grey[850],
                                                          ),
                                                        ),
                                                      ),
                                                    ],
                                                  ),
                                                );
                                              }).toList(),
                                            ),
                                          ],
                                        ),
                                      ),
                                      Padding(
                                        padding: const EdgeInsets.fromLTRB(
                                            13, 0, 13, 0),
                                        child: Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            TextButton.icon(
                                              style: TextButton.styleFrom(
                                                padding:
                                                    const EdgeInsets.symmetric(
                                                        vertical: 6,
                                                        horizontal: 10),
                                                shape: RoundedRectangleBorder(
                                                  borderRadius:
                                                      BorderRadius.circular(20),
                                                  side: const BorderSide(
                                                      color: Colors.orange),
                                                ),
                                              ),
                                              icon: const Icon(
                                                Icons.info,
                                                color: Colors.orange,
                                              ),
                                              label: const Text(
                                                "MORE INFO",
                                                style: TextStyle(
                                                  fontFamily: "DefaultFont",
                                                  color: Colors.orange,
                                                  fontSize: 10,
                                                ),
                                              ),
                                              onPressed: () {
                                                TertiaryVerifyService.saveStatistic(
                                                    title: 'Search Institution',
                                                    description:
                                                        'More Info button Clicked, Viewing ${_filteredList[index].name}',
                                                    sessionId: sessionProvider
                                                        .sessionId);
                                                Navigator.pushNamed(
                                                  context,
                                                  'institution_details',
                                                  arguments:
                                                      _filteredList[index],
                                                );
                                              },
                                            ),
                                            Visibility(
                                              visible: _filteredList[index]
                                                  .showApplyLink,
                                              child: TextButton.icon(
                                                style: TextButton.styleFrom(
                                                  padding: const EdgeInsets
                                                      .symmetric(
                                                      vertical: 6,
                                                      horizontal: 10),
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            20),
                                                    side: const BorderSide(
                                                        color: Colors
                                                            .green), // Adjust button style
                                                  ),
                                                ),
                                                icon: const Icon(
                                                  Icons.link,
                                                  color: Colors
                                                      .green, // Set the icon color
                                                ),
                                                label: const Text(
                                                  "APPLY HERE",
                                                  style: TextStyle(
                                                    fontFamily: "DefaultFont",
                                                    color: Colors.green,
                                                    // Set the text color
                                                    fontSize: 10,
                                                  ),
                                                ),
                                                onPressed: () {
                                                  TertiaryVerifyService.saveStatistic(
                                                      title:
                                                          'Search Institution',
                                                      description:
                                                          'Apply here button Clicked, Institution name: ${_filteredList[index].name}',
                                                      sessionId: sessionProvider
                                                          .sessionId);

                                                  applyLink =
                                                      _filteredList[index]
                                                          .applyLink.trim();
                                                  _launchApplyLink();
                                                }, // Call the URL launcher
                                              ),
                                            ),
                                          ],
                                        ),
                                      ),
                                    ],
                                  ),
                                )
                              ],
                            ),
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
