package com.lzq.web.controller;


import com.lzq.api.pojo.Example;
import com.lzq.api.pojo.Content;
import com.lzq.api.pojo.Favorites;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.ContentService;
import com.lzq.api.service.ExampleService;
import com.lzq.api.service.FavoritesService;
import com.lzq.web.utils.ExampleUtils;
import com.lzq.web.utils.QiniuyunUtils;
import com.lzq.web.utils.ResultMapUtils;
import com.qiniu.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/example")
@Api(value = "实例接口",description = "实例接口")
public class ExampleController {

    @Reference
    private ExampleService exampleService;

    @Reference
    private ContentService contentService;

    @Reference
    private FavoritesService favoritesService;

    @Reference
    private AccountService accountService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${resources.route}")
    public String fileLocation;

    @Value("${qiniuyun.url}")
    public String url;

    /**
     * 创建一个实例
     *
     * @param example 实例对象
     * @return
     */
    @PostMapping("/createExample")
    @ApiOperation("创建一个实例")
    public Map<String, Object> CreateFile(Example example, Content exampleContent, String content) {
        log.info("开始"+System.currentTimeMillis());
        //生成22位uuid
        String uuid = ExampleUtils.getUUid();
        Boolean bol =false;
        //随机生成uuid
        //判断用户是保存实例还是第一次创建实例
        if (example.getExampleId()!=null){
            try {
                //通过id查询实例信息
                Example query = exampleService.queryById(example.getExampleId());
                //获取实例的文件名和图片key
                example.setFileName(query.getFileName());
                example.setImg(query.getImg());
                //用户保存实例
                bol = ExampleUtils.SaveExampleContent(example, exampleContent, content, exampleService, contentService);
                return ResultMapUtils.ResultMap(bol,0,"保存实例内容");
            } catch (IOException e) {
                e.printStackTrace();
                return ResultMapUtils.ResultMap(false,0,"修改文件失败");
            }
        }else {
            example.setExampleId(uuid);
            //用户第一次创建实例
            //获取当前时间毫秒
            long time = System.currentTimeMillis();
            //创建编译后的html文件
            String file = ExampleUtils.FILE_LOCATION + example.getUsername() + "/" + time + ".html";
            //初始化模板
            File initfile = new File(ExampleUtils.INIT_HTML);
            File existfile = new File(file);
            try {
                //创建文件
                if (!existfile.getParentFile().exists()) {
                    existfile.getParentFile().mkdirs();
                }
                existfile.createNewFile();
                //把初始化模板拷贝到新建的html中
                FileUtils.copyFile(initfile, existfile);
                //插入编译后的文件名称time
                example.setFileName(Long.toString(time));
                //把文件信息插入到数据库中
                bol = exampleService.insert(example);
                log.info("获取exampleId:"+example);
                if (bol){
                    bol = accountService.addWorks(example.getUsername());
                    example.setExampleId(example.getExampleId());
                    //保存实例内容
                    bol = ExampleUtils.SaveExampleContent(example, exampleContent, content, exampleService, contentService);
                }
                log.info(bol.toString());
                return ResultMapUtils.ResultMap(bol, 0, example.getExampleId());
            } catch (Exception e) {
                e.printStackTrace();
                return ResultMapUtils.ResultMap(false, 1, "创建文件失败");
            }
        }
    }

    /**
     * 添加喜爱
     * @param favorites
     * @return
     */
    @PostMapping("/addFavorites")
    @ApiOperation("添加喜爱")
    public Map<String,Object> addFavorites(Favorites favorites){
        //判断是否用户已登录，登录则进行数据插入，否则则不做任何操作
        if (!StringUtils.isNullOrEmpty(favorites.getUsername())){
            Boolean bol = favoritesService.addFavorites(favorites);
            //添加到缓存
            if (bol){
                redisTemplate.opsForList().leftPush(favorites.getUsername()+"fav",favorites.getExampleId());
            }
            //更新用户喜爱数量
            Boolean aBoolean = accountService.addFavorites(favorites.getUsername());
            log.info(aBoolean.toString());
            return ResultMapUtils.ResultMap(bol,0,null);
        }else {
            return ResultMapUtils.ResultMap(false,1,null);
        }
    }


    /**
     * 放入回收站
     * @param example
     * @return
     */
    @DeleteMapping("/")
    @ApiOperation("放入回收站")
    public Map<String,Object> deleteExample(Example example){
        //逻辑删除实例放入回收站中
        boolean b = exampleService.deleteById(example.getExampleId());
        if (b){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("消息接收时间:"+sdf.format(new Date()));
            //发送消息到队列中
            rabbitTemplate.convertAndSend("delete_exchange", "delete", example, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    //设置延迟时间 1天
                    message.getMessageProperties().setHeader("x-delay",1000*60*60*24*1);
                    return message;
                }
            });
            //减少用户作品数
            accountService.reduceWorks(example.getUsername());
            //回收站数量增加
            accountService.increaseRecycle(example.getUsername());
            return ResultMapUtils.ResultMap(b,0,null);
        }else {
            return ResultMapUtils.ResultMap(b,0,null);
        }
    }

    /**
     * 立即删除回收站
     * @param example
     * @return
     */
    @DeleteMapping("/example")
    @ApiOperation("立即删除回收站")
    public Map<String,Object> deleteRightNow(Example example){
        //立即删除实例
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
        return ResultMapUtils.ResultMap(bol,0,null);
    }


}
