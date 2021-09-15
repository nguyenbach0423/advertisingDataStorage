package dev.helper;

import dev.model.Ad;
import dev.model.Error;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class AdHelper {
    private ArrayList<Error> errors = new ArrayList<>();

    public AdHelper() {}

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public ArrayList<Ad> getAdData (Sheet sheet) {
        ArrayList<Ad> ads = new ArrayList<>();

        Field[] fields = Ad.class.getDeclaredFields();

        int[] indexes = getIndex(fields, sheet);

        int rows = sheet.getLastRowNum();
        int cells = sheet.getRow(0).getLastCellNum();

        for (int i = 1; i <= rows; i++) {
            int errorNum = 0;
            Ad ad = new Ad();
            Row row = sheet.getRow(i);
            for (int j = 0; j < cells; j++) {
                Cell cell = row.getCell(j);
                String errorMessage = validateAdData(ad, cell, indexes[j]);
                if (!errorMessage.equals(""))
                {
                    errorNum++;
                    Error error = new Error(sheet.getSheetName(), sheet.getRow(0).getCell(j).getStringCellValue(), i + 1, errorMessage);
                    errors.add(error);
                }
            }
            if (errorNum == 0) {
                ads.add(ad);
            }
        }
        return ads;
    }

    private String validateAdData (Ad ad, Cell cell, int index) {
        String errorMessage = "";
        if (index == 0)
        {
            if (cell.getCellType() != CellType.NUMERIC) {
                errorMessage += "Invalid value type.";
            }
            else {
                double value = cell.getNumericCellValue();
                long id = (long) value;
                if (id < 0)
                    errorMessage += "Invalid value.";
                else if (id > 9999999999L)
                    errorMessage += "Mustn't be more than 10 characters.";
                else
                    ad.setAdID(id);
            }
        }
        else if (index == 1)
        {
            if (cell.getCellType() != CellType.STRING) {
                errorMessage += "Invalid value type.";
            }
            else {
                String name = cell.getStringCellValue();
                if (name.length() > 25)
                    errorMessage += "Mustn't be more than 25 characters.";
                else
                    ad.setAdName(name);
            }
        }
        else if (index == 2)
        {
            if (cell.getCellType() != CellType.STRING) {
                errorMessage += "Invalid value type.";
            }
            else {
                String status = cell.getStringCellValue();
                if (!status.equals("Active") && !status.equals("Paused") && !status.equals("Removed")) {
                    errorMessage += "Only takes values: Active, Paused, Removed.";
                }
                else
                    ad.setAdStatus(status);
            }
        }
        else if (index == 3)
        {
            if (cell.getCellType() != CellType.STRING) {
                errorMessage += "Invalid value type.";
            }
            else {
                String type = cell.getStringCellValue();
                if (!type.equals("Search") && !type.equals("Display") && !type.equals("Video")) {
                    errorMessage += "Only takes values: Search, Display, Video.";
                }
                else
                    ad.setAdType(type);
            }
        }
        else
        {
            if (cell.getCellType() != CellType.NUMERIC) {
                errorMessage += "Invalid value type.";
            }
            else {
                double value = cell.getNumericCellValue();
                if (value < 0)
                    errorMessage += "Invalid value.";
                else if (value > 5000)
                    errorMessage += "Mustn't be more than 5000.";
                else
                    ad.setBidModifier(value);
            }
        }

        return errorMessage;
    }

    private int[] getIndex(Field[] fields, Sheet sheet) {
        int cells = sheet.getRow(0).getLastCellNum();
        int[] indexes = new int[cells];
        for (int i = 0; i < cells; i++)
        {
            String columnName = sheet.getRow(0).getCell(i).getStringCellValue();
            columnName = columnName.replaceAll("\\s", "");
            for (int j = 0; j < fields.length; j++)
            {
                if (columnName.toLowerCase().equals(fields[j].getName().toLowerCase()))
                {
                    indexes[i] = j;
                    break;
                }
            }
        }
        return indexes;
    }
}
