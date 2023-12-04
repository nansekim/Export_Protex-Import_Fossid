package osbc.proteximport.fossid;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import static osbc.proteximport.value.AllValues.allValues;
public class UploadReport {
    private static final Logger LOGGER = LogManager.getLogger(UploadReport.class);
    public void uploadReport(String scanCode, String fileName, String filePath) {
        String fossidScanCode = base64Encode(scanCode);
        String fossidFileName = base64Encode(fileName);

        BufferedReader br = null;
        try {
            String uploadCurl = "curl "
                    + "--user nskim:tVyEW8AXGSOkxPO26YBmIAGo0R01hWfWdv6InRhKKl "
                    + "-H \"FOSSID-SCAN-CODE: " + fossidScanCode + "\" "
                    + "-H \"FOSSID-FILE-NAME: " + fossidFileName + "\" "
                    + "-X POST "
                    + "-T " + filePath + " "
                    + allValues.fossidLoginValues.getServerApiUri();

            LOGGER.debug("uploadCurl : " + uploadCurl);

            Process process = Runtime.getRuntime().exec(uploadCurl);
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String read;
            while ((read = br.readLine()) != null) {
                LOGGER.info(read);
            }
            process.waitFor();

            if (process.exitValue() == 0) {
                process.destroy();
            }
        } catch (Exception e) {
            LOGGER.error("Exception Message : " + e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                LOGGER.error("Exception Message : " + e);
            }
        }

    }

    private String base64Encode(String str) {
        return DatatypeConverter.printBase64Binary(str.getBytes());
    }
}
