package dev.jongyoul.slack.app.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.MessageEvent;

import dev.jongyoul.slack.app.service.SlackService;

@ExtendWith(MockitoExtension.class)
class MessageEventHandlerTest {
    @Mock
    private EventsApiPayload<MessageEvent> event;

    @Mock
    private MessageEvent messageEvent;

    @Mock
    private EventContext context;

    @Mock
    private SlackService slackService;

    @Test
    void apply_notMentionedAndEmptyThreadTs() {
        final String testText = "normal message with not mentioning any acount";
        final String testChannel = "testChannel";
        final String testTimestamp = "testTs";
        final String testThreadTs = StringUtils.EMPTY;
        final Response response = Response.ok();
        when(event.getEvent()).thenReturn(messageEvent);
        when(messageEvent.getText()).thenReturn(testText);
        when(messageEvent.getChannel()).thenReturn(testChannel);
        when(messageEvent.getTs()).thenReturn(testTimestamp);
        when(messageEvent.getThreadTs()).thenReturn(testThreadTs);
        when(context.ack()).thenReturn(response);

        MessageEventHandler messageEventHandler = new MessageEventHandler(slackService);
        Response returnValue = messageEventHandler.apply(event, context);

        verify(slackService, only()).analyze(testText, context, testChannel, testTimestamp);
        assertEquals(response, returnValue);
    }

    @Test
    void apply_notMentionedAndNonEmptyThreadTs() {
        final String testText = "normal message with not mentioning any acount";
        final String testChannel = "testChannel";
        final String testTimestamp = "testTs";
        final String testThreadTs = "testThreadTs";
        final Response response = Response.ok();
        when(event.getEvent()).thenReturn(messageEvent);
        when(messageEvent.getText()).thenReturn(testText);
        when(messageEvent.getChannel()).thenReturn(testChannel);
        when(messageEvent.getTs()).thenReturn(testTimestamp);
        when(messageEvent.getThreadTs()).thenReturn(testThreadTs);
        when(context.ack()).thenReturn(response);

        MessageEventHandler messageEventHandler = new MessageEventHandler(slackService);
        Response returnValue = messageEventHandler.apply(event, context);

        verify(slackService, never()).analyze(testText, context, testChannel, testTimestamp);
        assertEquals(response, returnValue);
    }

    @ParameterizedTest
    @MethodSource("provideTestText")
    void apply_mentionedAndEmptyThreadTs(String testText) {
        final String testChannel = "testChannel";
        final String testTimestamp = "testTs";
        final String testThreadTs = StringUtils.EMPTY;
        final Response response = Response.ok();
        when(event.getEvent()).thenReturn(messageEvent);
        when(messageEvent.getText()).thenReturn(testText);
        when(messageEvent.getChannel()).thenReturn(testChannel);
        when(messageEvent.getTs()).thenReturn(testTimestamp);
        when(messageEvent.getThreadTs()).thenReturn(testThreadTs);
        when(context.ack()).thenReturn(response);

        MessageEventHandler messageEventHandler = new MessageEventHandler(slackService);
        Response returnValue = messageEventHandler.apply(event, context);

        verify(slackService, never()).analyze(testText, context, testChannel, testTimestamp);
        assertEquals(response, returnValue);
    }

    @ParameterizedTest
    @MethodSource("provideTestText")
    void apply_mentionedAndNonEmptyThreadTs(String testText) {
        final String testChannel = "testChannel";
        final String testTimestamp = "testTs";
        final String testThreadTs = "testThreadTs";
        final Response response = Response.ok();
        when(event.getEvent()).thenReturn(messageEvent);
        when(messageEvent.getText()).thenReturn(testText);
        when(messageEvent.getChannel()).thenReturn(testChannel);
        when(messageEvent.getTs()).thenReturn(testTimestamp);
        when(messageEvent.getThreadTs()).thenReturn(testThreadTs);
        when(context.ack()).thenReturn(response);

        MessageEventHandler messageEventHandler = new MessageEventHandler(slackService);
        Response returnValue = messageEventHandler.apply(event, context);

        verify(slackService, never()).analyze(testText, context, testChannel, testTimestamp);
        assertEquals(response, returnValue);
    }

    private static Stream<Arguments> provideTestText() {
        return Stream.of(
                Arguments.of("@account_id Start with mention pattern"),
                Arguments.of("Include mention @account_id pattern"),
                Arguments.of("End with mention pattern @account_id")
        );
    }
}