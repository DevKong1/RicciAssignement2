package executors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewLinkTask extends RecursiveAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SharedContext sharedContext;
	private URL link;
	private String content;
	private int depth;
	
	public NewLinkTask(final SharedContext sharedContext, final URL link) {
		this.sharedContext = sharedContext;
		this.link = link;
		this.content = link.toString().substring(30);
		this.depth = 0;
	}
	
	public NewLinkTask(final SharedContext sharedContext, final String content, final int depth) {
		this.sharedContext = sharedContext;
		this.content = content;
		this.depth = depth;
	}

	@Override
	protected void compute() {
		List<RecursiveAction> tasks = new LinkedList<RecursiveAction>();
		if(this.depth++ < this.sharedContext.getDepth()) {
			if(depth == 1) {
				sharedContext.addNode(sharedContext.getInitialUrl().substring(30));
			}
			
			JSONObject jsonObject = this.getConnectionResponse();
		    if(!jsonObject.has("parse")) {
		    	return;
		    }
		    
		    JSONArray jsonArray = jsonObject.getJSONObject("parse").getJSONArray("links");
		    sharedContext.addNode(content);
		    for(int i = 0; i < jsonArray.length(); i++) {
		    	if(jsonArray.getJSONObject(i).getInt("ns") == 0) {
		    		String str = jsonArray.getJSONObject(i).getString("*");
		    		NewLinkTask newLinkTask = new NewLinkTask(this.sharedContext, str, depth);
		    		tasks.add(newLinkTask);
		    		newLinkTask.fork();
		    		if(!this.sharedContext.getMasterList().contains(str)) {
			    		this.sharedContext.setMasterList(str);
						this.sharedContext.addNode(str);
			    		if(!this.sharedContext.edgeExists(content+str) && !this.sharedContext.edgeExists(str+content)) {
			    			this.sharedContext.addEdge(content+str, content, str);
			    		}
		    		}
		    	}
		    }
			
		    for(RecursiveAction task : tasks) {
		    	task.join();
		    }
		}
	}
	
	private void parseUrl() {
		String parsedUrl = sharedContext.getBasicUrl()+"w/api.php?action=parse&page="+this.content.replaceAll("\\s", "_")+"&format=json&section=0&prop=links";
		try {
			URL myURL = new URL(parsedUrl);
			this.link = myURL;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject getConnectionResponse() {
		JSONObject jsonObject = new JSONObject();
		try {
			this.parseUrl();
			HttpURLConnection connection = (HttpURLConnection)link.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			if(connection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
			    StringBuffer response = new StringBuffer();
			    while ((inputLine = reader.readLine()) != null) {
			    	response.append(inputLine);
			    }
			    reader.close();
			    jsonObject = new JSONObject(response.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
		
	}

}
