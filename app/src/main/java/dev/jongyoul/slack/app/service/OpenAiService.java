package dev.jongyoul.slack.app.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiredArgsConstructor
@Slf4j
public class OpenAiService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final String apiToken;
    private final OkHttpClient okHttpClient;
    private final Gson gson;

    public void call(Object requestBody,
                     Consumer<String> onSuccess,
                     Consumer<Exception> onFailure) {
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiToken)
                .post(RequestBody.create(gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8),
                                         MediaType.parse("application/json")))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    final JsonElement jsonElement =
                            JsonParser.parseReader(Objects.requireNonNull(response.body()).charStream());
                    final JsonObject jsonObject = jsonElement.getAsJsonObject();
                    final String content = jsonObject
                            .get("choices").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("message").getAsJsonObject()
                            .get("content").getAsString();
                    onSuccess.accept(content);
                } catch (Exception e) {
                    log.error("Error while parsing response body. call: {}", call, e);
                    onFailure.accept(e);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onFailure.accept(e);
            }
        });
    }

}
