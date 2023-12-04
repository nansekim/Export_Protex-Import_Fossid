package osbc.proteximport.protex;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxy;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.ProjectColumn;
import com.blackducksoftware.sdk.protex.project.ProjectPageFilter;
import com.blackducksoftware.sdk.protex.util.PageFilterFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import osbc.proteximport.value.ExportProjectValue;

import java.util.ArrayList;
import java.util.List;

import static osbc.proteximport.value.AllValues.allValues;

public class GetDataProjectList {
    private static final Logger LOGGER = LogManager.getLogger(GetDataProjectList.class);
    private static final String serverUri = allValues.protexLoginValues.getServerUri();
    private static final String username = allValues.protexLoginValues.getId();
    private static final String password = allValues.protexLoginValues.getPw();
    private static ProjectApi projectApi = null;

    public List<ExportProjectValue> getProjectList() {
        long connectionTimeout = 120 * 1000L;

        ProtexServerProxy myProtexServer = null;

        List<ExportProjectValue> projectIdList = new ArrayList<>();

        try {
            try {
                myProtexServer = new ProtexServerProxy(serverUri, username, password, connectionTimeout);

                projectApi = myProtexServer.getProjectApi();
            } catch (RuntimeException e) {
                LOGGER.error("Connection to server '" + serverUri + "' failed: " + e.getMessage());
                throw e;
            }

            // Call the Api
            List<Project> projects;

            try {
                ProjectPageFilter pageFilter = PageFilterFactory.getAllRows(ProjectColumn.PROJECT_NAME);
                //TODO 임의로 프로젝트 개수 10개로 제한 (TEST 용 subList 삭제 필요)
                projects = projectApi.getProjects(pageFilter).subList(0, 10);
            } catch (SdkFault e) {
                LOGGER.error("getProjects() failed: " + e.getMessage());
                throw new RuntimeException(e);
            }

            LOGGER.info("Get Project List...");
            LOGGER.info("Project count : " + projects.size());

            // List returns can be null due to how apache CXF handles empty lists
            if (projects == null || projects.size() == 0) {
                LOGGER.info("No Projects assigned to user '" + username + "'");
            } else {
                for (Project project : projects) {
                    ExportProjectValue projectValue = new ExportProjectValue();
                    projectValue.setProjectName(project.getName());
                    projectValue.setProjectId(project.getProjectId());

                    projectIdList.add(projectValue);
                }
            }
        } catch (Exception e) {
            LOGGER.error("GetDataProjectList failed");
            LOGGER.error("Exception Message", e);
            System.exit(-1);
        } finally {
            // This is optional - it causes the proxy to overwrite the stored password with null characters, increasing
            // security
            try {
                if (myProtexServer != null) {
                    myProtexServer.close();
                }
            } catch (Exception e) {
                LOGGER.error("Exception Message", e);
            }
        }

        return projectIdList;
    }
}
