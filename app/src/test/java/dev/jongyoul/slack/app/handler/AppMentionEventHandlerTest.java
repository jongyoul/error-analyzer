package dev.jongyoul.slack.app.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppMentionEvent;

import dev.jongyoul.slack.app.service.SlackService;

@ExtendWith(MockitoExtension.class)
class AppMentionEventHandlerTest {
    @Mock
    private EventsApiPayload<AppMentionEvent> event;

    @Mock
    private AppMentionEvent appMentionEvent;

    @Mock
    private EventContext context;

    @Mock
    private SlackService slackService;

    @Test
    void apply() {
        final String testText = "testText";
        final String testChannel = "testChannel";
        final String testTs = "testTs";
        final Response response = Response.ok();
        when(event.getEvent()).thenReturn(appMentionEvent);
        when(appMentionEvent.getText()).thenReturn(testText);
        when(appMentionEvent.getChannel()).thenReturn(testChannel);
        when(appMentionEvent.getTs()).thenReturn(testTs);
        when(context.ack()).thenReturn(response);

        AppMentionEventHandler target = new AppMentionEventHandler(slackService);
        Response returnValue = target.apply(event, context);

        verify(slackService).ask(testText, context, testChannel, testTs);
        assertEquals(response, returnValue);
    }
}