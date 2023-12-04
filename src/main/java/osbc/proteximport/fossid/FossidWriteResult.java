package osbc.proteximport.fossid;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static osbc.proteximport.value.AllValues.allValues;
public class FossidWriteResult {
    private static final Logger LOGGER = LogManager.getLogger(FossidWriteResult.class);
	public void writeImportList(List<File> xlsxFiles) {
        try {
            File file = new File(System.getProperty("user.dir") + "\\import_project_list.log(fossid)");

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
            bufferedWriter.write("--[Export Xlsx List]--");
            bufferedWriter.newLine();

            for (File getFile : xlsxFiles) {
                bufferedWriter.write(getFile.getName());
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
            bufferedWriter.write("--[Success Scan List]--");
            bufferedWriter.newLine();

            for (String scanCode : allValues.successScan) {
                bufferedWriter.write(scanCode);
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
