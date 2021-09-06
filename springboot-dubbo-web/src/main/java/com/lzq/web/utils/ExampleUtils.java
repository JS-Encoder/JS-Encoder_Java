package com.lzq.web.utils;


import com.lzq.api.pojo.Content;
import com.lzq.api.pojo.Example;
import com.lzq.api.service.ContentService;
import com.lzq.api.service.ExampleService;
import com.qiniu.util.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ExampleUtils {

    public static String InitHtml;

    public static String filelocation;

    public static String chormeDriver;

    public static String bucket;

    public static String url;

    @Value("${resources.InitHtml}")
    public void setInitHtml(String initHtml) {
        InitHtml = initHtml;
    }

    @Value("${resources.route}")
    public void setFilelocation(String filelocation) {
        ExampleUtils.filelocation = filelocation;
    }

    @Value("${chorme.value}")
    public void setChormeDriver(String chormeDriver) {
        ExampleUtils.chormeDriver = chormeDriver;
    }

    @Value("${qiniuyun.bucket}")
    public void setBucket(String bucket) {
        ExampleUtils.bucket = bucket;
    }

    @Value("${qiniuyun.url}")
    public void setUrl(String url) {
        ExampleUtils.url = url;
    }

    /**
     * 截图
     *
     * @param username 用户名
     * @param filename 文件名
     * @param imgname
     * @return
     * @throws IOException
     */
    public static String screenshot(String username, String filename, String imgname) throws IOException {
        //使用截屏工具进行截屏
        //启用chrome驱动
        //chrome驱动的位置
        System.setProperty("webdriver.chrome.driver", chormeDriver);
        System.setProperty("java.awt.headless", "true");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.setHeadless(true);
        ChromeDriver broswer = new ChromeDriver(options);
        //设置浏览器窗口大小
        int winHeight = 800;
        int winWidth = 1280;
        Dimension dim = new Dimension(winWidth, winHeight);
        broswer.manage().window().setSize(dim);
        //等待1秒,
        broswer.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //打开url
        broswer.get("http://localhost:8090/" + username + "/" + filename + ".html");

        byte[] bytes = ((TakesScreenshot) broswer).getScreenshotAs(OutputType.BYTES);
        //返回图片名
        String fileName = QiniuyunUtils.uploadFiles(bytes, imgname);
        broswer.close();
        return fileName;

    }

    public static Boolean SaveExampleContent(Example example, Content exampleContent, String content, ExampleService exampleService, ContentService contentService) throws IOException {
        //获取html文件路劲
        //实例内容和实例进行绑定
        exampleContent.setExampleId(example.getExampleId());
        String file = filelocation + example.getUsername() + "/" + example.getFileName() + ".html";
        System.out.println(file);
        FileOutputStream fos = new FileOutputStream(new File(file));
        String screenshot = null;
        Boolean bol = false;
        try {
            //包编译后的html内容覆盖原来的内容
            fos.write(content.getBytes("GBK"));
            //第一次保存时生成图片
            if (StringUtils.isNullOrEmpty(example.getImg())) {
                //把当前时间戳设置为图片名称
                example.setImg(Long.toString(System.currentTimeMillis()));
                //截图后进行保存
                screenshot = ExampleUtils.screenshot(example.getUsername(), example.getFileName(), example.getImg());
            } else {
                //先删除图片后上传新图片
                QiniuyunUtils.deleteFiles(example.getImg());
                example.setImg(Long.toString(System.currentTimeMillis()));
                //截图后进行保存
                screenshot = ExampleUtils.screenshot(example.getUsername(), example.getFileName(), example.getImg());
            }
            //修改图片地址
            example.setImg(url + screenshot);
            //更新实例
            bol = exampleService.update(example);
            //修改实例内容 当表无该数据时插入数据
            bol = contentService.updateContent(exampleContent);
            if (!bol) {
                //第一次保存时在表中添加实例内容
                bol = contentService.addContent(exampleContent);
            }
            return bol;
        } catch (IOException e) {
            e.printStackTrace();
            return bol;
        } finally {
            fos.close();
        }
    }

    //
}
