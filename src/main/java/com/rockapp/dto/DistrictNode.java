package com.rockapp.dto;

import lombok.Data;

import java.util.List;

/**
 * 国家省市区节点
 */
@Data
public class DistrictNode {
    private long id;
    private String name;
    private String level;
    private long parentId;
    private List<DistrictNode> children;
}
