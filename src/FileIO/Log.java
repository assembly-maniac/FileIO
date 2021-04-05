package FileIO;
import java.io.FileWriter;
import java.io.IOException;

// -----------------------------------------------------
// Part: (include Part Number)
// Written by: (include your name(s) and student ID(s))
// -----------------------------------------------------
class Log {
  static Log log = null;
  static FileWriter writer = null;
  
  static Log getInstance() {
    if (log != null)
      return log;

    log = new Log();
    try {
      writer = new FileWriter("FileIO.log", true);
    } catch (IOException e) {
      System.out.println("Cannot open log file.");
      writer = null;
    }
    return log;
  }
  
  void close() {
    if (writer != null) {
      try {
    	writer.flush();
        writer.close();
      } catch (IOException e) {
      }
    }
  }
  
  void println(String s) {
    if (writer != null) {
      try {
        writer.write(s + "\r\n");
        writer.flush();
      } catch (IOException e) {
      }
    }
  }
}