package osbc.proteximport.value;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExportProjectValue implements Serializable {
    public ExportProjectValue() {

    }
    private String projectName;
    private String projectId;
    private final Map<String, Map<String, String>> idFiles = new HashMap<>();

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Map<String, Map<String, String>> getIdFiles() {
        return idFiles;
    }

    public void setIdFiles(Map<String, Map<String, String>> idFile) {
        this.idFiles.putAll(idFile);
    }
}
