import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Main { 
  
  public static Map<String, PostingList> toMap(JSONObject[] jsons) {
    Map<String, PostingList> map = new HashMap<String, PostingList>();
    
    for(JSONObject j : jsons) {
      String[] terms = j.get("text").toString().split("\\s+"); //split the text into tokens
      
      for(String str : terms) { //go through tokens and create posting lists for each (inverted lists)
        map.put(str, new PostingList()); //for right now just making a new PostingList() object because idk wtf to do
      }
      
    }
    
    return map;
  }
  
  @SuppressWarnings("finally")
  public static JSONObject[] getCorpus(File f) throws Exception {
    JSONParser parser = new JSONParser();
    JSONObject jsonObj = null;
    
    try {
      
      jsonObj = (JSONObject) parser.parse(new FileReader(f));
      
    } catch(FileNotFoundException e) {
      
      e.printStackTrace();
    
    } catch(IOException e) {
    
      e.printStackTrace();
    
    } catch(ParseException e) {
    
      e.printStackTrace();
    
    } catch(Exception e) {
     
      e.printStackTrace();
    
    } finally {
      
      if(jsonObj == null) {
        throw new Exception("Error Parsing File, please provide a valid JSON File to parse");
      }
      return (JSONObject[]) jsonObj.get("Corpus");
    
    }
    
  }
  
  public static void main(String[] args) throws Exception {
    
    Map<String, PostingList> map = new HashMap<String, PostingList>();
    
    JSONObject corpus[] = getCorpus(new File("shakespeare-scenes.json"));
    
  }
}
