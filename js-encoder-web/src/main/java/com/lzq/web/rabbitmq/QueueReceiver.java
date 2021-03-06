package com.lzq.web.rabbitmq;

import com.lzq.api.pojo.Example;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.ContentService;
import com.lzq.api.service.ExampleService;
import com.lzq.api.service.FavoritesService;
import com.lzq.web.utils.QiniuyunUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/9/2 10:40
 */
@Slf4j
@Component
@RabbitListener(queues = "delete_queue")
public class QueueReceiver {

    @Reference
    ExampleService exampleService;

    @Reference
    ContentService contentService;

    @Reference
    FavoritesService favoritesService;

    @Reference
    AccountService accountService;

    @Value("${resources.route}")
    public String fileLocation;

    @RabbitHandler
    public void process(Example example){
        log.info(example.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("消息接收时间:"+sdf.format(new Date()));
        //删除实例
        Boolean bol = exampleService.deleteExample(example.getExampleId());
        if (bol){
            File file = new File(fileLocation + example.getUsername() + "/" + example.getFileName()+".html");
            //删除文件
            file.delete();
            //删除实例内容
            contentService.deleteContent(example.getExampleId());
            //删除实例图片（七牛云）
            QiniuyunUtils.deleteFiles(example.getImg());
            //删除所有用户的对该作品的喜爱
            favoritesService.deleteFavorites(example.getExampleId());
            //减少回收站数量（删除实例）
            accountService.reduceRecycle(example.getUsername());
        }
    }

}
