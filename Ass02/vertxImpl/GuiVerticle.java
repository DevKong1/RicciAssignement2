package vertxImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class GuiVerticle extends AbstractVerticle {
	private Gui view;
	private EventBus eb;
	private Graph graph;
	private List<NodeTuple> nodes;
	private int nodeIndex = 0;
	private int edgeIndex = 0;
	private static final int DEPT = 0;
	
	@Override
	public void start(final Future<Void> startFuture) {
		eb = vertx.eventBus();
		graph = new SingleGraph("grafo");
		view = new Gui(1024, 768,eb,graph);
		nodes = new LinkedList<NodeTuple>();
		view.display();
		eb.consumer("init", message -> {
			 String[] f = message.body().toString().split(":");
			 String word= f[0]; 
			 int dept = Integer.parseInt(f[1]);
			 vertx.deployVerticle(new MyVerticle(DEPT,dept,Arrays.asList(word)));
		});
		eb.consumer("updateView", message -> {
			System.out.println("RECEIVED");
			//view.updateView((List<String>)message.body());
			DataHolder c = (DataHolder) message.body();
			List<NodeTuple> newWords = c.getData();
			System.out.println(newWords.size());
			for(NodeTuple t : newWords) {
				System.out.println(t.getValue());
			}
			nodes.addAll(newWords);
			updateView(newWords);
		});
		eb.consumer("stop", message -> {
			vertx.close();
		});
	}
	
	@Override
	public void stop(final Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}
	
	private void updateView(final List<NodeTuple> values) {
		for(NodeTuple node : values) {		
			String value = node.getValue();
			String father = node.getFather();
			synchronized (graph) {
				try {
					if(!nodeExists(value)) {
						graph.addNode(value);
						graph.getNode(value).addAttribute("ui.label", graph.getNode(value).getId());
						System.out.println("NNOODDII: "+graph.getNodeCount());
						graph.addEdge(father+value,father,value);		
					}else {	
						graph.addEdge(father+value,father,value);
					}
				} catch(Exception e) {}
			}
		}
	}
	
	private boolean nodeExists (String title) {
		return graph.getNode(title) != null;
	}
}
