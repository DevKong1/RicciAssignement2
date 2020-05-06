package vertxImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.rxjava.core.CompositeFuture;

public class MyVerticle extends AbstractVerticle {
	private static final String WIKI = "hhttps://it.wikipedia.org/w/api.php";
	private static final int ROOT = 0;
	private WebClient client;
	private List<NodeTuple> words;
	private EventBus eb;
	private int dept;
	private int maxDept;
	private List<String> assigned;
	private List<String> toAssign;
	private JsonArray links;
	
	public MyVerticle(int dept, int maxDept, final List<String> voices) {
		this.dept=dept;
		this.maxDept = maxDept;
		this.assigned = voices;
	}

	@Override
	public void start(Future<Void> startFuture) {
		System.out.println("I've been deployed!");
	    this.words = new LinkedList<NodeTuple>();
	    this.toAssign = new LinkedList<String>();
		eb = vertx.eventBus();
	    if(dept == ROOT) {
	    	ArrayList<NodeTuple> tmp = new ArrayList<NodeTuple>();
	    	tmp.add(new NodeTuple("root",assigned.get(ROOT)));
	    	eb.send("updateView", new DataHolder(tmp));
	    }
		Future<Void> steps;
		if(dept < maxDept) {
		     client = WebClient.create(vertx,
		            new WebClientOptions()
		                    .setSsl(true)
		                    .setTrustAll(true)
		                    .setDefaultPort(443)
		                    .setKeepAlive(true)
		                    .setDefaultHost("www.wikipedia.org")
	
		    );
			System.out.println("MyVerticle started!");
			//eb.send("updateView", new DataHolder(new NodeTuple(ROOT,assigned.get(0)));
			steps =  compute(assigned)
					.compose(v -> send())
					.onComplete(t -> {
						vertx.deployVerticle(new MyVerticle(dept,maxDept,assigned));
						vertx.undeploy(this.getClass().getName());
					});
		}else {
			eb.send("stop", "");
		}
	}

	@Override
	public void stop(final Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}

	private Future<Void> compute(List<String>toBeSearched) {
		Promise<Void> promise = Promise.promise();
		links = new JsonArray();
		List<Future<Void>>promises = new LinkedList<>();
		if(dept++ < maxDept) {
			for(String father : toBeSearched) {
				 //promise = Promise.promise();
				 client.get(WIKI)
			    .addQueryParam("action", "parse")
			    .addQueryParam("page", father)
			    .addQueryParam("format", "json")
			    .addQueryParam("section", "0")
			    .addQueryParam("prop", "links")
		        .as(BodyCodec.jsonObject())
		        .send(ar -> {
		            if (ar.succeeded()) {
		            	System.out.println("CERCANDO..");
		                promises.add(getWords(ar.result(),father));
		            } else {
		                ar.cause().printStackTrace();
		            }
		        });
			}
			System.out.println("HELLO");
			/*CompositeFuture.all(CompositeFuture.all(Arrays.asList(promises))).setHandler(ar ->{
				if(ar.succeeded()) {
					promise.complete();
				}
			});*/
		}else {
			eb.send("stop","");
		}
		return promise.future();
	}
	
	private Future<Void>getWords(HttpResponse<JsonObject> response,String father){
		Promise<Void> promise = Promise.promise();
		JsonObject body = response.body();
		/**
		 * Check if links has to be assigned or has to be links.add
		 */
		links = body.getJsonObject("parse").getJsonArray("links");
		System.out.println(links);
		//assigned = new ArrayList<String>();
		//words = new ArrayList<NodeTuple>();
		for (int i = 0; i < links.size(); i++) {
			String value = links.getJsonObject(i).getString("*");
			if (links.getJsonObject(i).getLong("ns") == 0) {
					words.add(new NodeTuple(father,value));
			}
			toAssign.add(value);
			if(i == links.size()-1) {
				promise.complete();
			}
		}
		return promise.future();
	}

	/*private Future<Void> updateWordList(final String father,final JsonArray links) {
		Promise<Void> promise = Promise.promise();
		System.out.println("Updating lists");
		assigned = new ArrayList<String>();
		words = new ArrayList<NodeTuple>();
		for (int i = 0; i < links.size(); i++) {
			String value = links.getJsonObject(i).getString("*");
			if (links.getJsonObject(i).getLong("ns") == 0) {
				if(dept==0) {
					words.add(new NodeTuple(null,value));
				}else {
					words.add(new NodeTuple(father,value));
				}
			}
			assigned.add(value);
		}
		promise.complete();
		System.out.println("Updated");
		return promise.future();
	}*/
	
	private Future<Void> send(){
		Promise<Void>promise = Promise.promise();
		System.out.println("Sending");
		eb.send("updateView", new DataHolder(words));
		promise.complete();
		System.out.println("SENT");
		return promise.future();
	}
	
	
}
