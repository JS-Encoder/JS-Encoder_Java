package com.lzq.web.utils;


import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class ExampleUtils {

    public static String InitHtml;

    public static String filelocation;

    public static String chormeDriver;

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


    //
}
