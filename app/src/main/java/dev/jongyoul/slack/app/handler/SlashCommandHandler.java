package dev.jongyoul.slack.app.handler;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;

import dev.jongyoul.slack.app.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SlashCommandHandler implements com.slack.api.bolt.handler.builtin.SlashCommandHandler {
    private static final String GEN_ERROR_KEY = "gen-error";

    private final SlackService slackService;

    @Override
    public Response apply(SlashCommandRequest slashCommandRequest, SlashCommandContext context) {
        final SlashCommandPayload slashCommandPayload = slashCommandRequest.getPayload();
        final String channel = slashCommandPayload.getChannelId();
        final String text = slashCommandPayload.getText();
        //noinspection SwitchStatementWithTooFewBranches
        switch (text) {
            case GEN_ERROR_KEY -> slackService.generateError(context, channel);
            default -> log.warn("wrong command");
        }
        return context.ack();
    }
}
