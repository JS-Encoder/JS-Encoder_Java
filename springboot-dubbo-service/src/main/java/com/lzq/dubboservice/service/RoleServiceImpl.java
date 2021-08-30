package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzq.api.pojo.Role;
import com.lzq.api.service.RoleService;
import com.lzq.dubboservice.mapper.RoleMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

/**
 * @author ：LZQ
 * @description：RoleService实现类
 * @date ：2021/8/23 10:48
 */
@Component
@Service(interfaceClass = RoleService.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Role queryById(Integer id) {
        return baseMapper.selectById(id);
    }


}
