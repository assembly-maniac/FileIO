package FileIO;

// -----------------------------------------------------
// Part: (include Part Number)
// Written by: (include your name(s) and student ID(s))
// -----------------------------------------------------
class CSVFileInvalidException extends InvalidException {
  CSVFileInvalidException(String file, int missing, String[] fieldList) {
    super("File " + file + " is invlaid: field is missing.\r\n" +
      "File is not converted to JSON.");
    
    Log log = Log.getInstance();

    log.println("File " + file + " is invalid.");
    if (fieldList == null)
      return;
    
    log.println("Missing field: " + (fieldList.length - missing) + " detected, " + 
      missing + " missing");
    String builder = "";
    for (int i = 0; i < fieldList.length; ++i) {
      if (i != 0)
        builder += ", ";
      String field = (String)fieldList[i];
      builder += (field.isEmpty() ? "***" : field);
    }
    log.println(builder);
    log.println("");
  }
}