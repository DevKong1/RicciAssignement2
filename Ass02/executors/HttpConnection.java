package executors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;


public class HttpConnection {
	
	private URL url;
	private boolean isConnected;

	public HttpConnection(final URL url) {
		this.url = url;
		this.isConnected = false;
	}
	
	public boolean connect() {
		 
		try {
			this.parseUrl();
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			if(connection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
				String inputLine;
			    StringBuffer response = new StringBuffer();
			    while ((inputLine = reader.readLine()) != null) {
			    	response.append(inputLine);
			    }
			    reader.close();
			    JSONObject jsonObject = new JSONObject(response.toString());
			    JSONArray links = jsonObject.getJSONObject("parse").getJSONArray("links");
			    for(int i = 0; i < links.length(); i++) {
			    	if(links.getJSONObject(i).getInt("ns") == 0) {
			    		System.out.println("" + links.getJSONObject(i).getString("*"));
			    	}
			    }
				isConnected = true;
			} else {
				isConnected = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isConnected;
	}
	
	private void parseUrl() {
		String basicUrl = this.url.toString().substring(0, 25);
		String content = this.url.toString().substring(30);
		String parsedUrl = basicUrl+"w/api.php?action=parse&page="+content+"&format=json&section=0&prop=links";
		try {
			URL myURL = new URL(parsedUrl);
			this.url = myURL;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
