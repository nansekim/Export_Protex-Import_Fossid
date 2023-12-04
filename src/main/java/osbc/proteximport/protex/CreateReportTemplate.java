package osbc.proteximport.protex;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxy;
import com.blackducksoftware.sdk.protex.common.ExternalDocumentLink;
import com.blackducksoftware.sdk.protex.report.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

import static osbc.proteximport.value.AllValues.allValues;

public class CreateReportTemplate {
    private static final Logger LOGGER = LogManager.getLogger(CreateReportTemplate.class);
    private ReportApi reportApi = null;
    private final String serverUri = allValues.protexLoginValues.getServerUri();
    private final String username = allValues.protexLoginValues.getId();
    private final String password = allValues.protexLoginValues.getPw();
    private final String reportTitle = "import_to_fossid_template";
    private ProtexServerProxy myProtexServer = null;

    {
        try {
            Long connectionTimeout = 120 * 1000L;
            myProtexServer = new ProtexServerProxy(serverUri, username, password, connectionTimeout);

            reportApi = myProtexServer.getReportApi();
        } catch (Exception e) {
            LOGGER.error("Connection to server '" + serverUri + "' failed: " + e.getMessage());
        }
    }

    public void suggestReportTemplates() throws Exception {
        try {
            List<ReportTemplate> reportTemplates = null;

            try {
                reportTemplates = reportApi.suggestReportTemplates(reportTitle);
            } catch (SdkFault e) {
                LOGGER.error("suggestReportTemplates() failed: " + e.getMessage());
                throw new RuntimeException(e);
            }

            if (reportTemplates == null || reportTemplates.isEmpty()) {
                LOGGER.info("No report templates matching the import_to_fossid_template");
                createReportTemplate();
            } else {
                for (ReportTemplate template : reportTemplates) {
                    LOGGER.info("Title: " + template.getName() + "; Id: " + template.getReportTemplateId());
                    if (reportTitle.equals(template.getName())) {
                        allValues.protexLoginValues.setReportTemplateID(template.getReportTemplateId());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("SampleSuggestReportTemplates failed");
            e.printStackTrace(System.err);
            System.exit(-1);
        } finally {
            // This is optional - it causes the proxy to overwrite the stored password with null characters, increasing
            // security
            if (myProtexServer != null) {
                myProtexServer.close();
            }
        }
    }

    private void createReportTemplate() {
        try {
            // Call the Api
            ReportTemplateRequest templateRequest = new ReportTemplateRequest();
            templateRequest.setName(reportTitle);
            templateRequest.setForced(Boolean.FALSE);

            ExternalDocumentLink externalDocumentLink1 = new ExternalDocumentLink();

            externalDocumentLink1.setDocumentId(null);

            templateRequest.getExternalDocumentLinks().add(externalDocumentLink1);

            ReportSection section1 = new ReportSection();
            section1.setLabel("File Inventory");
            section1.setSectionType(ReportSectionType.FILE_INVENTORY);

            ReportSection section2 = new ReportSection();
            section2.setLabel("Identified Files");
            section2.setSectionType(ReportSectionType.IDENTIFIED_FILES);

            templateRequest.getSections().add(section1);
            templateRequest.getSections().add(section2);

            String reportTemplateId = null;

            try {
                reportTemplateId = reportApi.createReportTemplate(templateRequest);
            } catch (SdkFault e) {
                LOGGER.error("createReportTemplate() failed: " + e.getMessage());
                throw new RuntimeException(e);
            }

            LOGGER.info("Create Report Template Id : " + reportTemplateId);
            allValues.protexLoginValues.setReportTemplateID(reportTemplateId);

        } catch (Exception e) {
            LOGGER.error("SampleCreateReportTemplate failed");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
