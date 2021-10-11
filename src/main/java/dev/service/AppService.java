package dev.service;

import org.springframework.web.multipart.MultipartFile;

public interface AppService {
    String excelToDB(MultipartFile File);
}
