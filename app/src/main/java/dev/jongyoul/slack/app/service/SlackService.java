package dev.jongyoul.slack.app.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.context.builtin.SlashCommandContext;

import dev.jongyoul.slack.app.model.ErrorAnalysisOpenAiRequest;
import dev.jongyoul.slack.app.model.GeneralQuestionOpenAiRequest;
import dev.jongyoul.slack.app.model.GenerateErrorOpenAiRequest;
import dev.jongyoul.slack.app.model.OpenAiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SlackService {
    private static final String EXCLAMATION_REACTION_KEY = "exclamation";
    private static final String WHITE_CHECK_MARK_REACTION_KEY = "white_check_mark";
    private static final String THINKING_FACE_REACTION_KEY = "thinking_face";

    private final ReactionService reactionService;
    private final OpenAiService openAiService;

    public void generateError(SlashCommandContext context, String channel) {
        openAiService.call(
                new GenerateErrorOpenAiRequest(),
                content -> context.asyncClient().chatPostMessage(req -> req
                        .channel(channel)
                        .text(content)
                        .username("ERROR")
                        .iconEmoji("fire")),
                exception -> {
                    log.warn("Generation failed", exception);
                    final StringWriter sw = new StringWriter();
                    exception.printStackTrace(new PrintWriter(sw));
                    context.asyncClient().chatPostMessage(req -> req
                            .channel(channel)
                            .text(sw.toString())
                            .username("GEN-ERROR Failed")
                            .iconEmoji("exclamation"));

                }
        );
    }

    public void ask(String question, EventContext context, String channel, String timestamp) {
        call(new GeneralQuestionOpenAiRequest(question), context, channel, timestamp);
    }

    public void analyze(String errorContent, EventContext context, String channel, String timestamp) {
        call(new ErrorAnalysisOpenAiRequest(errorContent), context, channel, timestamp);
    }

    private void call(OpenAiRequest openAiRequest, EventContext context, String channel, String timestamp) {
        reactionService.getReactions(context, channel, timestamp).thenAccept(reactions -> {
            if (reactions.contains(EXCLAMATION_REACTION_KEY)
                || reactions.contains(WHITE_CHECK_MARK_REACTION_KEY)
                || reactions.contains(THINKING_FACE_REACTION_KEY)) {
                log.info("Already handling. channel: {}, timestamp: {}", channel, timestamp);
                return;
            }
            reactionService.addReaction(context, channel, timestamp, THINKING_FACE_REACTION_KEY);
            openAiService.call(
                    openAiRequest,
                    content -> {
                        reactionService.removeReaction(context, channel, timestamp, THINKING_FACE_REACTION_KEY);
                        if (content != null) {
                            context.asyncClient().chatPostMessage(req -> req
                                    .channel(channel)
                                    .threadTs(timestamp)
                                    .text(content));
                            reactionService.addReaction(context, channel, timestamp,
                                                        WHITE_CHECK_MARK_REACTION_KEY);
                        } else {
                            reactionService.addReaction(context, channel, timestamp, EXCLAMATION_REACTION_KEY);
                        }
                    },
                    exception -> {
                        log.error("OpenAI Error", exception);
                        reactionService.removeReaction(context, channel, timestamp,
                                                       THINKING_FACE_REACTION_KEY);
                        reactionService.addReaction(context, channel, timestamp, EXCLAMATION_REACTION_KEY);
                    });
        });
    }
}
