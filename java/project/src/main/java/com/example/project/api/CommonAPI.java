package com.example.project.api;

import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 其他api
 */
@Controller
@RequestMapping("/common")
public class CommonAPI {

    @Autowired
    private CommonService commonService;

    @Value("${project.filePath.imgPath}")
    private String imgPath;

    /**
     * 条件检索
     * @param keyword
     * @param genres
     * @param styles
     * @param formats
     * @param forms
     * @param countries
     * @param decades
     * @param currentPage
     * @param pageSize
     * @param order
     * @param area
     * @return
     */
    @GetMapping("/search")
    @ResponseBody
    public Result info(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "genre", required = false) List<String> genres,
            @RequestParam(name = "style", required = false) List<String> styles,
            @RequestParam(name = "format", required = false) List<String> formats,
            @RequestParam(name = "form", required = false) List<String> forms,
            @RequestParam(name = "country", required = false) List<String> countries,
            @RequestParam(name = "decade", required = false) List<String> decades,
            @RequestParam(name = "currentPage", required = false, defaultValue = "1") int currentPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "12") int pageSize,
            @RequestParam(name = "order", required = false, defaultValue = "1") int order,
            @RequestParam("area") String area
    ) {
        Paging result = commonService.info(keyword, genres, styles, formats, forms, countries, decades, currentPage, pageSize, order, area);
        if(result.getData() != null) {
            return Result.success(result);
        } else {
            return Result.error("Get info failed!");
        }
    }

    /**
     * 图片上传
     * @param file
     * @return
     */
    @PutMapping("/uploadImg")
    @ResponseBody
    public Result upload(@RequestParam("file") MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后文件自动消失
        System.out.println("114514 "+imgPath);

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成的文件覆盖
        String fileName = UUID.randomUUID() + suffix;

        //如果存放目录不存在，创建目录
        File dir = new File(imgPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(imgPath + fileName));
            return Result.success(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败！");
        }

    }

    /**
     * 图片下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(imgPath + name);
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应类型
            response.setContentType("image/jpeg");

            //持续读取写入数据
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {

        }
    }
}
