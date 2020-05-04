import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public final class SharedContext {

	private static final SharedContext SINGLETON = new SharedContext();
	private boolean end = false;
	private Graph graph;
	
	public SharedContext() {
		graph = new SingleGraph("grafo");
		graph.setStrict(false);
		graph.display();
	}
	
	public boolean nodeExists (String title) {
		return graph.getNode(title) != null;
	}
	
	public boolean edgeExists (String title) {
		return graph.getEdge(title) != null;
	}

	public void addNode (String title, String color) {
		synchronized(graph) {
			Node node = graph.addNode(title);
			node.addAttribute("ui.label", title);
			node.addAttribute("ui.style", "fill-color: "+ color);
		}
	}
	
	public void addEdge (String title, String elem1, String elem2) {
		synchronized(graph) {
			graph.addEdge(title, elem1, elem2);
		}
	}
	
	public boolean isEnd () {
		return end;
	}
	
	
	// returns Singleton instance
	public static SharedContext getIstance() {
		return SharedContext.SINGLETON;
	}
	
	private static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}

}
