package dev.service;

import dev.helper.AdHelper;
import dev.helper.CampaignHelper;
import dev.helper.ExcelHelper;
import dev.mapper.AdMapper;
import dev.mapper.CampaignMapper;
import dev.mapper.ErrorMapper;
import dev.model.Ad;
import dev.model.Campaign;
import dev.model.Error;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class ExcelService {
    @Autowired
    CampaignMapper campaignMapper;
    @Autowired
    AdMapper adMapper;
    @Autowired
    ErrorMapper errorMapper;

    public int saveData (MultipartFile file) throws IOException {
        ArrayList<Error> errors = new ArrayList<>();
        ArrayList<Campaign> campaigns = new ArrayList<>();
        ArrayList<Ad> ads = new ArrayList<>();

        ExcelHelper excelHelper = new ExcelHelper();
        ArrayList<Sheet> sheets = excelHelper.readExcelFile(file.getInputStream());

        sheets.forEach((sheet) ->{
            if (sheet.getSheetName().equals("Campaign"))
            {
                CampaignHelper campaignHelper = new CampaignHelper();
                ArrayList<Campaign> campaignArrayList = campaignHelper.getCampaignData(sheet);
                errors.addAll(campaignHelper.getErrors());
                if (errors.isEmpty())
                {
                    campaigns.addAll(campaignArrayList);
                }
            }
            if (sheet.getSheetName().equals("Ad"))
            {
                AdHelper adHelper = new AdHelper();
                ArrayList<Ad> adArrayList = adHelper.getAdData(sheet);
                errors.addAll(adHelper.getErrors());
                if (errors.isEmpty())
                {
                    ads.addAll(adArrayList);
                }
            }
        });

        if (errors.isEmpty())
        {
            campaigns.forEach((data) -> {
                campaignMapper.insertCampaign(data);
            });
            ads.forEach((data) -> {
                adMapper.insertAd(data);
            });
            return 1;
        }
        else
        {
            errors.forEach((data) -> {
                errorMapper.insertError(data);
            });
            return 0;
        }
    }
}
