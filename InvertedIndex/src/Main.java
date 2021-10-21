import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Main { 
  
  public static boolean containsDoc(long docId, List<Posting> postings) {
    for(Posting p : postings) {
      if(p.DocId == docId) {
        return true;
      }
    }
    return false;
  }
  
  public static Posting getByDocId(long docId, List<Posting> postings) {
    for(Posting p : postings) {
      if(p.DocId == docId) {
        return p;
      }
    }
    return null; // if posting isn't found
  }
  
  @SuppressWarnings("unchecked")
  public static Map<String, List<Posting>> indexJsons(JSONArray jsons, List<String> scenes, List<String> plays) {//jsons the list of scenes, scenes the list of scenes and their id's
    Map<String, List<Posting>> map = new HashMap<String, List<Posting>>();
    
    long docId = 0;
    int pos;
    
    Iterator<JSONObject> iter = jsons.iterator(); //given collection C
    while(iter.hasNext()) { //while C has more objects
//      docId++;
      
      JSONObject j = iter.next(); //j = next JSONObject
      docId = (long)j.get("sceneNum");
      
      scenes.add(j.get("sceneId").toString()); 
      
      if(!plays.contains(j.get("playId").toString())) {
        plays.add(j.get("playId").toString());
      }
      
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
  
  public static int getCountBySceneId(String word, long sceneNum, Map<String, List<Posting>> map) {
    for(Posting p : map.get(word)) {
      if(p.DocId == sceneNum) {
        return p.freq;
      }
    }
    return 0; //docId not found
  }
  
  public static String getPlayIdBySceneNum(long sceneNum, JSONArray corpus) {
    Iterator<JSONObject> iter = corpus.iterator();
    JSONObject doc = null;
    
    while(iter.hasNext()) {
      doc = iter.next();
      
      if((long)doc.get("sceneNum") == sceneNum) {
        return doc.get("playId").toString();
      }
    }
    return null; //if playId is not found
  }
  
  public static boolean arrContains(String word, String[] arr) {
    for(String str : arr) {
      if(str.equals(word)) return true;
    }
    return false;
  }
  
  public static Set<String> findScenesByTerm(String term, Map<String, List<Posting>> map, JSONArray corpus, List<String> scenes) {
    Set<String> newScenes = new HashSet<String>();
    
    long sceneNum;
    
    for(Posting p : map.get(term)) {
      sceneNum = p.DocId;
      newScenes.add(scenes.get((int) sceneNum));
    }
    
    return newScenes;
  }
  
  public static Set<String> findPlaysByTerm(String term, Map<String, List<Posting>> map, JSONArray corpus) {
    Set<String> newPlays = new HashSet<String>();
    
    long sceneNum;
    
    for(Posting p : map.get(term)) {
      sceneNum = p.DocId;
      newPlays.add(getPlayIdBySceneNum(sceneNum, corpus));
    }
    
    return newPlays;
  }
  
  //terms0.txt Find scene(s) where the words thee or thou are used more frequently than the word you
  public static Set<String> term0(Map<String, List<Posting>> map, JSONArray corpus, List<String> scenes) {
    //List<String> newScenes = new ArrayList<String>();
    Set<String> newScenes = new HashSet<String>();
    
    Iterator<JSONObject> iter = corpus.iterator();
    JSONObject doc = null;
    
    int theeCount = 0;
    int thouCount = 0;
    int youCount = 0;
    long sceneNum = 0;
    
    while(iter.hasNext()) {
      doc = iter.next();
      sceneNum = (long)doc.get("sceneNum");
      
      theeCount = getCountBySceneId("thee", sceneNum, map);
      thouCount = getCountBySceneId("thou", sceneNum, map);
      youCount = getCountBySceneId("you", sceneNum, map);
      
      if(theeCount > youCount || thouCount > youCount) {
        newScenes.add(scenes.get((int)sceneNum));
      }
    }
    
    return newScenes;
  }
  
  //terms1.txt Find scene(s) where the place names venice, rome, or denmark are mentioned.
  public static Set<String> term1(Map<String, List<Posting>> map, JSONArray corpus, List<String> scenes) {
    //List<String> newScenes = new ArrayList<String>();
    Set<String> newScenes;
    
    Set<String> veniceScenes = findScenesByTerm("venice", map, corpus, scenes);
    Set<String> romeScenes = findScenesByTerm("rome", map, corpus, scenes);
    Set<String> denmarkScenes = findScenesByTerm("denmark", map, corpus, scenes);
    
    newScenes = new HashSet<String>(veniceScenes);
    newScenes.addAll(romeScenes);
    newScenes.addAll(denmarkScenes);
    
    return newScenes;
  }
  
  //terms2.txt Find the play(s) where the name goneril is mentioned.
  public static Set<String> term2(Map<String, List<Posting>> map, JSONArray corpus) {
    return findPlaysByTerm("goneril", map, corpus);
  }
  
  //terms3.txt Find the play(s) where the word soldier is mentioned
  public static Set<String> term3(Map<String, List<Posting>> map, JSONArray corpus) {
    return findPlaysByTerm("soldier", map, corpus);
  }
  
  //phrase0.txt Find scene(s) where "poor yorick" is mentioned.
  public static Set<String> phrase0(Map<String, List<Posting>> map, JSONArray corpus, List<String> scenes) {
    Set<String> newScenes = new HashSet<String>();
    
    long sceneNum;
    
    Set<String> poors = findScenesByTerm("poor", map, corpus, scenes);
    Set<String> yoricks = findScenesByTerm("yorick", map, corpus, scenes);
    
    Set<String> intersection = new HashSet<String>(poors); //finding the intersection between them
    poors.retainAll(yoricks);

    for(String scene : poors) {
      
    }
    
    return newScenes;
  }
  
  public static void writeCollectionToFile(File f, Map<String, List<Posting>> map) throws IOException {
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
  
  public static void writeListToFile(File f, List<String> scenes) throws IOException {
    FileWriter fw = new FileWriter(f);
    BufferedWriter bw = new BufferedWriter(fw);
    
    for(String scene : scenes) {
      bw.write(scene + "\n");
    }
    
    bw.close();
  }
  
  public static void writeSetToFile(File f, Set<String> scenes) throws IOException {
    FileWriter fw = new FileWriter(f);
    BufferedWriter bw = new BufferedWriter(fw);
    
    for(String scene : scenes) {
      bw.write(scene + "\n");
    }
    
    bw.close();
  }
  
  public static List<String> sortSet(Set<String> set) {
    List<String> list = new ArrayList<String>(set);
    Collections.sort(list);
    return list;
  }
  
  public static void main(String[] args) throws Exception {
    
    Map<String, List<Posting>> map = new HashMap<String, List<Posting>>();
    
    JSONArray corpus = getCorpus(new File("shakespeare-scenes.json"));

    List<String> scenes = new ArrayList<String>();
    List<String> plays = new ArrayList<String>();
    
    map = indexJsons(corpus, scenes, plays);
    
//    //Testing
//    writeListToFile(new File("plays.txt"), plays);
    
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
    writeListToFile(new File("terms0.txt"), sortSet(term0(map, corpus, scenes)));
    
    //Terms1
    writeListToFile(new File("terms1.txt"), sortSet(term1(map, corpus, scenes)));
    
    //Terms2
    writeListToFile(new File("terms2.txt"), sortSet(term2(map, corpus)));
    
    //Terms3
    writeListToFile(new File("terms3.txt"), sortSet(term3(map, corpus)));
    
    writeCollectionToFile(new File("map.txt"), map);
    
//    for(Map.Entry<String, List<Posting>> e : map.entrySet()) {
//      System.out.print(e.getKey());
//      for(Posting p : e.getValue()) {
//        System.out.println(" [" + p.DocId + ": Positions " + p.Positions + "] Frequency:" + p.freq);
//      }
//      System.out.println();
//    }
    
  }
}
