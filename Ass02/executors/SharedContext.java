package executors;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public final class SharedContext {

	private static final SharedContext SINGLETON = new SharedContext();
	private static String BASICURL;
	
	private List<String> guiLinks;
	private List<String> masterLinks;
	private int depth; 
	private boolean start;
	private String initialUrl;
	private Graph graph;
	
	public SharedContext() {
		guiLinks = new ArrayList<>();
		masterLinks = new ArrayList<>();
		start = false;
		graph = new SingleGraph("grafo");
		//graph.addAttribute("ui.stylesheet", "graph { fill-color: red; }");
		//graph.display();
	}
	
	public boolean nodeExists (String title) {
		return graph.getNode(title) != null;
	}
	
	public boolean edgeExists (String title) {
		return graph.getEdge(title) != null;
	}
	
	public void addNode (String title) {
		if(!nodeExists(title)) {
			graph.addNode(title);
		}
	}
	
	public void addEdge (String title, String elem1, String elem2) {
		if(!edgeExists(title)) {
			graph.addEdge(title, elem1, elem2);
		}
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	// returns Singleton instance
	public static SharedContext getIstance() {
		return SharedContext.SINGLETON;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public void setDepth(final int depth) {
		this.depth = depth;
	}
	
	public void setEnd(final boolean val) {
		this.start = false;
	}
	
	public List<String> getGuiList() {
		return this.guiLinks;
	}
	
	public void setGuiList(final String list) {
		this.guiLinks.add(list);
	}
	
	public List<String> getMasterList() {
		return this.masterLinks;
	}
	
	public void setMasterList(final String list) {
		this.masterLinks.add(list);
	}
	
	public String getInitialUrl() {
		return this.initialUrl;
	}
	
	public void setInitialUrl(final String url) {
		this.initialUrl = url;
	}
	
	public void running() {
		this.start = true;
		new Master().compute();
	}
	
	public boolean isStarted() {
		return this.start;
	}
	
	public void setBasicUrl() {
		SharedContext.BASICURL = this.initialUrl.toString().substring(0, 25);
	}
	
	public String getBasicUrl() {
		return SharedContext.BASICURL;
	}
	
	public static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}
}
