package vertxImpl;

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

public class MyVerticle extends AbstractVerticle {
	private static final String WIKI = "hhttps://it.wikipedia.org/w/api.php";
	private static final int ROOT = 0;
	private WebClient client;
	private EventBus eb;
	private int dept;
	private int maxDept;
	private List<String> assigned;
	private LinkedList<String> toAssign;
	private JsonArray links;
	private int instancies;

	public MyVerticle(int instancies, int dept, int maxDept, final List<String> voices) {
		this.dept = dept;
		this.maxDept = maxDept;
		this.assigned = voices;
		this.instancies = instancies;
	}

	@Override
	public void start(Future<Void> startFuture) {
		try {
			super.start(startFuture);
		} catch (Exception e) {
		}
		this.toAssign = new LinkedList<String>();
		eb = vertx.eventBus();

		/**
		 * The first verticle updates the view with the root node, the one passed as an
		 * argument by user.
		 */
		if (dept == ROOT) {
			NodeTuple tmp = new NodeTuple("root", assigned.get(ROOT));
			eb.send("updateView", new DataHolder(tmp));
		}

		/**
		 * Creates a vertx webClient that'll handle http requests.
		 */
		client = WebClient.create(vertx, new WebClientOptions().setSsl(true).setTrustAll(true).setDefaultPort(443)
				.setKeepAlive(true).setDefaultHost("www.wikipedia.org")

		);

		/**
		 * Computing is executed in two phases: 1- Gets links from wikipedia for every
		 * link assigned to this verticle and sends updates to GUIVerticle 2- creates a
		 * variable instancies of verticles that'll iterate these two phases.
		 */
		startFuture = compute(assigned).onComplete(t -> {
			dept++;
			if (dept == maxDept) {
				eb.send("stop", "");
			} else {
				int tmp = toAssign.size() / instancies;
				int oldTmp = 0;
				List<String> verticleWords;
				if (tmp > 0) {
					for (int i = 0; i < instancies; i++) {
						/*
						 * In case the number of words isn't perfectly splittable between instancies, the last instance
						 * gets the remaining words to be searched -> toAssing.Size() % instancies.
						 */
						if (i == instancies - 1) {
							verticleWords = toAssign.subList(oldTmp, toAssign.size());
							vertx.deployVerticle(new MyVerticle(instancies, dept, maxDept, verticleWords));
						} else {
							verticleWords = toAssign.subList(oldTmp, oldTmp += tmp);
							vertx.deployVerticle(new MyVerticle(instancies, dept, maxDept, verticleWords));
						}
					}
				} else {
					/*
					 * if the number of words to be searched is less than the number of instancies,
					 * a single verticle'll be deployed and every word'll be assigned to it.
					 */
					vertx.deployVerticle(new MyVerticle(instancies, dept, maxDept, toAssign));
				}
			}
		});
	}

	@Override
	public void stop(final Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
		System.out.println("MyVerticle at dept " + dept + " stopped!");
	}

	/**
	 * compute is a recursive method, for every word assigned to this verticle it
	 * makes a http request and invokes itself upon the next words. Once every word
	 * has been "computed", the promise'll complete.
	 * 
	 * @param toBeSearched list of words to be searched by this verticle
	 * @return a Future that'll be completed when an http request has been made for
	 *         every word in toBeSearched.
	 */
	private Future<Void> compute(final List<String> toBeSearched) {
		LinkedList<String> c = new LinkedList<String>();
		c.addAll(toBeSearched);
		Promise<Void> promise = Promise.promise();
		links = new JsonArray();
		if (dept < maxDept) {
			String father;
			if (c.size() > 0) {

				father = c.get(0);
				client.get(WIKI).addQueryParam("action", "parse").addQueryParam("page", father.replaceAll("\\s", "_"))
						.addQueryParam("format", "json").addQueryParam("section", "0").addQueryParam("prop", "links")
						.as(BodyCodec.jsonObject()).send(ar -> {
							if (ar.succeeded()) {
								/**
								 * ERRORE: Per ogni father crea un nuovo verticle passando alla depth
								 * successiva.
								 */
								sendWords(ar.result(), father).onComplete(t -> {
									c.remove(0);
									compute(c).onComplete(t2 -> {
										promise.complete();
									});

								});
							} else {
								System.out.println("404 NOT FOUND");
								c.remove(0);
									compute(c).onComplete(t2 -> {
									promise.complete();
								});
							}
						});
			} else {
				promise.complete();
			}

		}
		return promise.future();
	}

	/**
	 * Used to update view with new words, every time a http request has completed, the result is passed
	 * to this function that'll send every word contained in the result to the GuiVerticle through the event bus.
	 * @param response the response of the just completed http request
	 * @param father the father of the list of words contained in response.
	 * @return A future that'll be completed when every word has been sent trough the event bus
	 */
	Future<Void> sendWords(final HttpResponse<JsonObject> response, final String father) {
		Promise<Void> promise = Promise.promise();
		JsonObject body = response.body();

		if (body.getJsonObject("parse") == null || body.getJsonObject("parse").getJsonArray("links") == null) {
			System.out.println("LINK EMPTY, FATHER:" + father);
			promise.complete();
		} else {
			links = body.getJsonObject("parse").getJsonArray("links");
			for (int i = 0; i < links.size(); i++) {
				final int j = i;
				if (links.getJsonObject(i).getInteger("ns") == 0) {
					NodeTuple node = new NodeTuple(father, links.getJsonObject(i).getString("*"));
					updateView(node).onComplete(t -> {
						toAssign.add(node.getValue());
					});
				}
				if ((j == 0 && links.size() == 1) || j + 1 == links.size() - 1) {
					promise.complete();
				}

			}
		}
		return promise.future();
	}
	
	/**
	 * Sends a single word to the GuiVerticle through eventbus.
	 * @param word the word to be sent
	 * @return a future that'll be completed when the word has been sent.
	 */
	private Future<Void> updateView(final NodeTuple word) {
		Promise<Void> promise = Promise.promise();
		eb.send("updateView", new DataHolder(word));
		promise.complete();
		return promise.future();
	}

}
