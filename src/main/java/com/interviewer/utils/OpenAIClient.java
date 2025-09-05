package com.interviewer.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class OpenAIClient {
    private static final String API_URL = "http://ai.inspur.seasite.top/v1";
    private final OkHttpClient client;
    private final Gson gson = new Gson();
    private ResponseBody responseBody;

    public interface StreamCallback {
        void onChunkReceived(String content);
        void onComplete();
        void onError(String error);
    }

    public OpenAIClient(String apiKey) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + apiKey)
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    public void streamRequest(String prompt, StreamCallback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // 构建请求体
        String jsonBody = "{"
                + "\"model\": \"deepseek-r1-distill-qwen\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}],"
                + "\"stream\": true"
                + "}";

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("服务器返回错误: " + response.code());
                    return;
                }

                try (ResponseBody body = response.body();
                     BufferedReader reader = new BufferedReader(
                             new InputStreamReader(body.byteStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String json = line.substring(6).trim();
                            if (!json.equals("[DONE]")) {
                                try {
                                    // 解析JSON获取内容
                                    JsonObject data = gson.fromJson(json, JsonObject.class);
                                    JsonArray choices = data.getAsJsonArray("choices");
                                    if (choices != null && choices.size() > 0) {
                                        JsonObject choice = choices.get(0).getAsJsonObject();
                                        JsonObject delta = choice.getAsJsonObject("delta");
                                        if (delta != null && delta.has("content")) {
                                            String content = delta.get("content").getAsString();
                                            callback.onChunkReceived(content);
                                        }
                                    }
                                } catch (Exception e) {
                                    callback.onError("解析响应失败: " + e.getMessage());
                                }
                            }
                        }
                    }
                    callback.onComplete();
                } catch (IOException e) {
                    callback.onError("读取响应失败: " + e.getMessage());
                }
            }
        });
    }


        public static void main(String[] args) {
            OpenAIClient client = new OpenAIClient("sk-Bo4ohdoosiec5");

            client.streamRequest("你好，你是谁？", new OpenAIClient.StreamCallback() {
                @Override
                public void onChunkReceived(String content) {
                    System.out.print(content); // 实时输出接收到的内容
                }

                @Override
                public void onComplete() {
                    System.out.println("\n\n对话完成");
                }

                @Override
                public void onError(String error) {
                    System.err.println("发生错误: " + error);
                }
            });

            // 保持程序运行，等待响应
            try {
                Thread.sleep(10000); // 等待10秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



}
