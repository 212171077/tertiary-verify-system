import 'package:flutter/material.dart';

class AutocompletePage extends StatefulWidget {
  const AutocompletePage({super.key});

  @override
  _AutocompletePageState createState() => _AutocompletePageState();
}

class _AutocompletePageState extends State<AutocompletePage> {
  final List<String> _options = ['Apple', 'Banana', 'Cherry', 'Durian', 'Eggfruit'];
  String _selectedOption = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Autocomplete Input'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Autocomplete<String>(
              optionsBuilder: (textEditingValue) {
                return _options.where((option) =>
                    option.toLowerCase().contains(textEditingValue.text.toLowerCase()));
              },
              onSelected: (String selection) {
                setState(() {
                  _selectedOption = selection;
                });
              },
              fieldViewBuilder: (BuildContext context, TextEditingController textEditingController, FocusNode focusNode, VoidCallback onFieldSubmitted) {
                return TextFormField(
                  controller: textEditingController,
                  focusNode: focusNode,
                  onFieldSubmitted: (_) => onFieldSubmitted(),
                  decoration: const InputDecoration(
                    hintText: 'Search for an option',
                    border: OutlineInputBorder(),
                  ),
                );
              },
              displayStringForOption: (option) => option,
            ),



            const SizedBox(height: 16.0),
            Text(
              'Selected option: $_selectedOption',
              style: const TextStyle(fontSize: 18.0),
            ),
          ],
        ),
      ),
    );
  }
}
