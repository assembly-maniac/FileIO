package FileIO;

// -----------------------------------------------------
// Part: (include Part Number)
// Written by: (include your name(s) and student ID(s))
// -----------------------------------------------------
class CSVDataMissing extends InvalidException {
  CSVDataMissing(String file, int line, String[] fields, String[] records) {
    super("In file " + file + " line " + line + " not converted to JSON : missing data.");

    Log log = Log.getInstance();
    log.println("In file " + file + " line " + line);
    
    String builder = "", missing = "Missing: ";
    
    boolean firstRecord = true, firstMissing = true;
    for (int i = 0; i < records.length; ++i) {
      if (!firstRecord)
        builder += " ";
      firstRecord = false;
      
      String record = records[i];
      if ("".equals(record)) {
        record = "***";
        
        if (!firstMissing)
          missing += ", ";
        firstMissing = false;
        
        missing += fields[i];
      }
      builder += record;
    }
    for (int i = records.length; i < fields.length; i++) {
      if (!firstMissing)
        missing += ", ";
      firstMissing = false;
      missing += fields[i];
    }

    log.println(builder);
    log.println(missing);
    log.println("");
  }
}