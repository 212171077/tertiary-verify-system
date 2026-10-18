import 'package:flutter/material.dart';
import 'package:uuid/uuid.dart';

var uuid = const Uuid();
class SessionProvider extends ChangeNotifier {
  String _sessionId = '';

  String get sessionId => _sessionId;

  SessionProvider() {
    _generateSessionID();
  }

  void _generateSessionID() {
    const uuid = Uuid();
    _sessionId = uuid.v4();
  }

  void regenerateSessionID() {
    _generateSessionID();
    notifyListeners();
  }
}
