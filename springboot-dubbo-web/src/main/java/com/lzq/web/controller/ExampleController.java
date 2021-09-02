package com.lzq.web.controller;


import com.lzq.api.pojo.Example;
import com.lzq.api.pojo.Content;
import com.lzq.api.pojo.Favorites;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.ContentService;
import com.lzq.api.service.ExampleService;
import com.lzq.api.service.FavoritesService;
import com.lzq.web.utils.ExampleUtils;
import com.lzq.web.utils.JWTUtils;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 创建一个实例
     *
     * @param example 实例对象
     * @return
     */
    @PostMapping("/createExample")
    @ApiOperation("创建一个实例")
    public Map<String, Object> CreateFile(Example example) {
        Map<String, Object> map = new HashMap<>();
        //获取当前时间毫秒
        long time = System.currentTimeMillis();
        //创建编译后的html文件
        String file = ExampleUtils.filelocation + example.getUsername() + "/" + time + ".html";
        //初始化模板
        File initfile = new File(ExampleUtils.InitHtml);
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
            Boolean bol = exampleService.insert(example);
            return ResultMapUtils.ResultMap(bol, 0, example);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建文件失败");
            return ResultMapUtils.ResultMap(false, 1, example);
        }
    }

    /**
     * 保存实例内容和编译后的html代码
     *
     * @param example
     * @param exampleContent
     * @param content
     * @return
     * @throws FileNotFoundException
     */
    @PostMapping("/saveExample")
    @ApiOperation("保存实例")
    public Map<String, Object> SaveExample(Example example, Content exampleContent, String content) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        //获取html文件路劲
        String file = ExampleUtils.filelocation + example.getUsername() + "/" + example.getFileName() + ".html";
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
            example.setImg("firstbird.asia/" + screenshot);
            //更新实例
            bol = exampleService.update(example);
            //修改实例内容 当表无该数据时插入数据
            bol = contentService.updateContent(exampleContent);
            if (!bol){
                //第一次保存时在表中添加实例内容
                bol = contentService.addContent(exampleContent);
            }
            return ResultMapUtils.ResultMap(bol, 0, screenshot);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultMapUtils.ResultMap(false, 1, null);
        }finally {
            fos.close();
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
            Boolean aBoolean = accountService.updateFavorites(favorites.getUsername());
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
                    //设置延迟时间
                    message.getMessageProperties().setHeader("x-delay",1000*15);
                    return message;
                }
            });
            return ResultMapUtils.ResultMap(b,0,null);
        }else {
            return ResultMapUtils.ResultMap(b,0,null);
        }
    }
}
