package com.rockapp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RockData {
    private String rockName;
    private String englishName;
    private Map<String, String> sections = new HashMap<>();
    private String imagePath; // 改为单个图片路径
    private String feature;
    private String component;

    // Getter和Setter方法
    public String getRockName() { return rockName; }
    public void setRockName(String rockName) { this.rockName = rockName; }

    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }

    public void setSectionContent(String section, String content) {
        sections.put(section, content);
    }

    public String getSectionContent(String section) {
        return sections.getOrDefault(section, "");
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
}
