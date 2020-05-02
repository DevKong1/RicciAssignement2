import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
							context.addNode(base.split("/")[4]);
							List <String> titles = client.getResult();
							for (String title : titles) {
								//log("source: "+ title); 
								emitter.onNext(new Voice(1, title, base.split("/")[4]));
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

		log("Adding node "+node.getTitle());
		context.addNode(node.getTitle());
		context.addEdge(node.getFather()+node.getTitle(),node.getFather(),node.getTitle());
		
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
						emitter.onNext(new Voice(node.getDepth()+1, title, node.getTitle()));
					}
				}
			});		
			
			newNode
			.subscribeOn(Schedulers.io())
			.subscribe((s) -> {				
				//if(!context.nodeExists(s.getTitle())) {
					//log("Adding node "+s.getTitle());
					//context.addNode(s.getTitle());
					//log("Adding edge "+s.getFather()+s.getTitle());
					//context.addEdge(s.getFather()+s.getTitle(),s.getFather(),s.getTitle());
					handleNode(s);
				//} else if(!context.edgeExists(s.getFather()+s.getTitle()) && !context.edgeExists(s.getTitle()+s.getFather())) {
					//log("Adding edge "+s.getFather()+s.getTitle());
					//context.addEdge(s.getFather()+s.getTitle(),s.getFather(),s.getTitle());
				//}
			});	
			/*
			newNode
			.observeOn(Schedulers.single())
			.subscribe((s) -> {
				log("GOT "+s.getTitle() + " FROM "+s.getFather());
			});*/
			
		}
	}
	
	private static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}
}
