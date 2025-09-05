package com.interviewer.utils;

import com.alibaba.fastjson.JSON;
import com.interviewer.dto.AI.Message;
import com.interviewer.dto.AI.RequestBody;
import com.interviewer.dto.AI.ResponseBody;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class OpenAiUtils {
    private static final String URL = "http://ai.inspur.seasite.top/v1";
    private static final String API_KEY = "sk-Bo4ohdoosiec5";

    public String getAiRespondBody(String content) throws Exception {
        System.out.println("输入内容：" + content);

        List<Message> list = new ArrayList<>();
        Message message = new Message();
        message.setRole("user");
        message.setContent(content);
        list.add(message);

        RequestBody requestBody = new RequestBody();
        requestBody.setModel("deepseek-r1-distill-qwen");
        requestBody.setTemperature(0.7);
        requestBody.setMessages(list);
        String data = JSON.toJSONString(requestBody);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + API_KEY);
            httpPost.setEntity(new StringEntity(data));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                ResponseBody body = JSON.parseObject(responseBody, ResponseBody.class);
                String outputContent = body.getChoices().get(0).getMessage().getContent();
                System.out.println(outputContent);
                return outputContent;
            }
        }
    }
}

