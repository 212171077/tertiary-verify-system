import 'package:flutter/material.dart';

class HomeIcon extends StatelessWidget {
  final double size;
  final Color color;
  final IconData icon;
  final String label;

  const HomeIcon({
    super.key,
    this.size = 24.0,
    this.color = Colors.black,
    this.icon = Icons.message,
    this.label = 'Search',
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Icon(
          icon,
          size: size,
          color: color,
        ),
        const SizedBox(height: 4.0),
        Text(
          label,
          style: TextStyle(fontSize: 9.0, color: color),
        ),
      ],
    );
  }
}
