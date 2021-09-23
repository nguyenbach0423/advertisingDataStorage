package dev.helper;

import dev.model.Campaign;
import dev.model.Error;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

public class CampaignHelper {
    private ArrayList<Error> errors = new ArrayList<>();

    public CampaignHelper() {}

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public ArrayList<Campaign> getCampaignData (Sheet sheet) {
        ArrayList<Campaign> campaigns = new ArrayList<>();

        Field[] fields = Campaign.class.getDeclaredFields();

        ArrayList<String> columnNameList = new ArrayList<>();
        Row header = sheet.getRow(0);
        for (Cell cell : header)
        {
            columnNameList.add(cell.getStringCellValue());
        }

        int errorExists = validateHeader(fields, columnNameList, sheet.getSheetName());
        if (errorExists == 0) {
            int[] indexes = getIndex(fields, columnNameList);

            int rows = sheet.getLastRowNum();
            int cells = sheet.getRow(0).getLastCellNum();

            for (int i = 1; i <= rows; i++) {
                int errorNum = 0;
                Campaign campaign = new Campaign();
                Row row = sheet.getRow(i);
                for (int j = 0; j < cells; j++) {
                    Cell cell = row.getCell(j);
                    String errorMessage = validateCampaignData(campaign, cell, indexes[j]);
                    if (!errorMessage.equals("")) {
                        errorNum++;
                        Error error = new Error(sheet.getSheetName(), sheet.getRow(0).getCell(j).getStringCellValue(), i + 1, errorMessage);
                        errors.add(error);
                    }
                }
                if (errorNum == 0 && campaign.getStartDate().compareTo(campaign.getEndDate()) >= 0) {
                    errorNum++;
                    Error error = new Error(sheet.getSheetName(), "End Date", i + 1, "Mustn't be before Start Date.");
                    errors.add(error);
                }
                if (errorNum == 0) {
                    campaigns.add(campaign);
                }
            }
        }
        return campaigns;
    }

    private String validateCampaignData (Campaign campaign, Cell cell, int index) {
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
                    campaign.setCampaignID(id);
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
                    campaign.setCampaignName(name);
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
                    campaign.setCampaignStatus(status);
            }
        }
        else if (index == 3)
        {
            if (cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
                errorMessage += "Invalid value type.";
            }
            else {
                long millis = System.currentTimeMillis();
                Date localDate = new Date(millis);
                Date start = cell.getDateCellValue();
                if (start.compareTo(localDate) >= 0 )
                    errorMessage += "Mustn't be past day.";
                else
                    campaign.setStartDate(start);
            }
        }
        else if (index == 4)
        {
            if (cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
                errorMessage += "Invalid value type.";
            }
            else {
                long millis = System.currentTimeMillis();
                Date localDate = new Date(millis);
                Date end = cell.getDateCellValue();
                if (end.compareTo(localDate) >= 0 )
                    errorMessage += "Mustn't be past day.";
                else
                    campaign.setEndDate(end);
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
                else if (value > 100000000)
                    errorMessage += "Mustn't be more than 100000000.";
                else
                    campaign.setBudget(value);
            }
        }

        return errorMessage;
    }

    public ArrayList<Campaign> getCampaignDataStreaming (Sheet sheet) {
        ArrayList<Campaign> campaigns = new ArrayList<>();

        Field[] fields = Campaign.class.getDeclaredFields();

        ArrayList<String> columnNameList = new ArrayList<>();
        for (Row row : sheet)
        {
            for (Cell cell : row)
            {
                columnNameList.add(cell.getStringCellValue());
            }
            break;
        }

        int errorExists = validateHeader(fields, columnNameList, sheet.getSheetName());
        if (errorExists == 0) {
            int[] indexes = getIndex(fields, columnNameList);
            int rowNum = 1;
            for (Row row : sheet) {
                int cellNum = 0;
                int errorNum = 0;
                Campaign campaign = new Campaign();
                for (Cell cell : row) {
                    String errorMessage = validateCampaignData(campaign, cell, indexes[cellNum]);
                    if (!errorMessage.equals("")) {
                        errorNum++;
                        Error error = new Error(sheet.getSheetName(), columnNameList.get(cellNum), rowNum + 1, errorMessage);
                        errors.add(error);
                    }
                    cellNum++;
                }
                if (errorNum == 0 && campaign.getStartDate().compareTo(campaign.getEndDate()) >= 0) {
                    errorNum++;
                    Error error = new Error(sheet.getSheetName(), "End Date", rowNum + 1, "Mustn't be before Start Date.");
                    errors.add(error);
                }
                if (errorNum == 0) {
                    campaigns.add(campaign);
                }
                rowNum++;
            }
        }

        return campaigns;
    }

    private int validateHeader (Field[] fields, ArrayList<String> columnNameList, String sheetName) {
        int errorExists = 0;
        for (String columnName : columnNameList)
        {
            int columnNameExists = 0;
            String columnNameDuplicate = columnName.replaceAll("\\s", "");
            columnNameDuplicate = columnNameDuplicate.toLowerCase();
            for (Field field : fields)
            {
                String fieldName = field.getName().toLowerCase();
                if (columnNameDuplicate.equals(fieldName)) {
                    columnNameExists = 1;
                    break;
                }
            }
            if (columnNameExists == 1)
            {
                continue;
            }
            else {
                errorExists = 1;
                Error error = new Error();
                error.setSheetName(sheetName);
                error.setHeaderName(columnName);
                error.setErrorMessage("Header Name invalid.");
                errors.add(error);
            }
        }
        return errorExists;
    }

    private int[] getIndex (Field[] fields, ArrayList<String> columnNameList) {
        int cells = columnNameList.size();
        int[] indexes = new int[cells];

        for (int i = 0; i < cells; i++)
        {
            String columnName = columnNameList.get(i);
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
