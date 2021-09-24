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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/example",headers ="token")
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

    @Autowired
    ExampleUtils exampleUtils;

    @Value("${resources.route}")
    public String fileLocation;


    /**
     * 创建一个实例
     *
     * @param example 实例对象
     * @return
     */
    @PostMapping(value = "/createExample")
    @ApiOperation("创建一个实例")
    public Map<String, Object> CreateFile(HttpServletRequest request,Example example, Content exampleContent, String content) {
        log.info(example.toString());
        String token = request.getHeader("token");
        String username = JWTUtils.verify(token).getClaim("username").asString();
        example.setUsername(username);
        log.info("开始"+new Date(System.currentTimeMillis()).toString());
        //生成22位uuid
        // String uuid = ExampleUtils.getUUid();
        Boolean bol =false;
        //随机生成uuid
        //判断用户是保存实例还是第一次创建实例
        if (example.getExampleId()!=null){
            try {
                //通过id查询实例信息
                Example query = exampleService.queryById(example.getExampleId());
                //获取实例的文件名和图片key
                example.setImg(query.getImg());
                //修改实例内容
                bol = contentService.updateContent(exampleContent);
                String file = ExampleUtils.FILE_LOCATION + example.getUsername() + "/" + query.getFileName() + ".html";
                //用户保存实例
                exampleUtils.SaveExampleContent(example,content, exampleService,file);
                return ResultMapUtils.ResultMap(bol,0,"保存实例内容成功");
            } catch (IOException e) {
                e.printStackTrace();
                return ResultMapUtils.ResultMap(false,0,"修改文件失败");
            }
        }else {
            //用户第一次创建实例
            //获取当前时间毫秒
            long time = System.currentTimeMillis();
            //创建编译后的html文件
            String file = ExampleUtils.FILE_LOCATION + example.getUsername() + "/" + time + ".html";
            //初始化模板
            // File initfile = new File(ExampleUtils.INIT_HTML);
            File existfile = new File(file);
            try {
                //创建文件
                if (!existfile.getParentFile().exists()) {
                    existfile.getParentFile().mkdirs();
                }
                existfile.createNewFile();
                //把初始化模板拷贝到新建的html中
                // FileUtils.copyFile(initfile, existfile);
                //插入编译后的文件名称time
                example.setFileName(Long.toString(time));
                //把文件信息插入到数据库中
                example = exampleService.insert(example);
                System.out.println(example);
                if (example!=null){
                    //更新用户的个人作品数
                    accountService.addWorks(example.getUsername());
                    //保存实例内容
                    exampleContent.setExampleId(example.getExampleId());
                    //第一次保存时在表中添加实例内容
                    bol = contentService.addContent(exampleContent);
                    //用户保存实例
                    exampleUtils.SaveExampleContent(example,content, exampleService,file);
                }
                log.info(example.getExampleId());
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
        if (StringUtils.isNotBlank(favorites.getUsername())
                && StringUtils.isNotBlank(favorites.getExampleId())){
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
     * 取消喜爱
     * @param favorites
     * @return
     */
    @PostMapping("/cancelFavorites")
    @ApiOperation("取消喜爱")
    public Map<String,Object> cancelFavorites(Favorites favorites){
        log.info("取消喜爱"+favorites.toString());
        if (StringUtils.isNotBlank(favorites.getUsername())
                && StringUtils.isNotBlank(favorites.getExampleId())){
            //取消喜爱
            Boolean bol = favoritesService.cancelFavorites(favorites);
            log.info("取消喜爱-删除喜爱表中的数据----"+bol.toString());
            //清除缓存中的喜爱
            if (bol){
                redisTemplate.opsForList().remove(favorites.getUsername()+"fav", 0, favorites.getExampleId());
            }
            //更新用户喜爱数量
            Boolean aBoolean = accountService.reduceFavorites(favorites.getUsername());

            log.info("取消喜爱-更新用户喜爱数量----"+aBoolean.toString());
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
    public Map<String,Object> deleteExample(HttpServletRequest request, Example example){
        //获取token中的用户名
        String username = JWTUtils.verify(request.getHeader("token"))
                .getClaim("username").asString();
        //存入用户名进行查询
        example.setUsername(username);
        //查询是否存在该实例
        Example query = exampleService.queryByIdUsername(example);
        if (query!=null){
            //逻辑删除实例放入回收站中
            boolean b = exampleService.deleteById(example.getExampleId());
            if (b){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                log.info("消息接收时间:"+sdf.format(new Date())+"-------"+example.getExampleId());
                //发送消息到队列中
                rabbitTemplate.convertAndSend("delete_exchange", "delete", example, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //设置延迟时间 7天
                        message.getMessageProperties().setHeader("x-delay",1000*60*60*24*7);
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
        }else {
            return ResultMapUtils.ResultMap(false,1,"删除失败");
        }

    }

    /**
     * 恢复实例
     * @return
     */
    @PostMapping("/resume")
    public Map<String,Object> resume(Example example){
        log.info("恢复实例:-------"+example.getExampleId());
        example.setDeleted(0);
        Boolean update = exampleService.resumeExample(example.getExampleId());
        if (update){
            //减少回收站数量（恢复实例）
            accountService.reduceRecycle(example.getUsername());
            //增加作品数
            accountService.addWorks(example.getUsername());
        }
        return ResultMapUtils.ResultMap(update,0,null);
    }

    /**
     * 立即删除回收站
     * @param example
     * @return
     */
    @DeleteMapping("/delete")
    @ApiOperation("立即删除回收站")
    public Map<String,Object> deleteRightNow(Example example){
        //查询该用户的回收站是否有该梳理
        Example query = exampleService.getExampleByDeleted(example);
        log.info(query.toString());
        Boolean bol = null;
        if (query!=null) {
            //立即删除实例
            bol = exampleService.deleteExample(example.getExampleId());
            String filelocation=fileLocation + query.getUsername() + "/" + query.getFileName()+".html";
            log.info("删除的html地址"+filelocation);
            if (bol){
                File file = new File(fileLocation + query.getUsername() + "/" + query.getFileName()+".html");
                //删除文件
                boolean delete = file.delete();
                System.out.println(delete);
                //删除实例内容
                contentService.deleteContent(query.getExampleId());
                //删除实例图片（七牛云）
                QiniuyunUtils.deleteFiles(query.getImg());
                //删除所有用户的对该作品的喜爱
                favoritesService.deleteFavorites(query.getExampleId());
                //减少回收站数量（删除实例）
                accountService.reduceRecycle(query.getUsername());
            }
        }
        return ResultMapUtils.ResultMap(bol,0,null);
    }

    /**
     * 更新实例
     * @param example
     * @return
     */
    @ApiOperation("更新实例")
    @PutMapping("/")
    public Map<String,Object> updateExample(HttpServletRequest request,Example example){
        //获取token中的用户名
        String username = JWTUtils.verify(request.getHeader("token"))
                .getClaim("username").asString();
        //存入用户名进行查询
        example.setUsername(username);
        Example query = exampleService.queryByIdUsername(example);
        Boolean update=false;
        if (query!=null){
            update = exampleService.update(example);
        }
        return ResultMapUtils.ResultMap(update,0,null);
    }

}
