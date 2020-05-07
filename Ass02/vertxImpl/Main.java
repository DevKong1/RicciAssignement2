package vertxImpl;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main {

	
	/*public void start(final String link, final int dept) {
		this.dept = dept;
		Main.link = link;

	}*/
	
	public static void main(String[] args) {
		System.out.println(Runtime.getRuntime().availableProcessors() + 1);
		Vertx vertx = Vertx.vertx(/*new VertxOptions().setAddressResolverOptions(
	    	      new AddressResolverOptions().setSearchDomains(Collections.emptyList())
	    	    )*/);
		DeploymentOptions options = new DeploymentOptions().setWorker(true);
	    vertx.eventBus().registerDefaultCodec(DataHolder.class, new DataCodec());
		vertx.deployVerticle(GuiVerticle.class,options);
	}

}
