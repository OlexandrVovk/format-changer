package com.changer.view.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileConvertService {

    private final String UPLOAD_DIR_WORD = "/app/src/main/resources/static/documents/word/";
    private final String UPLOAD_DIR_PDF = "/app/src/main/resources/static/documents/pdf/";
    private final String CONVERT_URL = "http://gotenberg:3000/forms/libreoffice/convert";
    @SneakyThrows
    public File convertFile(HttpServletRequest request){
        File convertedPdf = null;
        Files.list(Paths.get(UPLOAD_DIR_PDF))
                .forEach(file -> file.toFile().delete());

        String fileName = request.getSession().getAttribute("fileName").toString();
        File inputFile = new File(UPLOAD_DIR_WORD + fileName);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpPost httpPost = new HttpPost(CONVERT_URL);
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", inputFile, ContentType.TEXT_HTML, inputFile.getName())
                    .build();
            httpPost.setEntity(entity);

            try(CloseableHttpResponse response = httpClient.execute(httpPost)){
                HttpEntity responseEntity = response.getEntity();
                inputFile.delete();
                if (responseEntity != null) {
                    InputStream inputStream = responseEntity.getContent();
                    fileName = fileName.replaceAll("\\.(.*)", ".pdf");
                    String outputPath = UPLOAD_DIR_PDF + fileName;
                    File outputFile = new File(outputPath);
                    try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    convertedPdf = outputFile;
                } else {
                    System.err.println("Empty response entity.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedPdf;
    }

    public void saveFile(MultipartFile file, HttpServletRequest request){
        try {
            Path path = Paths.get(UPLOAD_DIR_WORD + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            HttpSession session = request.getSession();
            session.setAttribute("fileName", file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public InputStream downloadFile(HttpServletRequest request){
        String fileName = request.getSession().getAttribute("fileName").toString();
        InputStream inputStream = new FileInputStream(new File(UPLOAD_DIR_PDF + fileName));
        return inputStream;
    }
}
