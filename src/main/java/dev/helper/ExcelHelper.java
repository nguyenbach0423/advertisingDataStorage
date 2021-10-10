package dev.helper;

import com.monitorjbl.xlsx.StreamingReader;
import dev.model.Error;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ExcelHelper {
    public ExcelHelper() {}

    public ArrayList<Sheet> readExcelFile (InputStream inputStream) {
        try {
            ArrayList<Sheet> sheets = new ArrayList<>();

            Workbook workbook = new XSSFWorkbook(inputStream);

            Iterator iterator = workbook.sheetIterator();
            while (iterator.hasNext())
            {
                Sheet sheet = (Sheet) iterator.next();
                sheets.add(sheet);
            }

            workbook.close();

            return sheets;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Sheet> readExcelFileStreaming (InputStream inputStream) {
        ArrayList<Sheet> sheets = new ArrayList<>();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(inputStream);
        for (Sheet sheet : workbook) {
            sheets.add(sheet);
        }

        return sheets;
    }

    public String createErrorsFile (ArrayList<Error> errors) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Error");

        int rowNum = 0;
        Row row;
        Cell cell;

        row = sheet.createRow(rowNum);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Sheet Name");

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Header Name");

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Row Number");

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Error Message");

        for (Error error : errors) {
            rowNum++;
            row = sheet.createRow(rowNum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(error.getSheetName());

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(error.getHeaderName());

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(error.getRowNumber());

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(error.getErrorMessage());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        String errorsFileName = "errors_" + dateFormat.format(date) + ".xlsx";
        String path = "src/main/resources/errorFiles/" + errorsFileName;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();
            return errorsFileName;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
