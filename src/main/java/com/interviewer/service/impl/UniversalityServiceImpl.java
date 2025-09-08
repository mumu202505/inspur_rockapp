package com.interviewer.service.impl;

import com.interviewer.core.constant.TokenConstant;
import com.interviewer.core.exception.ServiceException;
import com.interviewer.dto.DistrictNode;
import com.interviewer.dto.SysDistrictGd;
import com.interviewer.enums.result.SysResultEnum;
import com.interviewer.mapper.UniversalityMapper;
import com.interviewer.service.RedisService;
import com.interviewer.service.UniversalityService;
import com.interviewer.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;

@Service
@Slf4j
@Transactional
public class UniversalityServiceImpl implements UniversalityService {
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Autowired
    UniversalityMapper universalityMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public List<DistrictNode> getbuildTree() {
        List<DistrictNode> rootNodes = new ArrayList<>();
        // 从Redis缓存中获取数据
        rootNodes = (List<DistrictNode>) redisService.get("districtTree");
        if (rootNodes == null) {
            List<SysDistrictGd> sysDistrictGds = universalityMapper.getbuildTree();
            // 将所有节点存入Map，便于查找
            Map<String, DistrictNode> nodeMap = new HashMap<>();

            // 先创建所有节点
            for (SysDistrictGd district : sysDistrictGds) {
                DistrictNode node = new DistrictNode();
                node.setId(Long.parseLong(district.getAdCode()));
                node.setName(district.getName());
                node.setLevel(district.getLevel());
                node.setParentId(getParentId(district));
                node.setChildren(new ArrayList<>());
                nodeMap.put(district.getAdCode(), node);
            }

            // 构建树形结构
            rootNodes = new ArrayList<>();
            for (DistrictNode node : nodeMap.values()) {
                if (node.getParentId() == 0) {
                    rootNodes.add(node);
                } else {
                    // 查找父节点并添加当前节点为其子节点
                    DistrictNode parent = findParent(nodeMap, node.getParentId());
                    if (parent != null) {
                        parent.getChildren().add(node);
                    }
                }
            }
            // 对根节点进行排序
            rootNodes = rootNodes.stream()
                    .sorted(Comparator.comparing(DistrictNode::getId))
                    .collect(Collectors.toList());
            // 将结果存入Redis缓存
            redisService.set("districtTree", rootNodes, 60 * 60 * 24 * 365);
        }
        return rootNodes;
    }


    private static long getParentId(SysDistrictGd district) {
        // 根据业务逻辑确定如何获取parentId
        // 这里假设parent_code对应的是父级的ad_code
        if (district.getParentCode() == null || "100000".equals(district.getParentCode())) {
            return 0;
        }
        // 根据parent_code查找对应的id
        return Long.parseLong(district.getParentCode());
    }

    private static DistrictNode findParent(Map<String, DistrictNode> nodeMap, long parentId) {
        // 根据parentId查找父节点
        // 这里需要根据你的实际业务逻辑实现
        return nodeMap.values().stream()
                .filter(node -> node.getId() == parentId)
                .findFirst()
                .orElse(null);
    }
}
