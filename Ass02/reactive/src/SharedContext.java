import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import io.reactivex.rxjava3.subjects.PublishSubject;

public final class SharedContext {

	private static final SharedContext SINGLETON = new SharedContext();
	private PublishSubject<Integer> stream = PublishSubject.create();
	private Graph graph;
	
	public SharedContext() {
		graph = new SingleGraph("grafo");
		graph.setStrict(false);
	}
	
	public boolean nodeExists (String title) {
		synchronized(graph) {
			return graph.getNode(title) != null;
		}
	}
	
	public boolean edgeExists (String title) {
		synchronized(graph) {
			return graph.getEdge(title) != null;
		}
	}

	public void addNode (String title, String color) {
		synchronized(graph) {
			Node node = graph.addNode(title);
			node.addAttribute("ui.label", title);
			node.addAttribute("ui.style", "fill-color: "+ color);
			stream.onNext(graph.getNodeCount());
		}
	}
	
	public void addEdge (String title, String elem1, String elem2) {
		synchronized(graph) {
			graph.addEdge(title, elem1, elem2);
		}
	}	
	
	public Graph getGraph() {
		return graph;
	}
	
	public PublishSubject<Integer> getStream() {
		return stream;
	}
	
	// returns Singleton instance
	public static SharedContext getIstance() {
		return SharedContext.SINGLETON;
	}
	
	private static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}

}
