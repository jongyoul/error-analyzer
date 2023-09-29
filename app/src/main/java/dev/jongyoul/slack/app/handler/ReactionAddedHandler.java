package dev.jongyoul.slack.app.handler;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.ReactionAddedEvent;
import com.slack.api.model.event.ReactionAddedEvent.Item;

import dev.jongyoul.slack.app.service.ReactionService;
import dev.jongyoul.slack.app.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ReactionAddedHandler implements BoltEventHandler<ReactionAddedEvent> {
    private static final String REPEAT_REACTION_KEY = "repeat";
    private static final String WHITE_CHECK_MARK_REACTION_KEY = "exclamation";

    private final ReactionService reactionService;
    private final SlackService slackService;

    @Override
    public Response apply(EventsApiPayload<ReactionAddedEvent> event, EventContext context) {
        final String reactionName = event.getEvent().getReaction();
        switch (reactionName) {
            case REPEAT_REACTION_KEY -> {
                final Item item = event.getEvent().getItem();
                final String channel = item.getChannel();
                final String timestamp = item.getTs();
                context.asyncClient().conversationsHistory(req -> req
                               .channel(channel)
                               .latest(timestamp)
                               .inclusive(true)
                               .limit(1))
                       .thenAccept(conversationsHistoryResponse -> {
                           final String question = conversationsHistoryResponse.getMessages().get(0).getText();
                           slackService.ask(question, context, channel, timestamp);
                       }).exceptionallyAsync(throwable -> {
                           log.error("Error while getting the original text", throwable);
                           reactionService.addReaction(context, channel, timestamp, WHITE_CHECK_MARK_REACTION_KEY);
                           return null;
                       });
            }
            default -> log.warn("Not supported reaction: {}", reactionName);
        }
        return context.ack();
    }
}
