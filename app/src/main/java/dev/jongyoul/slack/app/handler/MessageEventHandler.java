package dev.jongyoul.slack.app.handler;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.MessageEvent;

import dev.jongyoul.slack.app.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MessageEventHandler implements BoltEventHandler<MessageEvent> {
    private static final Pattern MENTION = Pattern.compile("@\\w+");
    private final SlackService slackService;

    @Override
    public Response apply(EventsApiPayload<MessageEvent> event, EventContext context) {
        final MessageEvent messageEvent = event.getEvent();
        final String channel = messageEvent.getChannel();
        final String timestamp = messageEvent.getTs();
        final String errorContent = messageEvent.getText();
        final String threadTs = messageEvent.getThreadTs();
        if (!MENTION.matcher(errorContent).find() && StringUtils.isEmpty(threadTs)) {
            slackService.analyze(errorContent, context, channel, timestamp);
        }
        return context.ack();
    }
}
