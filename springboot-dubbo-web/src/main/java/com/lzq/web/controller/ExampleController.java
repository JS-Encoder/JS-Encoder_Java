package com.lzq.web.controller;


import com.lzq.api.pojo.Example;
import com.lzq.api.pojo.Content;
import com.lzq.api.service.ContentService;
import com.lzq.api.service.ExampleService;
import com.lzq.web.utils.ExampleUtils;
import com.lzq.web.utils.JWTUtils;
import com.lzq.web.utils.QiniuyunUtils;
import com.lzq.web.utils.ResultMapUtils;
import com.qiniu.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/example")
@Api(value = "实例接口",description = "实例接口")
public class ExampleController {

    @Reference
    private ExampleService exampleService;


    @Reference
    private ContentService contentService;


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
     * 根据用户名查询全部实例
     * @return
     */
    @GetMapping("/getExample")
    @ApiOperation("查询个人全部实例")
    public Map<String,Object> getExample(HttpServletRequest request){
        //获取用户名
        String username = JWTUtils.verify(request.getHeader("token"))
                .getClaim("username").asString();
        List<Example> list = exampleService.queryByAccount(username);
        return ResultMapUtils.ResultMap(true,0,list);
    }

    /**
     * 获取其他用户的公开实例
     * @param username
     * @return
     */
    @GetMapping("/getPublicExample")
    @ApiOperation("获取其他用户的公开实例")
    public Map<String,Object> getPublicExample(String username){
        List<Example> list = exampleService.queryByPublic(username);
        return ResultMapUtils.ResultMap(true,0,list);
    }


}
