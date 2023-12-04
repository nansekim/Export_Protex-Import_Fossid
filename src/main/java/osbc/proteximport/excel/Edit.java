package osbc.proteximport.excel;

import osbc.proteximport.value.ExportProjectValue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

public class Edit {
    private static final Logger LOGGER = LogManager.getLogger(Edit.class);
    private static final int ROW_INDEX = 2;
    private static final int CELL_INDEX_FILEPATH = 0;
    private static final int CELL_INDEX_SHA1 = 4;
    private static final int CELL_INDEX_MD5 = 5;

    public static void editExcel(File excelPath, ExportProjectValue projectValue) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        XSSFWorkbook xssfWorkbook = null;

        try {
            fileInputStream = new FileInputStream(excelPath);
            xssfWorkbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet fileInventory = xssfWorkbook.getSheetAt(1);

            fileInventory.setColumnWidth(CELL_INDEX_SHA1, 8200);
            fileInventory.setColumnWidth(CELL_INDEX_MD5, 8200);

            XSSFRow xssfRow = fileInventory.getRow(1);
            XSSFCell xssfCell = xssfRow.createCell(CELL_INDEX_SHA1);
            xssfCell.setCellValue("sha1");

            xssfCell = xssfRow.createCell(CELL_INDEX_MD5);
            xssfCell.setCellValue("md5");

            int maxRows = fileInventory.getPhysicalNumberOfRows();

            for (int i = ROW_INDEX; i < maxRows; i++) {
                xssfRow = fileInventory.getRow(i);

                if (xssfRow != null) {
                    xssfCell = xssfRow.getCell(CELL_INDEX_FILEPATH);

                    if (xssfRow.getCell(CELL_INDEX_FILEPATH) == null) {
                        continue;
                    }

                    String filePath = checkCellType(xssfCell);

                    LOGGER.debug(projectValue.getProjectName() + " : " + filePath);

                    Map<String, Map<String, String>> idFiles = projectValue.getIdFiles();

                    LOGGER.debug(idFiles);

                    Map<String, String> checkSum = idFiles.get(filePath);

                    if (checkSum != null) {
                        if (checkSum.get("sha1") != null) {
                            xssfCell = xssfRow.createCell(CELL_INDEX_SHA1);
                            xssfCell.setCellValue(checkSum.get("sha1"));
                        }
                        if (checkSum.get("md5") != null) {
                            xssfCell = xssfRow.createCell(CELL_INDEX_MD5);
                            xssfCell.setCellValue(checkSum.get("md5"));
                        }
                    }
                }
            }

            fileOutputStream  = new FileOutputStream("protex\\" + projectValue.getProjectName() + ".xlsx");
            xssfWorkbook.write(fileOutputStream);
            fileOutputStream.flush();

        } catch (Exception e) {
            LOGGER.error("Exception Message on " + projectValue.getProjectName(), e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (xssfWorkbook != null) {
                    xssfWorkbook.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                LOGGER.error("Exception Message", e);
            }
        }
    }

    private static String checkCellType(XSSFCell xssfCell) {
        String cellValue = "";

        switch (xssfCell.getCellType()) {
            case XSSFCell.CELL_TYPE_FORMULA:
                cellValue = xssfCell.getCellFormula();
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                cellValue = xssfCell.getNumericCellValue() + "";
                break;
            case XSSFCell.CELL_TYPE_STRING:
                cellValue = xssfCell.getStringCellValue();
                break;
            case XSSFCell.CELL_TYPE_BLANK:
                cellValue = xssfCell.getBooleanCellValue() + "";
                break;
            case XSSFCell.CELL_TYPE_ERROR:
                cellValue = xssfCell.getErrorCellString();
                break;
        }

        return cellValue;
    }
}
