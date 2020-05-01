import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import io.reactivex.rxjava3.core.*;
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
		PublishSubject<Voice> source = PublishSubject.<Voice>create();
		
		log("subscribing.");

		//generate a new publishsubject for each voice found util we reach desired depth
		source
		.observeOn(Schedulers.computation())
		.subscribe((s) -> {
			handleNode(s);
		}, Throwable::printStackTrace);
		
		startBase(source);
		try {
			Thread.sleep(200000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleNode(Voice node) {

		//context.addNode(node.getTitle());
		if(node.getDepth() < depth) {				
			PublishSubject<Voice> newNode = PublishSubject.<Voice>create();
			
			newNode
			.observeOn(Schedulers.computation())
			.subscribe((s) -> {
				handleNode(s);
			});
			
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
				log("GETTING TITLES FROM: " + node.getTitle());
				for (String title : titles) {
					log(""+ title); 
					newNode.onNext(new Voice(node.getDepth()+1, title));
				}
			}
}
	}
	
	private void startBase(PublishSubject<Voice> source) {
		log("starting...");
		
		new Thread(() -> {
			if(depth > 0){
				try {
					URL converted = new URL(base);
					httpClient client = new httpClient(converted);
					if(client.connect()) {
						List <String> titles = client.getResult();
						for (String title : titles) {
							log("source: "+ title); 
							source.onNext(new Voice(1, title));
						}
					}
				} catch (Exception ex){}
			}
		}).start();
	}
	
	private static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}
}
