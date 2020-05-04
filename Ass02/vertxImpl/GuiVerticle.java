package vertxImpl;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

public class GuiVerticle extends AbstractVerticle {
	private Viewer view;
	private EventBus eb;
	private static final int DEPT = 0;
	@Override
	public void start(final Future<Void> startFuture) {
		eb = vertx.eventBus();
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
		view = new Viewer(620,620,eb);
		view.display();
	}
	
	@Override
	public void stop(final Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}
}
