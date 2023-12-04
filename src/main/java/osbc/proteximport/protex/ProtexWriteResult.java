package osbc.proteximport.protex;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import osbc.proteximport.value.ExportProjectValue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProtexWriteResult {
    private static final Logger LOGGER = LogManager.getLogger(ProtexWriteResult.class);
	public void writeImportList(List<ExportProjectValue> projectList, List<File> xlsFiles) {
        try {
            File file = new File(System.getProperty("user.dir") + "\\export_project_list(protex).log");

            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new Exception("Make file failed...");
                }
            }
 
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            
            LocalDateTime now = LocalDateTime.now();
            String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));

            bufferedWriter.write(formatNow);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("--[Protex Project List]--");
            bufferedWriter.newLine();

            for (ExportProjectValue exportProjectValue : projectList) {
                bufferedWriter.write(exportProjectValue.getProjectName());
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
            bufferedWriter.write("--[Success Export Project List]--");
            bufferedWriter.newLine();

            for (File xlsFile : xlsFiles) {
                bufferedWriter.write(xlsFile.getName());
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();

            bufferedWriter.flush();

        } catch (Exception e) {
            LOGGER.error("Exception Message", e);
            e.printStackTrace();
        }
    }
}
