package dev.service;

import dev.helper.AdHelper;
import dev.helper.CampaignHelper;
import dev.helper.ExcelHelper;
import dev.mapper.AdMapper;
import dev.mapper.CampaignMapper;
import dev.model.Ad;
import dev.model.Campaign;
import dev.model.Error;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Service (value = "AppService")
public class AppServiceImpl implements AppService{

    @Autowired
    CampaignMapper campaignMapper;
    @Autowired
    AdMapper adMapper;

    public String excelToDB (MultipartFile file) {
        ArrayList<Campaign> campaigns = new ArrayList<>();
        ArrayList<Ad> ads = new ArrayList<>();
        ArrayList<Error> errors = new ArrayList<>();

        ExcelHelper excelHelper = new ExcelHelper();

        try {
            ArrayList<Sheet> sheets = excelHelper.readExcelFile(file.getInputStream());

            sheets.forEach((sheet) -> {
                String sheetName = sheet.getSheetName();
                if (sheetName.equals(CAMPAIGN_SHEET_NAME)) {
                    CampaignHelper campaignHelper = new CampaignHelper();
                    ArrayList<Campaign> campaignArrayList = campaignHelper.getCampaignData(sheet);
                    errors.addAll(campaignHelper.getErrors());
                    if (errors.isEmpty()) {
                        campaigns.addAll(campaignArrayList);
                    }
                }
                else if (sheetName.equals(AD_SHEET_NAME)) {
                    AdHelper adHelper = new AdHelper();
                    ArrayList<Ad> adArrayList = adHelper.getAdData(sheet);
                    errors.addAll(adHelper.getErrors());
                    if (errors.isEmpty()) {
                        ads.addAll(adArrayList);
                    }
                }
                else
                {
                    Error error = getErrorSheetName(sheetName);
                    errors.add(error);
                }
            });

            if (errors.isEmpty()) {
                insertDataToDB(campaigns, ads);
                return "";
            } else {
                String errorsFileName = excelHelper.createErrorsFile(errors);
                return errorsFileName;
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String excelToDBStreaming (MultipartFile file) {
        ArrayList<Campaign> campaigns = new ArrayList<>();
        ArrayList<Ad> ads = new ArrayList<>();
        ArrayList<Error> errors = new ArrayList<>();

        ExcelHelper excelHelper = new ExcelHelper();

        try {
            ArrayList<Sheet> sheets = excelHelper.readExcelFileStreaming(file.getInputStream());

            sheets.forEach((sheet) -> {
                String sheetName = sheet.getSheetName();
                if (sheetName.equals(CAMPAIGN_SHEET_NAME)) {
                    CampaignHelper campaignHelper = new CampaignHelper();
                    ArrayList<Campaign> campaignArrayList = campaignHelper.getCampaignDataStreaming(sheet);
                    errors.addAll(campaignHelper.getErrors());
                    if (errors.isEmpty()) {
                        campaigns.addAll(campaignArrayList);
                    }
                }
                else if (sheetName.equals(AD_SHEET_NAME)) {
                    AdHelper adHelper = new AdHelper();
                    ArrayList<Ad> adArrayList = adHelper.getAdDataStreaming(sheet);
                    errors.addAll(adHelper.getErrors());
                    if (errors.isEmpty()) {
                        ads.addAll(adArrayList);
                    }
                }
                else
                {
                    Error error = getErrorSheetName(sheetName);
                    errors.add(error);
                }
            });

            if (errors.isEmpty()) {
                insertDataToDB(campaigns, ads);
                return "";
            } else {
                String errorsFileName = excelHelper.createErrorsFile(errors);
                return errorsFileName;
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Error getErrorSheetName (String sheetName) {
        Error error = new Error();
        error.setSheetName(sheetName);
        error.setErrorMessage("Sheet Name invalid.");
        return error;
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
}
