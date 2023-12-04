package osbc.proteximport.value;

import java.io.Serializable;

public class ProtexLoginValues implements Serializable {
    private static final ProtexLoginValues PROTEX_LOGIN_VALUES = new ProtexLoginValues();

    public ProtexLoginValues() {

    }

    public static ProtexLoginValues getInstance() {
        return PROTEX_LOGIN_VALUES;
    }

    private String serverUri;
    private String id;
    private String pw;
    private String reportTemplateID;

    public String getReportTemplateID() {
        return reportTemplateID;
    }

    public void setReportTemplateID(String reportTemplateID) {
        this.reportTemplateID = reportTemplateID;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
