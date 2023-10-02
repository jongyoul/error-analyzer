package dev.jongyoul.slack.app.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

import dev.jongyoul.slack.app.service.SlackService;

@ExtendWith(MockitoExtension.class)
class SlashCommandHandlerTest {
    @Mock
    private SlashCommandContext context;

    @Mock
    private SlashCommandRequest slashCommandRequest;

    @Mock
    private SlackService slackService;

    @Mock
    private SlashCommandPayload slashCommandPayload;

    @Test
    void apply_correctCommand() {
        final String testChannel = "textChannel";
        final String testText = "gen-error";
        when(slashCommandRequest.getPayload()).thenReturn(slashCommandPayload);
        when(slashCommandPayload.getChannelId()).thenReturn(testChannel);
        when(slashCommandPayload.getText()).thenReturn(testText);

        SlashCommandHandler target = new SlashCommandHandler(slackService);
        target.apply(slashCommandRequest, context);

        verify(slackService, only()).generateError(context, testChannel);
    }

    @Test
    void apply_wrongCommand() {
        final String testChannel = "textChannel";
        final String testText = "wrong-parameter";
        when(slashCommandRequest.getPayload()).thenReturn(slashCommandPayload);
        when(slashCommandPayload.getChannelId()).thenReturn(testChannel);
        when(slashCommandPayload.getText()).thenReturn(testText);

        SlashCommandHandler target = new SlashCommandHandler(slackService);
        target.apply(slashCommandRequest, context);

        verify(slackService, never()).generateError(any(), any());
    }
}
