package osbc.proteximport.fossid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static osbc.proteximport.value.AllValues.allValues;
public class ImportScan {
	private static final Logger LOGGER = LogManager.getLogger(ImportScan.class);
	public void importScan(String fileName, String scanCode) {
		JSONObject dataObject = new JSONObject();
		dataObject.put("username", allValues.fossidLoginValues.getUsername());
	    dataObject.put("key", allValues.fossidLoginValues.getApikey());
	    dataObject.put("scan_code", scanCode);

	    JSONObject rootObject = new JSONObject();
	    rootObject.put("group", "scans");
	    rootObject.put("action", "import_report");
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
				LOGGER.info("Start Importing " + fileName + " to ProjectName: " + scanCode + " / ScanName: " + scanCode);
	        }
			
			checkScanStatus(scanCode);

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void checkScanStatus(String scanCode) {
		// to map scan to project
		JSONObject dataObject = new JSONObject();
        dataObject.put("username", allValues.fossidLoginValues.getUsername());
        dataObject.put("key", allValues.fossidLoginValues.getApikey());
        dataObject.put("scan_code", scanCode);
        dataObject.put("type", "REPORT_IMPORT");
        
		JSONObject rootObject = new JSONObject();
        rootObject.put("group", "scans");
        rootObject.put("action", "check_status");
		rootObject.put("data", dataObject);
						
		//1000 = 1 second
		int intervals = 10 * 1000;

		try {
			while(true) {
				HttpPost httpPost = new HttpPost(allValues.fossidLoginValues.getServerApiUri());
				CloseableHttpClient httpClient = HttpClientBuilder.create().build();
				HttpResponse httpClientResponse;

				StringEntity entity = new StringEntity(rootObject.toString());
				httpPost.addHeader("content-type", "application/json");
				httpPost.setEntity(entity);
				httpClientResponse = httpClient.execute(httpPost);					
						
				if (httpClientResponse.getStatusLine().getStatusCode() != 200) {
					System.out.println("Failed : HTTP Error code : " + httpClientResponse.getStatusLine().getStatusCode());
					System.exit(1);					
				}
					
				BufferedReader br = new BufferedReader(
						new InputStreamReader(httpClientResponse.getEntity().getContent(), StandardCharsets.UTF_8));
				String result = br.readLine();				
					
				JSONParser jsonParser = new JSONParser();
			    JSONObject jsonObj1 = (JSONObject) jsonParser.parse(result);
			    JSONObject jsonObj2 = (JSONObject) jsonObj1.get("data");

				if(jsonObj1.get("status").toString().equals("0")) {
					LOGGER.warn("Importing " + scanCode + " report failed. Please, check the FossID log files.");
				}

				if(jsonObj2.get("status").equals("FINISHED")) {
					allValues.successScan.add(scanCode);

					break;
				}

				LOGGER.debug(result);

				Thread.sleep(intervals);

			    httpClient.close();
		    }
		} catch (Exception e) {
			LOGGER.error("Exception Message : " + e);
			e.printStackTrace();
		}
	}	

}
