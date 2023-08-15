package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.uploadPath}")
    private String LOCAL_PATH;

    @PostMapping("/upload")
    public R<String> uploadFile(MultipartFile file){
        String fileOff = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = String.valueOf(UUID.randomUUID())+fileOff;

        try {
            file.transferTo(new File(LOCAL_PATH +fileName));
        } catch (IOException e) {
            log.info(String.valueOf(e));
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public  void downLoad(String name, HttpServletResponse response){
        try {
            FileInputStream fileInputStream = new FileInputStream(LOCAL_PATH+name);
            ServletOutputStream ouputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];

            int len = 0;
            while ((len =fileInputStream.read(bytes))  != -1){
                ouputStream.write(bytes,0,len);
                ouputStream.flush();
            }
            fileInputStream.close();
            ouputStream.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
