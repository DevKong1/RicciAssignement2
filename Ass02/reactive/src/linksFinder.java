import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class linksFinder {

	private final SharedContext context;
	private final String base;
	private final int depth;
	
	public linksFinder(final SharedContext context, String base, int depth) {
		this.context = context;
		this.base = base;
		this.depth = depth;
	}
	
	public void start() {
		
		Observable<Voice> source = Observable.create(emitter -> {	     
			log("starting...");
			
			new Thread(() -> {
				if(depth > 0){
					try {
						URL converted = new URL(base);
						httpClient client = new httpClient(converted);
						if(client.connect()) {
							context.addNode(base.split("/")[4],"rgb(0,0,0);");
							List <String> titles = client.getResult();
							for (String title : titles) {
								//log("source: "+ title); 
								emitter.onNext(new Voice(1, title, base.split("/")[4], generateColor()));
							}
						}
					} catch (Exception ex){}
				}
			}).start();			
		 });
		
		log("subscribing.");

		//generate a new Flowable for each voice found util we reach desired depth
		source
		.subscribeOn(Schedulers.io())
		.subscribe((s) -> {
			handleNode(s);
		}, Throwable::printStackTrace);
		
		while(!context.isEnd()) {		
		}
	}

	private void handleNode(Voice node) {

		if(!context.nodeExists(node.getTitle())) {
			
			context.addNode(node.getTitle(), node.getColor());
			context.addEdge(node.getFather()+node.getTitle(),node.getFather(),node.getTitle());			
			createAndSubscribe(node);
		} else {
			
			context.addEdge(node.getFather()+node.getTitle(),node.getFather(),node.getTitle());			
			createAndSubscribe(node);
		}
	}	
	
	private void createAndSubscribe(Voice node)
	{
		if(node.getDepth() < depth) {	
			
			Observable<Voice> newNode = Observable.create(emitter -> {	  
				URL converted = null;
				
				try {
					converted = new URL("https://it.wikipedia.org/wiki/"+node.getTitle());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				httpClient client = new httpClient(converted);
				if(client.connect() && client.getResult() != null) {
					
					List <String> titles = client.getResult();
					for (String title : titles) {
						emitter.onNext(new Voice(node.getDepth()+1, title, node.getTitle(), node.getColor()));
					}
				}
			});		
			
			newNode
			.subscribeOn(Schedulers.io())
			.subscribe((s) -> {				
				handleNode(s);
			});	
		}
	}

	private String generateColor() {
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		Color randomColor = new Color(r, g, b);
		return "rgb(" + randomColor.getRed() + "," + randomColor.getGreen() + "," + randomColor.getBlue() + ");";
	}
	
	private static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}
}
