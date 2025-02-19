package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;


/**
 * 通用Controller
 * 1、上传，下载操作
 * 2、
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //文件上传保存路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("进行文件上传");

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //利用UUID生成随机文件名，防止覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("文件上传成功");
        return R.success(fileName);
    }


    /**
     * 文件下载
     * @param response
     * @param name
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        /**
         * 具体逻辑如下：
         * 1). 定义输入流，通过输入流读取文件内容
         * 2). 通过response对象，获取到输出流
         * 3). 通过response对象设置响应数据格式(image/jpeg)
         * 4). 通过输入流读取文件数据，然后通过上述的输出流写回浏览器
         * 5). 关闭资源
         */
        log.info("进行文件下载");
        try {
            //1). 定义输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //2). 通过response对象，获取到输出流
            ServletOutputStream outputStream = response.getOutputStream();

            //3). 通过response对象设置响应数据格式(image/jpeg)
            response.setContentType("image/jpeg");

            //4). 通过输入流读取文件数据，然后通过上述的输出流写回浏览器
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //5). 关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return R.success("下载成功");
    }
}
