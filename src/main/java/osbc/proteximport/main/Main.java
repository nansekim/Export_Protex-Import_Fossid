package osbc.proteximport.main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import osbc.proteximport.excel.Edit;
import osbc.proteximport.fossid.*;
import osbc.proteximport.protex.*;
import osbc.proteximport.value.ExportProjectValue;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static osbc.proteximport.value.AllValues.allValues;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final long start = System.currentTimeMillis();
    public static List<File> xlsFiles;
    public static List<File> xlsxFiles;
    public static List<ExportProjectValue> projectIdList;

    public static void main(String[] args) {
        try {
            if (args[0].equals("-protex")) {
                protex(args);
            } else if (args[0].equals("-fossid")) {
                fossid(args);
            } else {
                LOGGER.warn("Please check first command (EX: -protex or -fossid)");
            }

            long end = System.currentTimeMillis();
            LOGGER.info("RunTime : " + (end - start)/60000 + "m" + ((end - start)%60000)/1000 + "s");
        } catch (Exception e) {
            LOGGER.error("Exception Message", e);
            System.exit(1);
        }
    }

    public static void protex(String[] args) {
        try {
            PrintInfo.protexStart();

            if (args.length > 1) {
                for (int i = 1; i < args.length; i += 2) {
                    switch (args[i]) {
                        case "--domain":
                            allValues.protexLoginValues.setServerUri(args[i + 1]);
                            break;
                        case "--id":
                            allValues.protexLoginValues.setId(args[i + 1]);
                            break;
                        case "--pw":
                            allValues.protexLoginValues.setPw(args[i + 1]);
                            break;
                    }
                }
            } else {
                GetLoginInfo getLoginInfo = new GetLoginInfo();
                getLoginInfo.getProtexLoginInfo();
            }

            PrintInfo.protexPrintInfo();

            //레포트 템플릿 생성 필요시 주석제거, 필요시 GenerateReport 수정필요
            //CreateReportTemplate createReportTemplate = new CreateReportTemplate();
            //createReportTemplate.suggestReportTemplates();

            GetDataProjectList getDataProjectList = new GetDataProjectList();
            projectIdList = getDataProjectList.getProjectList();

            makeFolder();

            GenerateReport generateReport = new GenerateReport();
            generateReport.generateReport(projectIdList);

            LOGGER.info("Get file info...");
            for (ExportProjectValue projectValue : projectIdList) {
                projectValue.setIdFiles(GetDataFileInfo.getFileInfo(projectValue.getProjectId()));
            }

            xlsFiles = getXlsList();

            LOGGER.info("Edit excel file...");

            for (File file : xlsFiles) {
                for (ExportProjectValue exportProjectValue : projectIdList) {
                    if (file.toString().contains(exportProjectValue.getProjectName())) {
                        Edit.editExcel(file, exportProjectValue);

                        break;
                    }
                }
            }

            ProtexWriteResult protexWriteResult = new ProtexWriteResult();
            protexWriteResult.writeImportList(projectIdList, xlsFiles);

            PrintInfo.protexEnd();
        } catch (Exception e) {
            LOGGER.error("Exception Message", e);
            System.exit(1);
        }
    }

    public static void fossid(String[] args) {
        try {
            PrintInfo.fossidStart();

            String protocol = "";
            String address = "";
            String userName = "";
            String apikey = "";

            for (int i = 1; i < args.length; i += 2) {
                switch (args[i]) {
                    case "--protocol":
                        protocol = args[i + 1];
                        break;
                    case "--domain":
                        address = args[i + 1];
                        break;
                    case "--username":
                        userName = args[i + 1];
                        break;
                    case "--apikey":
                        apikey = args[i + 1];
                        break;
                }
            }

            GetLoginInfo getLoginInfo = new GetLoginInfo();
            getLoginInfo.getFossidLoginInfo(protocol, address, userName, apikey);

            PrintInfo.fossidPrintInfo();

            xlsxFiles = getXlsxList();

            CreateProject createProject = new CreateProject();
            CreateScan createScan = new CreateScan();
            UploadReport uploadReport = new UploadReport();
            ImportScan importScan = new ImportScan();
            for (File file : xlsxFiles) {
                String fileName = file.getName();
                String filePath = file.getAbsolutePath();
                String projectName = fileName.substring(0, fileName.length() - 5);

                createProject.createProject(projectName);

                createScan.createScan(projectName);

                uploadReport.uploadReport(projectName, fileName, filePath);

                importScan.importScan(fileName, projectName);
            }

            FossidWriteResult fossidWriteResult = new FossidWriteResult();
            fossidWriteResult.writeImportList(xlsxFiles);

            PrintInfo.fossidEnd();
        } catch (Exception e) {
            LOGGER.error("Exception Message", e);
            System.exit(1);
        }
    }

    public static List<File> getXlsList() {
        File file = new File(System.getProperty("user.dir") + "\\protex");
        final FileFilter fileFilter = filter -> filter.getName().endsWith(".xls");

        return Arrays.stream(Objects.requireNonNull(file.listFiles(fileFilter))).collect(Collectors.toList());
    }

    public static List<File> getXlsxList() {
        File file = new File(System.getProperty("user.dir") + "\\protex");
        final FileFilter fileFilter = filter -> filter.getName().endsWith(".xlsx");

        return Arrays.stream(Objects.requireNonNull(file.listFiles(fileFilter))).collect(Collectors.toList());
    }

    public static void makeFolder () throws Exception {
        File file = new File(System.getProperty("user.dir") + "\\protex");
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new Exception("Make directory failed...");
            }
        }
    }
}
