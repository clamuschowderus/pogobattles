package pogobattles.dbutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ImportToDb {
  
  public static final String BASE_FOLDER = "C:\\Personal\\PoGo\\Gen2.2\\";
  
  public static void main(String[] args) throws Exception {
    Class.forName("org.hsqldb.jdbcDriver");
    
    Connection c = DriverManager.getConnection("jdbc:hsqldb:file:/Personal/Pogo/results.db", "SA", "");
   
    String inputFolder = BASE_FOLDER + "CountersMax\\";
    importAll(c, inputFolder, "COUNTERS");
    
    
    inputFolder = BASE_FOLDER + "PrestigersMax\\";
    importAll(c, inputFolder, "PRESTIGE_OPTIMAL");
    
    
    inputFolder = BASE_FOLDER + "1000PrestigeMax\\";
    importAll(c, inputFolder, "PRESTIGE_1000");
    
    c.close();
  }
  
  private static void importAll(Connection c, String inputFolder, String simType) throws Exception {
    File folder = new File(inputFolder);
    String[] fileList = folder.list();
    for(String fileName : fileList){
      if(fileName.endsWith(".csv")){
        
        System.out.println("Importing: " + inputFolder + fileName);
        
        BufferedReader reader = new BufferedReader(new FileReader(inputFolder + fileName));
        
        String line = reader.readLine(); // skip header
        
        while((line = reader.readLine())!=null){
          line += "," + simType; // add simulation type to the end of the line;
          String[] values = line.split(",");
          String query = buildInsertStatement(values);
          //System.out.println(query);
          PreparedStatement stmt = c.prepareStatement(query);
          try{
            stmt.execute();
          }catch(java.sql.SQLIntegrityConstraintViolationException icve){
            //do nothing
          }
          stmt.close();
        }
        
        reader.close();
      }
    }
  }
  
  private static String buildInsertStatement(String[] values){
    StringBuffer insertQuery = new StringBuffer();
    insertQuery.append("INSERT INTO RESULTS VALUES (");
    for(String value : values){
      insertQuery.append("'" + value + "',");
    }
    insertQuery.deleteCharAt(insertQuery.length()-1);
    insertQuery.append(")");
    return insertQuery.toString();
  }
}
