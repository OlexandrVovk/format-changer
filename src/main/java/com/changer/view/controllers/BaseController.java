package com.changer.view.controllers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;


@Controller
@AllArgsConstructor
public class BaseController {

    private final String UPLOAD_DIR_WORD = "/app/src/main/resources/static/documents/word/";
    private final String UPLOAD_DIR_PDF = "/app/src/main/resources/static/documents/pdf/";
    private final String CONVERT_URL = "http://gotenberg:3000/forms/libreoffice/convert";
    private final String[] AVAILABLE_FORMATS = {".bib", ".doc",  ".xml", ".docx", ".fodt",
            ".html",  ".ltx",  ".txt",  ".odt",  ".ott",  ".pdb",  ".pdf",  ".psw",  ".rtf",  ".sdw",
            ".stw", ".sxw",  ".uot",  ".vor",  ".wps",  ".epub",  ".png",  ".bmp",  ".emf",  ".eps",
            ".fodg",  ".gif",  ".jpg",  ".met",  ".odd",  ".otg",  ".pbm",  ".pct",  ".pgm",  ".ppm",
            ".ras",  ".std",  ".svg",  ".svm",  ".swf",  ".sxd",  ".sxw",  ".tiff",  ".xhtml",  ".xpm",
            ".fodp",  ".potm",  ".pot",  ".pptx",  ".pps",  ".ppt",  ".pwp",  ".sda",  ".sdd",  ".sti",
            ".sxi",  ".uop",  ".wmf",  ".csv",  ".dbf",  ".dif",  ".fods",  ".ods",  ".ots",  ".pxl",
            ".sdc",  ".slk",  ".stc",  ".sxc",  ".uos",  ".xls", ".xlt",  ".xlsx",  ".tif",  ".jpeg",
            ".odp",  ".odg",  ".dotx", ".xltx"};

    @GetMapping("/")
    public String index(@ModelAttribute("message") String message,
                        @ModelAttribute("fileAdded") String fileAdded,
                        Model model){
        model.addAttribute("fileAdded", Boolean.valueOf(fileAdded));
        model.addAttribute("message", message);
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            redirectAttributes.addFlashAttribute("fileAdded", false);
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        boolean fineFileFormat = false;
        for (String format : AVAILABLE_FORMATS) {
            if (fileName.contains(format)) {
                fineFileFormat = true;
                break;
            }
        }
        if (!fineFileFormat){
            redirectAttributes.addFlashAttribute("message", "Wrong file format, upload Office documents (Word, Excel, PowerPoint, etc.)");
            return "redirect:/";
        }
        try {
            Path path = Paths.get(UPLOAD_DIR_WORD + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            HttpSession session = request.getSession();
            session.setAttribute("fileName", fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        redirectAttributes.addFlashAttribute("fileAdded", true);
        return "redirect:/";
    }

    @SneakyThrows
    @GetMapping("/convert")
    public String convertDocument2(HttpServletRequest request, Model model){
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
                } else {
                    System.err.println("Empty response entity.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("fileName", fileName);
        request.getSession().setAttribute("fileName", fileName);
        return "download_page";
    }

    @SneakyThrows
    @GetMapping("/get-file")
    public void download(HttpServletResponse response, HttpServletRequest request) {
        String fileName = request.getSession().getAttribute("fileName").toString();
        InputStream inputStream = new FileInputStream(new File(UPLOAD_DIR_PDF + fileName));
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }


}
