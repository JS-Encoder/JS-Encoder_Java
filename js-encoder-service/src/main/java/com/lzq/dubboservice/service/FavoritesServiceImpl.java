package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzq.api.pojo.Example;
import com.lzq.api.pojo.Favorites;
import com.lzq.api.service.FavoritesService;
import com.lzq.dubboservice.mapper.AccountMapper;
import com.lzq.dubboservice.mapper.ExampleMapper;
import com.lzq.dubboservice.mapper.FavoritesMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author ：LZQ
 * @description：FollowService实现类
 * @date ：2021/8/25 10:48
 */
@Component
@Service(interfaceClass = FavoritesService.class)
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

    @Resource
    private ExampleMapper exampleMapper;


    @Override
    public Boolean addFavorites(Favorites favorites) {
        Boolean insert = baseMapper.insert(favorites) > 0 ? true : false;
        if (insert) {
            //用来查询关注人
            QueryWrapper<Example> wrapper = new QueryWrapper<>();
            wrapper.eq("example_id", favorites.getExampleId());
            //获取喜爱的用例
            Example example = exampleMapper.selectOne(wrapper);
            return exampleMapper.addFavorites(example) > 0 ? true : false;
        } else {
            return false;
        }
    }

    @Override
    public Boolean cancelFavorites(Favorites favorites) {
        //先删除
        QueryWrapper<Favorites> wrapper = new QueryWrapper<>();
        wrapper.eq("username", favorites.getUsername());
        wrapper.eq("example_id", favorites.getExampleId());
        Boolean delete = baseMapper.delete(wrapper) > 0 ? true : false;
        if (delete) {
            //用来查询关注人
            QueryWrapper<Example> exampleQueryWrapper = new QueryWrapper<>();
            exampleQueryWrapper.eq("example_id", favorites.getExampleId());
            //获取喜爱的用例
            Example example = exampleMapper.selectOne(exampleQueryWrapper);
            //更新喜爱人数
            return exampleMapper.reduceFavorites(example) > 0 ? true : false;
        } else {
            return false;
        }
    }

    @Override
    public void deleteFavorites(String exampleId) {
        baseMapper.deleteFavorites(exampleId);
    }

    @Override
    public Integer getCount(String username) {
        QueryWrapper<Favorites> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectCount(wrapper);
    }
}
