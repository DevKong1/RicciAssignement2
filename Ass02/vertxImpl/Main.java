package vertxImpl;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main {

	
	/*public void start(final String link, final int dept) {
		this.dept = dept;
		Main.link = link;

	}*/
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx(/*new VertxOptions().setAddressResolverOptions(
	    	      new AddressResolverOptions().setSearchDomains(Collections.emptyList())
	    	    )*/);    	    
	    vertx.eventBus().registerDefaultCodec(DataHolder.class, new DataCodec());
		vertx.deployVerticle(new GuiVerticle(),new DeploymentOptions().setWorker(true));
	}

}
