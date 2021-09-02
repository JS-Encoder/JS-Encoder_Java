package com.lzq.dubboservice.config;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.ByteArrayInputStream;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/26 10:53
 */
public class QiniuyunUtils {

    private static final String accessKey = "fIf9nYz-wQo3HD1AlhQ5wrUrdjtygUPGe2dpuLlY";
    public static final String secretKey = "ItVUX8sAaxsuj7kgmk3IZ0ip3BHljAx61N8m922C";
    public static final String bucket = "lzqwxr";

    /**
     * 上传图片到七牛云
     * @param bytes
     * @param imgname 图片名称
     * @return
     */
    public static String uploadFiles(byte[] bytes,String imgname) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = imgname;
        // byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        Auth auth = Auth.create(accessKey, secretKey);

        String upToken = auth.uploadToken(bucket);
        DefaultPutRet putRet = null;
        try {
            Response response = uploadManager.put(byteInputStream, key, upToken, null, null);
            // new BucketManager(auth,cfg).prefetch(bucket, key);
            //解析上传成功的结果
            putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
        return putRet.key;
    }

    /**
     * 删除七牛云图片
     * @param imgname
     */
    public static void deleteFiles(String imgname){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        String key = imgname;
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}
