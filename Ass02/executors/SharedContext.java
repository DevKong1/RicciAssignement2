package executors;

import java.util.ArrayList;
import java.util.List;

public final class SharedContext {

	private static final SharedContext SINGLETON = new SharedContext();
	private static String BASICURL;
	
	private List<String> guiLinks;
	private List<String> masterLinks;
	private int depth; 
	private boolean start;
	private String initialUrl;
	
	public SharedContext() {
		guiLinks = new ArrayList<>();
		masterLinks = new ArrayList<>();
		start = false;
	}
	
	// returns Singleton instance
	public static SharedContext getIstance() {
		return SharedContext.SINGLETON;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public void setDepth(final int depth) {
		this.depth = depth;
	}
	
	public void setEnd(final boolean val) {
		this.start = false;
	}
	
	public List<String> getGuiList() {
		return this.guiLinks;
	}
	
	public void setGuiList(final String list) {
		this.guiLinks.add(list);
	}
	
	public List<String> getMasterList() {
		return this.masterLinks;
	}
	
	public void setMasterList(final String list) {
		this.masterLinks.add(list);
	}
	
	public String getInitialUrl() {
		return this.initialUrl;
	}
	
	public void setInitialUrl(final String url) {
		this.initialUrl = url;
	}
	
	public void running() {
		this.start = true;
		new Master().compute();
	}
	
	public boolean isStarted() {
		return this.start;
	}
	
	public void setBasicUrl() {
		SharedContext.BASICURL = this.initialUrl.toString().substring(0, 25);
	}
	
	public String getBasicUrl() {
		return SharedContext.BASICURL;
	}
	
	public static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
	}
}
