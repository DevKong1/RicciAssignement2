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
	private WebClient client;
	private List<NodeTuple> words;
	private EventBus eb;
	private int dept;
	private int maxDept;
	private List<String> assigned;
	private JsonArray links;
	
	public MyVerticle(int dept, int maxDept, final List<String> voices) {
		this.dept=dept;
		this.maxDept = maxDept;
		this.assigned = voices;
	}

	@Override
	public void start(Future<Void> startFuture) {
		System.out.println("I've been deployed!");
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
		    this.words = new LinkedList<NodeTuple>();
			System.out.println("MyVerticle started!");
			eb = vertx.eventBus();
			steps =  compute(assigned)
					.compose(v -> updateWordList("0", links))
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
		                promises.add(getWords(ar.result()));
		    			System.out.println(promises.size());
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
	
	private Future<Void>getWords(HttpResponse<JsonObject> response){
		Promise<Void> promise = Promise.promise();
		JsonObject body = response.body();
		links.add(body.getJsonObject("parse").getJsonArray("links"));
		promise.complete();
		return promise.future();
	}
	private Future<Void> send(){
		Promise<Void>promise = Promise.promise();
		System.out.println("Sending");
		eb.send("updateView", new DataHolder(words));
		promise.complete();
		System.out.println("SENT");
		return promise.future();
	}
	

	private Future<Void> updateWordList(final String father,final JsonArray links) {
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
	}
	
}
