package osbc.proteximport.protex;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxy;
import com.blackducksoftware.sdk.protex.project.codetree.CharEncoding;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.SourceFileInfoNode;
import com.blackducksoftware.sdk.protex.util.CodeTreeUtilities;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static osbc.proteximport.value.AllValues.allValues;

public class GetDataFileInfo {
    private static final Logger LOGGER = LogManager.getLogger(GetDataFileInfo.class);
    private static final String serverUri = allValues.protexLoginValues.getServerUri();
    private static final String username = allValues.protexLoginValues.getId();
    private static final String password = allValues.protexLoginValues.getPw();
    private static CodeTreeApi codetreeApi = null;

    public static Map<String, Map<String, String>> getFileInfo(String projectId) {
        Map<String, Map<String, String>> fileInfoForId = new HashMap<>();

        Long connectionTimeout = 120 * 1000L;

        ProtexServerProxy myProtexServer = null;

        try {
            codetreeApi = null;
            try {
                myProtexServer = new ProtexServerProxy(serverUri, username, password, connectionTimeout);

                codetreeApi = myProtexServer.getCodeTreeApi();
            } catch (RuntimeException e) {
                LOGGER.error("Connection to server '" + serverUri + "' failed: " + e.getMessage());
                throw new RuntimeException(e);
            }

            String root = "/";

            List<SourceFileInfoNode> fileInfo = null;

            try {
                fileInfo = codetreeApi.getFileInfo(projectId, root, CodeTreeUtilities.INFINITE_DEPTH, Boolean.TRUE, CharEncoding.BASE_64);
            } catch (SdkFault e) {
                LOGGER.warn("getFileInfo() failed: " + e.getMessage());
            }

            if (fileInfo != null) {
                for (SourceFileInfoNode node : fileInfo) {
                    Map<String, String> checkSum = new HashMap<>();
                    checkSum.put("sha1", node.getExactSha1Checksum());
                    checkSum.put("md5", node.getExactChecksum());

                    fileInfoForId.put(node.getName(), checkSum);
                }
            }

        } catch (Exception e) {
            LOGGER.error("GetFileInfo failed");
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


        return fileInfoForId;
    }

}
