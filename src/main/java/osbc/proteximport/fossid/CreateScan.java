package osbc.proteximport.fossid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static osbc.proteximport.value.AllValues.allValues;

public class CreateScan {
	private static final Logger LOGGER = LogManager.getLogger(CreateScan.class);
	public void createScan(String projectName) {
		
		String checkStatus = checkScanCode(projectName);
		
		if(checkStatus.equals("f")) {
			JSONObject dataObject = new JSONObject();
			dataObject.put("username", allValues.fossidLoginValues.getUsername());
		    dataObject.put("key", allValues.fossidLoginValues.getApikey());
		    dataObject.put("project_code", projectName);
		    dataObject.put("scan_code", projectName);
		    dataObject.put("scan_name", projectName);
		    dataObject.put("import_from_report", "1");
	
		    JSONObject rootObject = new JSONObject();
		    rootObject.put("group", "scans");
		    rootObject.put("action", "create");
			rootObject.put("data", dataObject);			
			
			HttpPost httpPost = new HttpPost(allValues.fossidLoginValues.getServerApiUri());
			HttpClient httpClient = HttpClientBuilder.create().build();		
			
			try {
				StringEntity entity = new StringEntity(rootObject.toString());
				httpPost.addHeader("content-type", "application/json");
				httpPost.setEntity(entity);
				
				HttpResponse httpClientResponse = httpClient.execute(httpPost);			
				
				
				if (httpClientResponse.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException(
							"Failed : HTTP error code : " + httpClientResponse.getStatusLine().getStatusCode());
				}	
				
				BufferedReader br = new BufferedReader(
						new InputStreamReader(httpClientResponse.getEntity().getContent(), StandardCharsets.UTF_8));
				String result = br.readLine();
	
				JSONParser jsonParser = new JSONParser();
		        JSONObject jsonObj = (JSONObject) jsonParser.parse(result);
				
		        if(jsonObj.get("status").equals("1")) {
					LOGGER.info("");
					LOGGER.info("ScanName: " + projectName + " / ScanCode: " + projectName +" is created");
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static String checkScanCode(String projectName) {
		JSONObject dataObject = new JSONObject();
        dataObject.put("username", allValues.fossidLoginValues.getUsername());
        dataObject.put("key", allValues.fossidLoginValues.getApikey());
		
		JSONObject rootObject = new JSONObject();
        rootObject.put("group", "scans");
        rootObject.put("action", "list_scans");
		rootObject.put("data", dataObject);		
		
		HttpPost httpPost = new HttpPost(allValues.fossidLoginValues.getServerApiUri());
		HttpClient httpClient = HttpClientBuilder.create().build();
		
        String checkStatus = "f";
				
		try {
			StringEntity entity = new StringEntity(rootObject.toString());
			httpPost.addHeader("content-type", "application/json");
			httpPost.setEntity(entity);
			
			HttpResponse httpClientResponse = httpClient.execute(httpPost);			
			
			
			if (httpClientResponse.getStatusLine().getStatusCode() != 200) {
				LOGGER.error("");
				LOGGER.error("FAILED: HTTP Error code: " + httpClientResponse.getStatusLine().getStatusCode());
				LOGGER.error("");
				System.exit(1);	
			}
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(httpClientResponse.getEntity().getContent(), StandardCharsets.UTF_8));
			String result = br.readLine();
			
			LOGGER.debug("list_scans : " + result);
			
			JSONParser jsonParser = new JSONParser();
	        JSONObject jsonObj1 = (JSONObject) jsonParser.parse(result);
			JSONObject jsonObj2 = (JSONObject) jsonObj1.get("data");

			// set key value of jsonObj2 and run loop while(until) iter has value
			for (Object o : jsonObj2.keySet()) {
				// set key value to key
				String key = (String) o;
				// get values from key
				JSONObject tempObj = (JSONObject) jsonObj2.get(key);

				if (projectName.equals(tempObj.get("code").toString())) {
					LOGGER.warn("The scanCode: \"" + projectName + "\" is exist");
					checkStatus = "t";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return checkStatus;	
	}
	
}
