package dev.jongyoul.slack.app.handler;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppMentionEvent;

import dev.jongyoul.slack.app.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AppMentionEventHandler implements BoltEventHandler<AppMentionEvent> {
    private final SlackService slackService;

    @Override
    public Response apply(EventsApiPayload<AppMentionEvent> event, EventContext context) {
        final AppMentionEvent appMentionEvent = event.getEvent();
        final String question = appMentionEvent.getText();
        final String channel = appMentionEvent.getChannel();
        final String timestamp = appMentionEvent.getTs();
        slackService.ask(question, context, channel, timestamp);
        return context.ack();
    }
}
