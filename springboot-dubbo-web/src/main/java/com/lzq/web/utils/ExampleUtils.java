package com.lzq.web.utils;


import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ExampleUtils {

    public static final String InitHtml="F:/项目/MyDemo/data/HelloWord.html";

    public static final String filelocation="F:/项目/MyDemo/data/";


    /**
     * 截图
     * @param username 用户名
     * @param filename 文件名
     * @param imgname
     * @return
     * @throws IOException
     */
    public static String screenshot(String username,String filename,String imgname) throws IOException {
        //使用截屏工具进行截屏
        //启用chrome驱动
        //chrome驱动的位置
        System.setProperty("webdriver.chrome.driver", "F:\\ChormeDriver\\chromedriver.exe");
        System. setProperty("java.awt.headless", "true");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.setHeadless(true);
        ChromeDriver broswer = new ChromeDriver(options);
        //设置浏览器窗口大小
        int winHeight = 800 ;
        int winWidth = 1280 ;
        Dimension dim = new Dimension(winWidth, winHeight);
        broswer.manage().window().setSize(dim);
        //等待1秒,
        broswer.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //打开url
        broswer.get("http://localhost:8090/"+username+"/"+filename+".html");

        byte[] bytes = ((TakesScreenshot) broswer).getScreenshotAs(OutputType.BYTES);
        //返回图片名
        String fileName = QiniuyunUtils.uploadFiles(bytes,imgname);
        broswer.close();
        return fileName;

    }


    //
}
