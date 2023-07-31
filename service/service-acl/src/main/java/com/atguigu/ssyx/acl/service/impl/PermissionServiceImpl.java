package com.atguigu.ssyx.acl.service.impl;

import com.atguigu.ssyx.acl.mapper.PermissionMapper;
import com.atguigu.ssyx.acl.service.PermissionService;
import com.atguigu.ssyx.acl.utils.PermissionHelper;
import com.atguigu.ssyx.model.acl.Permission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Override
    public List<Permission> queryAllPermission() {
        List<Permission> allPermissionList = baseMapper.selectList(null);
        List<Permission> Result = PermissionHelper.buildPermissions(allPermissionList);
        return Result;
    }

    @Override
    public void removeChildById(Long id) {
        List<Long> idList = new ArrayList<>();
        this.selectChildListById(id,idList);
        idList.add(id);
        baseMapper.deleteBatchIds(idList);
    }

    private void selectChildListById(Long id, List<Long> idList) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);
        List<Permission> list = baseMapper.selectList(null);
        list.stream().forEach(item -> {
            idList.add(item.getId());
            this.selectChildListById(item.getId(), idList);
        });
    }


}
