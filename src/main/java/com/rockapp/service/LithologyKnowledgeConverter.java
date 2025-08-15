package com.rockapp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rockapp.entity.BaseSyncLithologyKnowledgeEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class LithologyKnowledgeConverter {

    /**
     * 将岩性知识库对象转换为指定格式的JSON数组
     *
     * @param knowledge 岩性知识库对象
     * @return JSON数组格式的数据
     */
    public JSONArray convertToStructuredFormat(BaseSyncLithologyKnowledgeEntity knowledge) {
        JSONArray result = new JSONArray();

        if (knowledge == null) {
            return result;
        }

        // 添加各个字段到结果中
        String[] split = knowledge.getFImagePaths().split(",");
        String url = Arrays.stream(split)
                .skip(1) // 跳过第一个元素
                .collect(Collectors.joining(","));
        addFieldIfNotEmpty(result, "元素矿物组成", knowledge.getFElementalMinera(), null);
        addFieldIfNotEmpty(result, "结构构造", knowledge.getFStructuralConst(), ObjectUtils.isNotEmpty(split[0]) ? split[0] : null);
        addFieldIfNotEmpty(result, "物理性质", knowledge.getFPhysicalPropert(), null);
        addFieldIfNotEmpty(result, "成因分析", knowledge.getFFactorialAnalys(), null);
        addFieldIfNotEmpty(result, "常见用途", knowledge.getFCommonUse(), null);
        addFieldIfNotEmpty(result, "易混淆岩性", knowledge.getFConfoundingLith(), url);
        addFieldIfNotEmpty(result, "特征", knowledge.getFFeature(), null);
        addFieldIfNotEmpty(result, "成份", knowledge.getFComponent(), null);
        return result;
    }

    /**
     * 如果字段内容不为空，则添加到结果中
     */
    private void addFieldIfNotEmpty(JSONArray result, String title, String content, String imageUrl) {
        if (content != null && !content.trim().isEmpty()) {
            JSONObject obj = new JSONObject();
            obj.put("title", title);
            obj.put("content", content);
            obj.put("img", imageUrl != null ? imageUrl : "");
            result.add(obj);
        }
    }

    /**
     * 从富文本内容中提取第一个图片URL
     */
    private String extractFirstImage(String richText) {
        if (richText == null || richText.isEmpty()) {
            return null;
        }

        // 使用正则表达式匹配img标签的src属性
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = pattern.matcher(richText);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}