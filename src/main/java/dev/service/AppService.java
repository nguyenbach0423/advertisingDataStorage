package dev.service;

import dev.helper.AdHelper;
import dev.helper.CampaignHelper;
import dev.helper.ExcelHelper;
import dev.mapper.AdMapper;
import dev.mapper.CampaignMapper;
import dev.model.Ad;
import dev.model.Campaign;
import dev.model.Error;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AppService {
    private int MAX_NUM_OF_RECORDS = 500;

    @Autowired
    CampaignMapper campaignMapper;
    @Autowired
    AdMapper adMapper;

    public String saveData (MultipartFile file) {
        ArrayList<Campaign> campaigns = new ArrayList<>();
        ArrayList<Ad> ads = new ArrayList<>();
        ArrayList<Error> errors = new ArrayList<>();

        ExcelHelper excelHelper = new ExcelHelper();

        try {
            ArrayList<Sheet> sheets = excelHelper.readExcelFile(file.getInputStream());

            sheets.forEach((sheet) -> {
                if (sheet.getSheetName().equals("Campaign")) {
                    CampaignHelper campaignHelper = new CampaignHelper();
                    ArrayList<Campaign> campaignArrayList = campaignHelper.getCampaignData(sheet);
                    errors.addAll(campaignHelper.getErrors());
                    if (errors.isEmpty()) {
                        campaigns.addAll(campaignArrayList);
                    }
                }
                else if (sheet.getSheetName().equals("Ad")) {
                    AdHelper adHelper = new AdHelper();
                    ArrayList<Ad> adArrayList = adHelper.getAdData(sheet);
                    errors.addAll(adHelper.getErrors());
                    if (errors.isEmpty()) {
                        ads.addAll(adArrayList);
                    }
                }
                else
                {
                    Error error = new Error();
                    error.setSheetName(sheet.getSheetName());
                    error.setErrorMessage("Sheet Name invalid.");
                    errors.add(error);
                }
            });

            if (errors.isEmpty()) {
                insertDataToDB(campaigns, ads);
                return "";
            } else {
                String errorsFileName = createErrorsFile(errors);
                return errorsFileName;
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String saveDataStreaming (MultipartFile file) {
        ArrayList<Campaign> campaigns = new ArrayList<>();
        ArrayList<Ad> ads = new ArrayList<>();
        ArrayList<Error> errors = new ArrayList<>();

        ExcelHelper excelHelper = new ExcelHelper();
        try {
            ArrayList<Sheet> sheets = excelHelper.readExcelFileStreaming(file.getInputStream());

            sheets.forEach((sheet) -> {
                if (sheet.getSheetName().equals("Campaign")) {
                    CampaignHelper campaignHelper = new CampaignHelper();
                    ArrayList<Campaign> campaignArrayList = campaignHelper.getCampaignDataStreaming(sheet);
                    errors.addAll(campaignHelper.getErrors());
                    if (errors.isEmpty()) {
                        campaigns.addAll(campaignArrayList);
                    }
                }
                else if (sheet.getSheetName().equals("Ad")) {
                    AdHelper adHelper = new AdHelper();
                    ArrayList<Ad> adArrayList = adHelper.getAdDataStreaming(sheet);
                    errors.addAll(adHelper.getErrors());
                    if (errors.isEmpty()) {
                        ads.addAll(adArrayList);
                    }
                }
                else
                {
                    Error error = new Error();
                    error.setSheetName(sheet.getSheetName());
                    error.setErrorMessage("Sheet Name invalid.");
                    errors.add(error);
                }
            });

            if (errors.isEmpty()) {
                insertDataToDB(campaigns, ads);
                return "";
            } else {
                String errorsFileName = createErrorsFile(errors);
                return errorsFileName;
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void insertDataToDB (ArrayList<Campaign> campaigns, ArrayList<Ad> ads) {
        long startTime = System.currentTimeMillis();
        int campaignSize = campaigns.size();
        for (int i = 0; i < campaignSize; i += MAX_NUM_OF_RECORDS){
            campaignMapper.insertMultiCampaign(campaigns.subList(i, Math.min(campaignSize, i + MAX_NUM_OF_RECORDS)));
        }

        int adSize = ads.size();
        for (int i = 0; i < adSize; i += MAX_NUM_OF_RECORDS){
            adMapper.insertMultiAd(ads.subList(i, Math.min(adSize, i + MAX_NUM_OF_RECORDS)));
        }
        long endTime = System.currentTimeMillis();
        System.out.println (endTime - startTime + "ms");
    }

    private String createErrorsFile (ArrayList<Error> errors) {
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
