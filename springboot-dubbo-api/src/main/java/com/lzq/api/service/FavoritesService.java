package com.lzq.api.service;

import com.lzq.api.pojo.Favorites;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/29 10:55
 */
public interface FavoritesService {

    /**
     * 添加喜爱
     * @param favorites
     * @return
     */
    Boolean addFavorites(Favorites favorites);

    /**
     * 取消喜爱
     * @param favorites
     * @return
     */
    Boolean cancelFavorites(Favorites favorites);

    /**
     * 删除喜爱（物理）
     * @param exampleId
     */
    void deleteFavorites(String exampleId);

    /**
     * 获取用户的喜爱总数
     * @param username
     * @return
     */
    Integer getCount(String username);
}
