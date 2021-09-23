package dev.controller;

import dev.message.ResponseMessage;
import dev.service.ExcelService;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExcelController {
    @Autowired
    ExcelService excelService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        excelService.clearErrorData();

        String message = "";

        if (file.getSize() <= 5 * 1024 * 1024) {
            String messageResponse = excelService.saveData(file);

            if (messageResponse.equals("")) {
                message = "Uploaded the file successfully:" + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } else {
                message = messageResponse;
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        else {
            String messageResponse = excelService.saveDataStreaming(file);

            if (messageResponse.equals("")) {
                message = "Uploaded the file successfully:" + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } else {
                message = messageResponse;
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
    }

    @GetMapping("/")
    public ResponseEntity<ArrayList<String>> loadErrorFile () {
        File folder = new File("src/main/resources/errorFiles");
        File[] files = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return ResponseEntity.ok().body(fileNames);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadErrorFile (@PathVariable String fileName) {
        try {
            File file = new File("src/main/resources/errorFiles/" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(fileInputStream, outputStream);

            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));

            fileInputStream.close();
            file.delete();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
