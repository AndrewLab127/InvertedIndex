import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Main { 
  
  public static boolean containsDoc(int docId, List<Posting> postings) {
    for(Posting p : postings) {
      if(p.DocId == docId) {
        return true;
      }
    }
    return false;
  }
  
  public static Posting getByDocId(int docId, List<Posting> postings) {
    for(Posting p : postings) {
      if(p.DocId == docId) {
        return p;
      }
    }
    return null; // if posting isn't found
  }
  
  @SuppressWarnings("unchecked")
  public static Map<String, List<Posting>> indexJsons(JSONArray jsons, List<String> docs) {//jsons the list of docs, docs the list of docs and their id's
    Map<String, List<Posting>> map = new HashMap<String, List<Posting>>();
    
    int docId = 0;
    int pos;
    
    Iterator<JSONObject> iter = jsons.iterator(); //given collection C
    while(iter.hasNext()) { //while C has more objects
      docId++;
      
      JSONObject j = iter.next(); //j = next JSONObject
      
      docs.add(j.get("sceneId").toString()); 
      
      String[] tokens = j.get("text").toString().split("\\s+"); //parse(d)
      pos = 0;
      
      for(String str : tokens) {
        pos++;
        
        if(!map.containsKey(str)) { // if the map doesn't already contain the string
          map.put(str, new ArrayList<Posting>()); //add it to the map
        }
        
        List<Posting> postings = map.get(str);
        
        if(!containsDoc(docId, postings)) { // if the current string's postings doesn't already contain a doc with the docId
          postings.add(new Posting(docId)); //add it
        }
        
        Posting p = getByDocId(docId, postings); //get the string's posting by docId
        p.Positions.add(pos); //add position to it
        p.freq++;
      }
    }
    
    return map;
    
  }
  
  @SuppressWarnings("finally")
  public static JSONArray getCorpus(File f) throws Exception {
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
      return (JSONArray) jsonObj.get("corpus");
    
    }
    
  }
  
  //terms0.txt Find scene(s) where the words thee or thou are used more frequently than the word you
  public static List<String> term0(Map<String, List<Posting>> map, List<String> docs) {
    List<String> scenes = new ArrayList<String>();
    
    for(String doc : docs) { //for every scene in the collection
      //if word in
    }
    
    return scenes;
  }
  
  public static void term1(Map<String, List<Posting>> map) {
    
  }
  
  public static void writeToFile(File f, Map<String, List<Posting>> map) throws IOException {
    FileWriter fw = new FileWriter(f);
    BufferedWriter bw = new BufferedWriter(fw);
    
    for(Map.Entry<String, List<Posting>> e : map.entrySet()) {
      bw.write(e.getKey() + "\n");//System.out.print(e.getKey());
      for(Posting p : e.getValue()) {
        bw.write(" [" + p.DocId + ": Positions " + p.Positions + "] Frequency:" + p.freq + "\n"); //System.out.println(" [" + p.DocId + ": Positions " + p.Positions + "] Frequency:" + p.freq);
      }
      bw.write("\n");
    }
    
    bw.close();
    
  }
  
  public static void main(String[] args) throws Exception {
    
    Map<String, List<Posting>> map = new HashMap<String, List<Posting>>();
    
    JSONArray corpus = getCorpus(new File("shakespeare-scenes.json"));

    List<String> docs = new ArrayList<String>();
    
    map = indexJsons(corpus, docs);
    
    /*
      Terms
      terms0.txt Find scene(s) where the words thee or thou are used more frequently than the word you
      terms1.txt Find scene(s) where the place names venice, rome, or denmark are mentioned.
      terms2.txt Find the play(s) where the name goneril is mentioned.
      terms3.txt Find the play(s) where the word soldier is mentioned
      
      Phrases
      phrase0.txt Find scene(s) where "poor yorick" is mentioned.
      phrase1.txt Find the scene(s) where "wherefore art thou romeo" is mentioned.
      phrase2.txt Find the scene(s) where "let slip" is mentioned
     */
    
    //Terms0
    
    
    writeToFile(new File("map.txt"), map);
    
//    for(Map.Entry<String, List<Posting>> e : map.entrySet()) {
//      System.out.print(e.getKey());
//      for(Posting p : e.getValue()) {
//        System.out.println(" [" + p.DocId + ": Positions " + p.Positions + "] Frequency:" + p.freq);
//      }
//      System.out.println();
//    }
    
  }
}
