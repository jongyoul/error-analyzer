package dev.jongyoul.slack.app.model;

public class ErrorAnalysisOpenAiRequest extends OpenAiRequest {
    public ErrorAnalysisOpenAiRequest(String errorContent) {
        messages.add(new Message("system",
                                 "You are a senior developer and will be asked about analyzing error message"));
        messages.add(new Message("user",
                                 "Could you please tell me what the most possible reason for the follow error? ```"
                                 + errorContent + "```"));
    }
}
