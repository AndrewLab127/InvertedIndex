import java.util.ArrayList;
import java.util.List;

/*
public class PostingList {
  
  public class Posting {
    public int DocId;
    public List<Integer> Positions;
    
    public Posting(int DocId) {
      this.DocId = DocId;
      this.Positions = new ArrayList<Integer>();
    }
  }
  
  public List<Posting> postings;
  
  public PostingList() {
    
    this.postings = new ArrayList<Posting>();
  }
  
}
*/

public class Posting {
  public int DocId;
  public int freq;
  public List<Integer> Positions;
  
  public Posting(int DocId) {
    this.DocId = DocId;
    this.freq = 0;
    this.Positions = new ArrayList<Integer>();
  }
}