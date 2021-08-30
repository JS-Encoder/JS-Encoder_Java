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
    public Boolean addFavorites(Favorites favorites);
}
