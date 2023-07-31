package com.atguigu.ssyx.acl.utils;

import com.atguigu.ssyx.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    public static List<Permission> buildPermissions(List<Permission> allPermissionList) {
        List<Permission> trees = new ArrayList<Permission>();
        for (Permission treeNode : allPermissionList) {
            if (treeNode.getPid() == 0) {
                treeNode.setLevel(1);
                trees.add(findChildren(treeNode, allPermissionList));
            }
        }
        return trees;
    }

    private static Permission findChildren(Permission treeNode, List<Permission> allPermissionList) {
        treeNode.setChildren(new ArrayList<>());
        for (Permission it : allPermissionList) {
            if (treeNode.getId().longValue() == it.getPid().longValue()) {
                int level = treeNode.getLevel() + 1;
                it.setLevel(level);
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<Permission>());
                }
                treeNode.getChildren().add(findChildren(it, allPermissionList));
            }
        }
        return treeNode;
    }
}
