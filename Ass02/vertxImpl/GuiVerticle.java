package vertxImpl;

import java.util.Arrays;
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
	private static final int DEPT = 0;
	
	@Override
	public void start(final Future<Void> startFuture) {
		eb = vertx.eventBus();
		graph = new SingleGraph("grafo");
		eb.consumer("init", message -> {
			 String[] f = message.body().toString().split(":");
			 String word = f[0]; 
			 int dept = Integer.parseInt(f[1]);
			 vertx.deployVerticle(new MyVerticle(DEPT,dept,Arrays.asList(word)));
		});
		eb.consumer("updateView", message -> {
			//view.updateView((List<String>)message.body());
			DataHolder c = (DataHolder) message.body();
			List<NodeTuple> newWords = c.getData();
			for(NodeTuple t : newWords) {
				System.out.println(t.getValue());
			}
			view.updateView(newWords);
		});
		eb.consumer("stop", message -> {
			vertx.close();
		});
		view = new Gui(500,180,eb);
		view.display();
	}
	
	@Override
	public void stop(final Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}
	
	private boolean nodeExists (String title) {
		return graph.getNode(title) != null;
	}
	
	private boolean edgeExists (String title) {
		return graph.getEdge(title) != null;
	}
	
	private void addNode (String title) {
		synchronized (graph) {
			try {
				if(!nodeExists(title)) {
					graph.addNode(title);
					graph.getNode(title).addAttribute("ui.label", graph.getNode(title).getId());
				}
			} catch(Exception e) {}
		}
	}
	
	private void addEdge (String title, String elem1, String elem2) {
		synchronized (graph) {
			try {
				if(!edgeExists(title)) {
					graph.addEdge(title, elem1, elem2);
				}
			} catch (Exception e) {}
		}
	}
}
