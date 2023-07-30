package com.atguigu.ssyx.acl.utils;

import com.atguigu.ssyx.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    public static List<Permission> buildPermission(List<Permission> allList) {
        List<Permission> trees = new ArrayList<>();
        for (Permission permission : allList) {
            if (permission.getPid() == 0) {
                permission.setLevel(1);
                trees.add(findChildren(permission, allList));
            }
        }
        return trees;
    }

    private static Permission findChildren(Permission permission, List<Permission> allList) {
        permission.setChildren(new ArrayList<Permission>());
        for (Permission it : allList) {
            if (it.getPid().longValue() == permission.getId().longValue()) {
                int level = permission.getLevel() + 1;
                it.setLevel(level);
                if (permission.getChildren() == null) {
                    permission.setChildren(new ArrayList<>());
                }
                permission.getChildren().add(findChildren(it, allList));
            }
        }
        return permission;
    }
}
