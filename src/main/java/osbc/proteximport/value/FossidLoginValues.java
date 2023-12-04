package osbc.proteximport.value;

import java.io.Serializable;

public class FossidLoginValues implements Serializable {
    private static final FossidLoginValues FOSSID_LOGIN_VALUES = new FossidLoginValues();

    public FossidLoginValues() {

    }

    public static FossidLoginValues getInstance() {
        return FOSSID_LOGIN_VALUES;
    }

    private static String serverUri;
    private static String serverApiUri;
    private static String username;
    private static String apikey;

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        FossidLoginValues.serverUri = serverUri;
    }

    public String getServerApiUri() {
        return serverApiUri;
    }

    public void setServerApiUri(String serverApiUri) {
        FossidLoginValues.serverApiUri = serverApiUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        FossidLoginValues.username = username;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        FossidLoginValues.apikey = apikey;
    }
}
