
public final class SharedContext {

	private static final SharedContext SINGLETON = new SharedContext();
	
	public void addNode (String title) {
		log("context: " + title);
	}
	
	// returns Singleton instance
	public static SharedContext getIstance() {
		return SharedContext.SINGLETON;
	}
	
	private static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}
}
