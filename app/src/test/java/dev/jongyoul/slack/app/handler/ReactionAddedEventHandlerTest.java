package dev.jongyoul.slack.app.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slack.api.RequestConfigurator;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.AsyncMethodsClient;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.Message;
import com.slack.api.model.event.ReactionAddedEvent;
import com.slack.api.model.event.ReactionAddedEvent.Item;

import dev.jongyoul.slack.app.service.ReactionService;
import dev.jongyoul.slack.app.service.SlackService;

@ExtendWith(MockitoExtension.class)
class ReactionAddedEventHandlerTest {
    @Mock
    private EventsApiPayload<ReactionAddedEvent> event;

    @Mock
    private ReactionAddedEvent reactionAddedEvent;

    @Mock
    private Item item;

    @Mock
    private EventContext context;

    @Mock
    private AsyncMethodsClient asyncMethodsClient;

    @Mock
    private ConversationsHistoryResponse conversationsHistoryResponse;

    @Mock
    private List<Message> messages;

    @Mock
    private Message message;

    @Mock
    private ReactionService reactionService;
    @Mock
    private SlackService slackService;

    @Test
    void apply_correctReaction() {
        final String testReactionName = "repeat";
        final String testItemChannel = "testItemChannel";
        final String testItemTimestamp = "testItemTimestamp";
        final String testResultText = "testResultText";
        final Response response = Response.ok();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        when(event.getEvent()).thenReturn(reactionAddedEvent);
        when(reactionAddedEvent.getReaction()).thenReturn(testReactionName);
        when(reactionAddedEvent.getItem()).thenReturn(item);
        when(item.getChannel()).thenReturn(testItemChannel);
        when(item.getTs()).thenReturn(testItemTimestamp);
        when(context.asyncClient()).thenReturn(asyncMethodsClient);
        //noinspection unchecked
        when(asyncMethodsClient.conversationsHistory(any(RequestConfigurator.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> conversationsHistoryResponse));
        when(conversationsHistoryResponse.getMessages()).thenReturn(messages);
        when(messages.get(0)).thenReturn(message);
        when(message.getText()).thenReturn(testResultText);
        when(context.ack()).thenReturn(response);

        ReactionAddedEventHandler target = new ReactionAddedEventHandler(reactionService,
                                                                         slackService,
                                                                         executorService);
        Response returnValue = target.apply(event, context);

        verify(slackService, only()).ask(testResultText, context, testItemChannel, testItemTimestamp);
        assertEquals(response, returnValue);
    }

    @Test
    void apply_correctReactionWithException() throws Exception {
        final String testReactionName = "repeat";
        final String testItemChannel = "testItemChannel";
        final String testItemTimestamp = "testItemTimestamp";
        final Response response = Response.ok();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final CompletableFuture<ConversationsHistoryRequest> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    throw new RuntimeException();
                }, executorService);
        when(event.getEvent()).thenReturn(reactionAddedEvent);
        when(reactionAddedEvent.getReaction()).thenReturn(testReactionName);
        when(reactionAddedEvent.getItem()).thenReturn(item);
        when(item.getChannel()).thenReturn(testItemChannel);
        when(item.getTs()).thenReturn(testItemTimestamp);
        when(context.asyncClient()).thenReturn(asyncMethodsClient);

        //noinspection unchecked
        when(asyncMethodsClient.conversationsHistory(any(RequestConfigurator.class)))
                .thenReturn(completableFuture);
        when(context.ack()).thenReturn(response);

        ReactionAddedEventHandler target = new ReactionAddedEventHandler(reactionService,
                                                                         slackService,
                                                                         executorService);
        Response returnValue = target.apply(event, context);

        // Wait until CompletableFuture finishes
        executorService.shutdown();
        //noinspection ResultOfMethodCallIgnored
        executorService.awaitTermination(20, TimeUnit.SECONDS);

        verify(slackService, never()).ask(any(), eq(context), eq(testItemChannel), eq(testItemTimestamp));
        verify(reactionService, only()).addReaction(context, testItemChannel, testItemTimestamp, "exclamation");
        assertEquals(response, returnValue);
    }

    @Test
    void apply_invalidReaction() {
        final String testReactionName = "wrong";
        final Response response = Response.ok();
        final ExecutorService executorService = mock(ExecutorService.class);
        when(event.getEvent()).thenReturn(reactionAddedEvent);
        when(reactionAddedEvent.getReaction()).thenReturn(testReactionName);
        when(context.ack()).thenReturn(response);

        ReactionAddedEventHandler target = new ReactionAddedEventHandler(reactionService,
                                                                         slackService,
                                                                         executorService);
        Response returnValue = target.apply(event, context);

        verify(slackService, never()).ask(any(), any(), any(), any());
        verify(reactionService, never()).addReaction(any(), any(), any(), any());
        assertEquals(response, returnValue);
    }
}