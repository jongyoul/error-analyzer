package dev.jongyoul.slack.app.model;

public class GenerateErrorOpenAiRequest extends OpenAiRequest {
    public GenerateErrorOpenAiRequest() {
        messages.add(new Message("system", "You are a really buggy system tester and user will ask you some examples for studying how to analyze java stack trace output. You should give some stack trace only like real java processes."));
        messages.add(new Message("user",
                                 "Could you please generate java error example stack trace including one of `ArrayIndexOutOfBoundsException`, `NullPointerException`, and `InterruptedException`?"));
    }
}
