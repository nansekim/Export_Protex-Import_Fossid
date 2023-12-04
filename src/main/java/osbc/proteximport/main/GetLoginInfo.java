package osbc.proteximport.main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.util.Properties;

import static osbc.proteximport.value.AllValues.allValues;

public class GetLoginInfo {
	private static final Logger logger = LogManager.getLogger(GetLoginInfo.class);
	private static final String propsPath = System.getProperty("user.dir") + "\\config.properties";
	private static FileReader resources = null;
	private static Properties props = null;

	static {
		logger.info("config.properties path : " + propsPath);
		try {
			resources = new FileReader(propsPath);
			props = new Properties();
			props.load(resources);
		} catch (Exception e) {
			logger.error("Exception Message", e);
		}
	}

	public void getProtexLoginInfo() {
		try {
			allValues.protexLoginValues.setServerUri(props.getProperty("protex.domain"));
			allValues.protexLoginValues.setId(props.getProperty("protex.id"));
			allValues.protexLoginValues.setPw(props.getProperty("protex.pw"));
		} finally {
			try {
				if (resources != null) {
					resources.close();
				}
			} catch (Exception e) {
				logger.error("Exception Message", e);
			}
		}
	}

	public void getFossidLoginInfo(String protocol, String address, String userName, String apiKey) {
		try {
			String schema;
			String url;

			if(protocol.equals("")) {
				schema = props.getProperty("fossid.schema");
			} else {
				schema = protocol;
			}

			if(address.equals("")) {
				url = props.getProperty("fossid.domain");
			} else {
				url = address;
			}

			if(schema.equals("http")) {

				allValues.fossidLoginValues.setServerUri("http://" + url);

				//check "fossid.domain" to add / in front of api.php
				String temp = allValues.fossidLoginValues.getServerUri();
				temp = temp.substring(temp .length() - 1);

				if(temp.endsWith("/")) {
					allValues.fossidLoginValues.setServerApiUri("http://" + url + "api.php");
				} else {
					allValues.fossidLoginValues.setServerApiUri("http://" + url + "/api.php");
				}

			} else if(schema.equals("https")) {
				allValues.fossidLoginValues.setServerUri("https://" + url);

				//check "fossid.domain" to add / in front of api.php
				String temp = allValues.fossidLoginValues.getServerUri();
				temp = temp.substring(temp .length() - 1);

				if(temp.endsWith("/")) {
					allValues.fossidLoginValues.setServerApiUri("https://" + url + "api.php");
				} else {
					allValues.fossidLoginValues.setServerApiUri("https://" + url + "/api.php");
				}
			}

			String username = "";
			String apikey = "";

			if(userName.equals("")) {
				username = props.getProperty("fossid.username");
			} else {
				username = userName;
			}

			if(apiKey.equals("")) {
				apikey = props.getProperty("fossid.apikey");
			} else {
				apikey = apiKey;
			}

			allValues.fossidLoginValues.setUsername(username);
			allValues.fossidLoginValues.setApikey(apikey);
		} finally {
			try {
				if (resources != null) {
					resources.close();
				}
			} catch (Exception e) {
				logger.error("Exception Message", e);
			}
		}
	}
}
