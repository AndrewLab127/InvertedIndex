import java.util.ArrayList;
import java.util.List;

public class Posting {
  public long DocId;
  public int freq;
  public List<Integer> Positions;
  
  public Posting(long docId) {
    this.DocId = docId;
    this.freq = 0;
    this.Positions = new ArrayList<Integer>();
  }
}