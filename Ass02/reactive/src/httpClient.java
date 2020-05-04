import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class httpClient {
	
	private URL url;
	private boolean isConnected;
	private List<String> result = new ArrayList<String>();

	public httpClient(final URL url) {
		this.url = url;
		this.isConnected = false;
	}
	
	public boolean connect() {
		 
		try {
			URL parsedUrl = parseUrl();
			HttpURLConnection connection = (HttpURLConnection)parsedUrl.openConnection();
			connection.setRequestMethod("GET");
			try {
			connection.connect();
			} catch (Exception ConnectException) {
				System.out.println("TIMEOUT");
				return false;
			}
			
			if(connection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
				String inputLine;
			    StringBuffer response = new StringBuffer();
			    while ((inputLine = reader.readLine()) != null) {
			    	response.append(inputLine);
			    }
			    reader.close();
			    JSONObject jsonObject = new JSONObject(response.toString());
			    JSONArray links;
			    try {
			    	links = jsonObject.getJSONObject("parse").getJSONArray("links");
			    } catch (Exception ex){
			    	links = null;
			    }
			    if(links != null) {
				    for(int i = 0; i < links.length(); i++) {
				    	if(links.getJSONObject(i).getInt("ns") == 0) {
				    		result.add(links.getJSONObject(i).getString("*"));
				    	}
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
	
	private URL parseUrl() {
		String basicUrl = this.url.toString().substring(0, 25);
		String content = this.url.toString().substring(30);
		String parsedStringUrl = basicUrl+"w/api.php?action=parse&page="+content+"&format=json&section=0&prop=links";
		URL myURL;
		try {
			myURL = new URL(parsedStringUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			myURL = null;
			e.printStackTrace();
		}
		return myURL;		
	}
	
	public List<String> getResult(){
		return isConnected ? result : null;
	}
}
