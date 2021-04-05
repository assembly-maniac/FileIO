package FileIO;
// -----------------------------------------------------
// Part: (include Part Number)
// Written by: (include your name(s) and student ID(s))
// -----------------------------------------------------
class InvalidException extends java.lang.Exception {
  InvalidException() {
    System.out.println("Error: Input row cannot be parsed due to missing information");
  }

  InvalidException(String msg) {
    System.out.println(msg);
  }
}