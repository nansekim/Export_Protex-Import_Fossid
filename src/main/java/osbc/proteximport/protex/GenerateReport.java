package osbc.proteximport.protex;

import com.blackducksoftware.sdk.protex.report.*;
import osbc.proteximport.value.AllValues;
import osbc.proteximport.value.ExportProjectValue;
import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxy;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static osbc.proteximport.protex.BDProtexSample.*;
import static osbc.proteximport.value.AllValues.allValues;

public class GenerateReport {
    private static final Logger LOGGER = LogManager.getLogger(GenerateReport.class);
    private static final String serverUri = allValues.protexLoginValues.getServerUri();
    private static final String username = allValues.protexLoginValues.getId();
    private static final String password = allValues.protexLoginValues.getPw();
    private static ReportApi reportApi = null;

    private static void usage() {
        String className = GenerateReport.class.getSimpleName();

        List<String> parameters = new ArrayList<String>(getDefaultUsageParameters());
        parameters.add("<project ID>");
        parameters.add("<report file name>");
        parameters.add("<table of contents>");

        List<String> paramDescriptions = new ArrayList<String>(getDefaultUsageParameterDetails());
        paramDescriptions.add(formatUsageDetail("project ID", "The project ID of the project to list, i.e. c_testproject"));
        paramDescriptions
                .add(formatUsageDetail(
                        "report file name",
                        "File name of the report (with desired extension), e.g., report.html. report file name must have one of the following extensions: .html, .xls, .doc, .odt, which also determine the format of the report."));
        paramDescriptions
                .add(formatUsageDetail(
                        "table of contents",
                        "the table of Contents of the report sections in the project report, e.g., true. If the report will have the table of contents of the available report sections, if false - no table of contents in the report"));

        outputUsageDetails(className, parameters, paramDescriptions);
    }

    public void generateReport(List<ExportProjectValue> projectValues) {
        Boolean showTOC = false;

        Long connectionTimeout = 120 * 1000L;

        ProtexServerProxy myProtexServer = null;

        try {
            reportApi = null;
            try {
                myProtexServer = new ProtexServerProxy(serverUri, username, password, connectionTimeout);

                // Set timeout for this API individually to indefinite - because reports can take a very long time.
                reportApi = myProtexServer.getReportApi(ProtexServerProxy.INDEFINITE_TIMEOUT);
            } catch (RuntimeException e) {
                LOGGER.error("Connection to server '" + serverUri + "' failed: " + e.getMessage());
                throw e;
            }

            LOGGER.info("Generate Report...");

            for (ExportProjectValue projectValue : projectValues) {
                String projectId = projectValue.getProjectId();
                String projectName = charCheck(projectValue.getProjectName());
                projectValue.setProjectName(projectName);
                String reportFileName = "protex\\" + projectName + ".xls";

                LOGGER.debug("FileName : " + reportFileName);

                if (getReportFormat(reportFileName) == null) {
                    LOGGER.error("Invalid reportFileName extension!");
                    usage();
                    System.exit(-1);
                }

                try {
                    // Call the Api
                    Report report;

                    try {
                        ReportTemplateRequest request = createSummaryReportTemplateRequest();
                        report = reportApi.generateAdHocProjectReport(projectId, request, getReportFormat(reportFileName), showTOC);
                    } catch (SdkFault e) {
                        LOGGER.error("generateProjectReport failed: " + e.getMessage());
                        throw new RuntimeException(e);
                    }

                    File transferredFile = new File(reportFileName);

                    try (FileOutputStream outStream = new FileOutputStream(transferredFile)) {
                        report.getFileContent().writeTo(outStream);
                        LOGGER.info(reportFileName);
                    } catch (IOException e) {
                        LOGGER.error("report.getFileContent().writeTo() failed: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    LOGGER.error("SampleGenerateReportFromTemplate failed");
                    LOGGER.error(System.err);
                    LOGGER.error("Exception Message", e);
                    System.exit(-1);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception Message", e);
        }finally {
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
    }

    private static ReportTemplateRequest createSummaryReportTemplateRequest() {
        ReportTemplateRequest reportTemplate = new ReportTemplateRequest();
        reportTemplate.setName("Sample Report");
        reportTemplate.setForced(Boolean.TRUE);

        ReportSection section1 = new ReportSection();
        section1.setLabel("File Inventory");
        section1.setSectionType(ReportSectionType.FILE_INVENTORY);

        ReportSection section2 = new ReportSection();
        section2.setLabel("Identified Files");
        section2.setSectionType(ReportSectionType.IDENTIFIED_FILES);

        reportTemplate.getSections().add(section1);
        reportTemplate.getSections().add(section2);

        return reportTemplate;
    }

    private String charCheck(String projectName) {
        String restrictChars = "[" + "|\\\\?*<\":>/" + "]+";
        return projectName.replaceAll(restrictChars, "");
    }

    private ReportFormat getReportFormat(String reportFileName) {
        String fileNameExtension = reportFileName.substring(reportFileName.lastIndexOf(".") + 1);
        switch (fileNameExtension) {
            case "html":
                return ReportFormat.HTML;
            case "xls":
                return ReportFormat.XLS;
            case "doc":
                return ReportFormat.MS_WORD;
            case "odt":
                return ReportFormat.ODF_TEXT;
            default:
                return null;
        }
    }
}
