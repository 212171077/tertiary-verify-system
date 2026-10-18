import 'package:validators/validators.dart' as validator;

class CommonValidator {
  static String? validateFullName(String? value) {
    if (value == null || value.isEmpty) {
      return 'Please enter your full name';
    }
    final fullNameRegex = RegExp(r'^(?=.*[a-zA-Z])(?!\d+$).{2,}$');
    if (!fullNameRegex.hasMatch(value)) {
      return 'Please enter a valid full name';
    }
    if (value.length < 2) {
      return 'Full name should be at least 2 characters';
    }
    if (value.length > 100) {
      return 'Full name should not exceed 100 characters';
    }
    return null;
  }

  static String? validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return 'Please enter email address';
    }
    final emailRegex = RegExp(r'^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$');
    if (!emailRegex.hasMatch(value)) {
      return 'Please enter a valid email address';
    }
    return null;
  }

  static String? validateCellNumber(String? value) {
    if (value == null || value.isEmpty) {
      return 'Please enter cell number';
    }
    final cellNumberRegex = RegExp(r'^(\\+27|0)[6-8]\d{8}$');
    if (!cellNumberRegex.hasMatch(value)) {
      return 'Please enter a valid South African cell number';
    }
    return null;
  }

  static String? validateMessage(String? value) {
    if (value == null || value.isEmpty) {
      return 'Please enter a message';
    }
    if (value.length > 500) {
      return 'Message should not exceed 500 characters';
    }
    return null;
  }

  static String? validateName(String? value) {
    if (value == null) {
      return 'Please enter your name';
    }
    value = value.trim();

    if (value.isEmpty) {
      return 'Please enter your name';
    }

    if (value.length > 50) {
      return 'Name should not exceed 50 characters';
    }

    if (!validator.matches(value, r'^(?=.*[a-zA-Z])(?!\d+$).{2,}$')) {
      return 'Please enter valid name';
    }

    if (value.length < 2) {
      return 'Name should be at least 2 characters';
    }

    return null;
  }


  static String?  validateSurname(String? value) {
    if (value == null) {
      return 'Please enter your surname';
    }

    value = value.trim();

    if (value.isEmpty) {
      return 'Please enter your surname';
    }

    if (value.length > 50) {
      return 'Surname should not exceed 50 characters';
    }

    if (!RegExp(r"^(?=.*[a-zA-Z])(?!\d+$).{2,}$").hasMatch(value)) {
      return 'Please enter a valid surname';
    }

    if (value.length < 2) {
      return 'Surname should be at least 2 characters';
    }

    return null;
  }

  static String? validatePostalCode(String? instPostalCode) {
    // Check if postal code is null, empty, or blank
    if (instPostalCode == null || instPostalCode.trim().isEmpty) {
      return 'Please enter suburb';
    }
    // Check if postal code matches the pattern
    final RegExp pattern = RegExp(r'\d{4}');
    if (!pattern.hasMatch(instPostalCode)) {
      return 'Invalid suburb code. Please enter a valid South African suburb code.';
    }

    return null;
  }



}
