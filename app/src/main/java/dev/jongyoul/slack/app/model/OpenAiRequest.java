package dev.jongyoul.slack.app.model;

import java.util.ArrayList;
import java.util.List;

public abstract class OpenAiRequest {
    private final String model = "gpt-3.5-turbo";
    private final int temperature = 1;
    private final int max_tokens = 256;
    private final int top_p = 1;
    private final int frequency_penalty = 0;
    private final int presence_penalty = 0;
    protected final List<Message> messages = new ArrayList<>();
}
