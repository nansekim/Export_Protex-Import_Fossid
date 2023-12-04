package osbc.proteximport.fossid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import osbc.proteximport.main.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static osbc.proteximport.value.AllValues.allValues;

public class CreateProject {
	private static final Logger LOGGER = LogManager.getLogger(CreateProject.class);

	public void createProject(String projectName) {
		
		String checkStatus = checkProjectCode(projectName);
		
		if(checkStatus.equals("f")) {
			JSONObject dataObject = new JSONObject();
			dataObject.put("username", allValues.fossidLoginValues.getUsername());
		    dataObject.put("key", allValues.fossidLoginValues.getApikey());
		    dataObject.put("project_code", projectName);
		    dataObject.put("project_name", projectName);

		    JSONObject rootObject = new JSONObject();
		    rootObject.put("group", "projects");
		    rootObject.put("action", "create");
			rootObject.put("data", dataObject);			
			
			HttpPost httpPost = new HttpPost(allValues.fossidLoginValues.getServerApiUri());
			HttpClient httpClient = null;
			
			try {
				httpClient = HttpClientBuilder.create().build();

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
					LOGGER.info("ProjectName: " + projectName + " / ProjectCode: " + projectName +  " is created");
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
    private static String checkProjectCode(String projectName) {
		// create json to call FOSSID project/list_projects api
		JSONObject dataObject = new JSONObject();
        dataObject.put("username", allValues.fossidLoginValues.getUsername());
        dataObject.put("key", allValues.fossidLoginValues.getApikey());
		
		JSONObject rootObject = new JSONObject();
        rootObject.put("group", "projects");
        rootObject.put("action", "list_projects");
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
			
			//System.out.println("list_projects" + result.toString());
			
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(result);
            
            Object object = jsonObj.get("data");
            if(object instanceof JSONArray){    
                // JSONArray
            	JSONArray dataArray = (JSONArray) jsonObj.get("data");
            	
                //set projectCode            
				for (Object o : dataArray) {
					JSONObject tempObj = (JSONObject) o;

					if (tempObj.get("project_code").equals(projectName)) {
						LOGGER.warn("The ProjectCode: \"" + projectName + "\" is exist");
						checkStatus = "t";
					}
				}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return checkStatus;		
	}
	
}
