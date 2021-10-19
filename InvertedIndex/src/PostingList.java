import java.util.ArrayList;
import java.util.List;

public class PostingList {
  
  class Posting {
    public int DocId;
    public List<Integer> Positions;
    
    public Posting() {
      
      this.Positions = new ArrayList<Integer>();
    }
  }
  
  public List<Posting> postings;
  
  public PostingList() {
    
    this.postings = new ArrayList<Posting>();
  }
  
}
