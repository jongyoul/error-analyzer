package dev.jongyoul.slack.app.model;

public class GeneralQuestionOpenAiRequest extends OpenAiRequest {
    public GeneralQuestionOpenAiRequest(String question) {
        messages.add(new Message("system", "Anyone can have any question to you for all kind of topics"));
        messages.add(new Message("user",
                                 "Could you please answer my question in various aspect? I would like to know the answer at least two different categories. Here is the questioin: ```"
                                 + question + "```"));
    }
}
