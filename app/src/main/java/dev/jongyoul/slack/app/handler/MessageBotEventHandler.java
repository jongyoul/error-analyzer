package dev.jongyoul.slack.app.handler;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.MessageBotEvent;

import dev.jongyoul.slack.app.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MessageBotEventHandler implements BoltEventHandler<MessageBotEvent> {
    private final SlackService slackService;

    @Override
    public Response apply(EventsApiPayload<MessageBotEvent> event, EventContext context) {
        final MessageBotEvent messageEvent = event.getEvent();
        final String channel = messageEvent.getChannel();
        final String timestamp = messageEvent.getTs();
        final String errorContent = messageEvent.getText();
        slackService.analyze(errorContent, context, channel, timestamp);
        return context.ack();
    }
}
