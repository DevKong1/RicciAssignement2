package executors;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection {
	
	private final URL url;
	private final String filePath;
	private boolean isConnected;

	public HttpConnection(URL url, String filePath) {
		this.url = url;
		this.filePath = filePath;
		this.isConnected = false;
	}
	
	public boolean connect() {
		 
		try {
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			if(connection.getResponseCode() == 200) {
				InputStream inputStream = connection.getInputStream();
				BufferedInputStream reader = new BufferedInputStream(inputStream);
				 
				BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(filePath));
				 
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				 
				while ((bytesRead = reader.read(buffer)) != -1) {
				    writer.write(buffer, 0, bytesRead);
				}
				 
				writer.close();
				reader.close();
				
				isConnected = true;
			} else {
				isConnected = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isConnected;
	}
}
