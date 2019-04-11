package com.qf.com.qf.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@Controller
@RequestMapping("/imgs")
public class ImgController {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    private static final String UPLOADER_PATH = "D:\\img\\";

    @RequestMapping("/uploader")
    @ResponseBody
    public String uploaderImg(MultipartFile file) {

        //获取图片后缀
        int index = file.getOriginalFilename().lastIndexOf(".");
        String houzhui = file.getOriginalFilename().substring(index + 1);
        System.out.println("后缀："+houzhui);
        try {
            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(),file.getSize(),houzhui,null);

            //获取上传到FastDFS中的图片访问路径
            String storeUrl = storePath.getFullPath();
            System.out.println("路径:"+storeUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //上传本地磁盘
        /*try(
                InputStream in = file.getInputStream();
                OutputStream out = new FileOutputStream(UPLOADER_PATH + UUID.randomUUID().toString());
        ) {
            IOUtils.copy(in,out);
        }catch (IOException e){
            e.printStackTrace();
        }*/
        return "succ";
        /*System.out.println("有图片开始上传！！"+file.getOriginalFilename());
        return null;*/
    }
}
