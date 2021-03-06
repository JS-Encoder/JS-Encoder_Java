package com.lzq.api.service;

import com.lzq.api.pojo.Content;

/**
 * @author ：LZQ
 * @description：(example_content)表服务接口
 * @date ：2021/8/27 14:07
 */
public interface ContentService {

    /**
     * 添加实例内容
     *
     * @param content
     * @return
     */
    Boolean addContent(Content content);

    /**
     * 更新实例内容
     *
     * @param content
     * @return
     */
    Boolean updateContent(Content content);

    /**
     * 获取实例内容
     *
     * @param exampleId
     * @param username
     * @param ispublic
     * @return
     */
    Content getContent(String exampleId, String username, Integer ispublic);

    /**
     * 删除实例内容
     *
     * @param exampleId 实例id
     * @return
     */
    void deleteContent(String exampleId);
}
