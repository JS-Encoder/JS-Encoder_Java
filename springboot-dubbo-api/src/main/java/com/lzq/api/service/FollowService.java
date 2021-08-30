package com.lzq.api.service;

import com.lzq.api.pojo.Follow;

/**
 * @author ：LZQ
 * @description：(Follow)表服务接口
 * @date ：2021/8/25 9:50
 */
public interface FollowService {

    /**
     * 添加关注
     * @param follow
     * @return
     */
    boolean addFollow(Follow follow);

    /**
     * 取消关注
     * @param follow
     * @return
     */
    boolean cancelFollow(Follow follow);
}
