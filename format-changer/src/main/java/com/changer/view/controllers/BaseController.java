package com.changer.view.controllers;

import com.changer.view.services.FileConvertService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BaseController {
    private final String[] supportedFormats = {
            ".bib",".doc",".xml",".docx",".fodt",
            ".html.", "ltx",".txt",".odt",".ott",
            ".pdb",".pdf",".psw",".rtf",".sdw",
            ".stw",".sxw",".uot",".vor",".wps",
            ".epub",".png",".bmp",".emf",".eps",
            ".fodg",".gif",".jpg",".met",".odd",
            ".otg",".pbm",".pct",".pgm",".ppm",
            ".ras",".std",".svg",".svm",".swf",
            ".sxd",".sxw",".tiff",".xhtml",".xpm",
            ".fodp",".potm",".pot",".pptx",".pps",
            ".ppt",".pwp",".sda",".sdd",".sti",".sxi",
            ".uop", ".wmf", ".csv",".dbf",".dif",".fods",
            ".ods",".ots", ".pxl", ".sdc", ".slk", ".stc",
            ".sxc",".uos",".xls",".xlt",".xlsx",".tif",
            ".jpeg",".odp",".odg",".dotx",".xltx"
    };

    private final FileConvertService fileConvertService;

    @GetMapping("/")
    public String index(@ModelAttribute("message") String message,
                        @ModelAttribute("fileAdded") String fileAdded,
                        Model model){
        model.addAttribute("fileAdded", Boolean.valueOf(fileAdded));
        model.addAttribute("message", message);
        return "index";
    }

    @PostMapping("/")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            redirectAttributes.addFlashAttribute("fileAdded", false);
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        boolean wrongFileFormat = Arrays.stream(supportedFormats)
                .filter(fileFormats -> fileName.contains(fileFormats))
                .findFirst()
                .isEmpty();
        if (wrongFileFormat){
            redirectAttributes.addFlashAttribute("message", "Wrong file format, upload LibreOffice file");
            return "redirect:/";
        }
        fileConvertService.saveFile(file, request);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        redirectAttributes.addFlashAttribute("fileAdded", true);
        return "redirect:/";
    }

    @SneakyThrows
    @GetMapping("/convert")
    public String convertDocument(HttpServletRequest request, Model model){
        File convertedFile = fileConvertService.convertFile(request);
        model.addAttribute("fileName", convertedFile.getName());
        request.getSession().setAttribute("fileName", convertedFile.getName());
        return "download_page";
    }

    @SneakyThrows
    @GetMapping("/download")
    public void download(HttpServletResponse response, HttpServletRequest request) {
        InputStream inputStream = fileConvertService.downloadFile(request);
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }


}
