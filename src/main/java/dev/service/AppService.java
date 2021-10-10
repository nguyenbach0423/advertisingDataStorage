package dev.service;

import org.springframework.web.multipart.MultipartFile;

public interface AppService {
    int MAX_NUM_OF_RECORDS = 500;
    String CAMPAIGN_SHEET_NAME = "Campaign";
    String AD_SHEET_NAME = "Ad";

    String excelToDB(MultipartFile File);
    String excelToDBStreaming(MultipartFile File);
}
