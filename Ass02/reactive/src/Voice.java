
public class Voice {
	final private int depth;
	final private String title;
	final private String father;
	
	public Voice(int depth, String title, String father) {
		this.depth = depth;
		this.title = title;
		this.father = father;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getFather() {
		return father;
	}
}
