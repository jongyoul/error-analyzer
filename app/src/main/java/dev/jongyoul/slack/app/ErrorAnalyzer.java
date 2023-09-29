package dev.jongyoul.slack.app;

import java.time.Duration;
import java.util.Objects;

import com.google.gson.Gson;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageBotEvent;
import com.slack.api.model.event.MessageEvent;
import com.slack.api.model.event.ReactionAddedEvent;

import dev.jongyoul.slack.app.handler.AppMentionEventHandler;
import dev.jongyoul.slack.app.handler.MessageBotEventHandler;
import dev.jongyoul.slack.app.handler.MessageEventHandler;
import dev.jongyoul.slack.app.handler.ReactionAddedHandler;
import dev.jongyoul.slack.app.handler.SlashCommandHandler;
import dev.jongyoul.slack.app.service.OpenAiService;
import dev.jongyoul.slack.app.service.ReactionService;
import dev.jongyoul.slack.app.service.SlackService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class ErrorAnalyzer {
    private static final int HTTP_READ_TIMEOUT = 45;
    private static final String ERROR_ANALYZER_COMMAND = "/ea";

    public static void main(String[] args) throws Exception {
        // Load necessary environment
        final String appToken = Objects.requireNonNull(System.getenv("APP_TOKEN"));
        final String botToken = Objects.requireNonNull(System.getenv("BOT_TOKEN"));
        final String apiToken = Objects.requireNonNull(System.getenv("API_TOKEN"));

        // Instantiate necessary classes
        final Gson gson = new Gson();
        final OkHttpClient okHttpClient =
                new OkHttpClient.Builder().readTimeout(Duration.ofSeconds(HTTP_READ_TIMEOUT)).build();
        final ReactionService reactionService = new ReactionService();
        final OpenAiService openAiService = new OpenAiService(apiToken, okHttpClient, gson);
        final SlackService slackService =
                new SlackService(reactionService, openAiService);
        final AppMentionEventHandler appMentionEventHandler = new AppMentionEventHandler(slackService);
        final ReactionAddedHandler reactionAddedHandler =
                new ReactionAddedHandler(reactionService, slackService);
        final MessageEventHandler messageEventHandler = new MessageEventHandler(slackService);
        final MessageBotEventHandler messageBotEventHandler = new MessageBotEventHandler(slackService);
        final SlashCommandHandler slashCommandHandler = new SlashCommandHandler(slackService);

        // Set a slack app
        final AppConfig appConfig = AppConfig.builder()
                                             .singleTeamBotToken(botToken)
                                             .build();
        final App app = new App(appConfig);
        app.event(AppMentionEvent.class, appMentionEventHandler);
        app.event(ReactionAddedEvent.class, reactionAddedHandler);
        app.event(MessageEvent.class, messageEventHandler);
        app.event(MessageBotEvent.class, messageBotEventHandler);
        app.command(ERROR_ANALYZER_COMMAND, slashCommandHandler);

        final SocketModeApp socketModeApp = new SocketModeApp(appToken, app);
        socketModeApp.start();
    }
}
