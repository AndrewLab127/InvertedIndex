import java.util.ArrayList;
import java.util.List;

public class Posting {
  public int DocID;
  public List<Integer> Positions;
  
  public Posting(int DocID) {
    this.DocID = DocID;
    this.Positions = new ArrayList<Integer>();
  }
  
  public int getDunkedOn() {
    return 69;
  }
}
