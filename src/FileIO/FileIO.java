package FileIO;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;

// -----------------------------------------------------
// Part: (include Part Number)
// Written by: (include your name(s) and student ID(s))
// -----------------------------------------------------
public class FileIO {
  
  static Scanner[] scanners = null;
  static PrintWriter[] writers = null;
  static String[] filenames = null;
  
  public static void main(String[] args) {
	// Enter data file names
    BufferedReader console = new BufferedReader(
      new InputStreamReader(System.in));
    System.out.print("Please enter data file names: ");
    
    String params = null;
    try {
    	params = console.readLine();
    } catch (IOException e) {
    	params = null;
    }
    if (params == null) {
    	System.out.print("Please enter valid file names.");
    	return;
    }
    
    StringTokenizer token = new StringTokenizer(params, ",");
    String[] inputParams = new String[100];
    int count = 0;
    while (token.hasMoreTokens())
    	inputParams[count++] = token.nextToken().trim();
    
    String[] inputFiles = new String[count];
    System.arraycopy(inputParams, 0, inputFiles, 0, count);
    
    // Start program
    Log log = Log.getInstance();
    
    scanners = new Scanner[inputFiles.length];
    writers = new PrintWriter[inputFiles.length];
    filenames = inputFiles;
    
    for (int i = 0; i < inputFiles.length; i++) {
      try {
        scanners[i] = new Scanner(new File(inputFiles[i]));
      } catch (IOException e) {
        System.out.println("Could not open input file " + inputFiles[i] + " for reading.");
        System.out.println("Please check if file exists! " +
          "Program will terminate after closing any opened files.");
        closeScanners();
        return;
      }
    }
    
    for (int i = 0; i < inputFiles.length; i++) {
      try {
        writers[i] = new PrintWriter(getJson(inputFiles[i]));
      } catch (IOException e) {
        System.out.println("Could not open input file " + inputFiles[i] + " for writing.");
        System.out.println("Program will terminate after closing any opened files.");
        
        closeScanners();
        closeWriters();
        return;
      }
    }
    
    /*
     */
    if (!processFilesForValidation(scanners, writers)) {
      closeScanners();
      cleanWriters();
      return;
    }

    /*
     * Ask the user to enter the name of one of the created output files to display
     */
    for (int i = 0; i < 2; ++i) {
      System.out.print("Enter JSON file name: ");
      String file = "";
      try {
        file = console.readLine();
      } catch (IOException e) {

      }
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null)
          System.out.println(line);
        break;
      } catch (FileNotFoundException e) {
        System.out.println("The file does not exist.");
      } catch (IOException e) {
        System.out.println("Failed to read the file.");
      } finally {
        try {
          if (reader != null)
            reader.close();
        } catch (IOException e) {
        }
      }
    }
    log.close();
    
    closeScanners();
    closeWriters();
  }
  
  static void cleanWriters() {
    closeWriters();
    for (String file : filenames)
      new File(getJson(file)).delete();
  }
  
  static void closeScanners() {
    for (Scanner scanner : scanners) {
      if (scanner != null)
        scanner.close();
    }
  }
  
  static void closeWriters() {
    for (PrintWriter writer : writers) {
      if (writer != null)
        writer.close();
    }
  }
  
  static String getJson(String file) {
    int n = file.lastIndexOf('.');

    return new String((n != -1 ? file.substring(0, n) : file) + ".json");
  }

  /*
   * This method will represent the core engine
   * for processing the input files and creating the output ones.
   */
  static boolean processFilesForValidation(Scanner[] scanners, PrintWriter[] writers) {
    try {
      for (int id = 0; id < filenames.length; ++id) {
        Scanner scanner = (Scanner)scanners[id];
        PrintWriter writer = (PrintWriter)writers[id];
        String file = filenames[id];
        
        // Check fields
        if (!scanner.hasNextLine())
          throw new CSVFileInvalidException(file, 0, null);
        String line = scanner.nextLine();
        int missing = 0;
        String[] fields = getRecords(line);
        for (String s : fields) {
          if (s.trim().isEmpty())
            missing++;
        }
        if (missing > 0)
          throw new CSVFileInvalidException(file, missing, fields);
        
        // Check records
        writer.println("[");
        boolean first = true;
        missing = 0;
        while (scanner.hasNextLine()) {
          missing++;
          line = scanner.nextLine();
          String[] records = getRecords(line);
          
          // Check if records are valid
          boolean valid = true;
          for (String record : records) {
            if (record.isEmpty()) {
              valid = false;
              break;
            }
          }
          if (!valid || records.length != fields.length) {
            writer.close();
            throw new CSVDataMissing(file, missing, fields, records);
          }

          if (!first)
            writer.println(",");
          first = false;

          writer.println("  {");
          for (int i = 0; i < fields.length; i++) {
            writer.println("    \"" + fields[i] + "\": \"" + 
              records[i] + "\",");
          }
          writer.print("  }");
        }
        writer.println();
        writer.println("]");
        writer.flush();
        writer.close();
      }
    } catch (CSVDataMissing | CSVFileInvalidException e) {
      return false;
    }
    return true;
  }
  
  static String[] getRecords(String line) {
    boolean bra = false;
    String field = new String();
    
    int count = 0;
    for (int i = 0; i < line.length(); ++i)
      if (line.charAt(i) == ',')
        ++count;
    String[] records = new String[++count];
    
    count = 0;
    for (int i = 0; i < line.length(); ++i) {
      char c = line.charAt(i);
      if (!bra && c == '\"') {
        bra = true; c = 0;
      }
      else if (bra && c == '\"') {
        bra = false; c = 0;
      }
      if (c == ',') {
        if (bra) {
          field += c;
        } else {
          records[count++] = field.trim();
          field = "";
        }
      } else {
        if (c != 0)
          field += c;
      }
    }
    records[count++] = field.trim();
    
    // Copy into return value
    String[] res = new String[count];
    System.arraycopy(records, 0, res, 0, count);
    
    return res;
  }
}