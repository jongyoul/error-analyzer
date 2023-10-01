package dev.jongyoul.slack.app.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.model.Reaction;

public class ReactionService {
    public void addReaction(EventContext context, String channel, String timestamp, String name) {
        context.asyncClient().reactionsAdd(req -> req
                .channel(channel)
                .timestamp(timestamp)
                .name(name));
    }

    public void removeReaction(EventContext context, String channel, String timestamp, String name) {
        context.asyncClient().reactionsRemove(req -> req
                .channel(channel)
                .timestamp(timestamp)
                .name(name));
    }

    public CompletableFuture<List<String>> getReactions(EventContext context,
                                                        String channel,
                                                        String timestamp) {
        return context.asyncClient().reactionsGet(req -> req
                              .channel(channel)
                              .timestamp(timestamp))
                      .thenApplyAsync(reactionsGetResponse -> {
                          List<Reaction> reactions = reactionsGetResponse.getMessage().getReactions();
                          // If there's no reaction, the rest of logic wouldn't be executed
                          // The caller, however, checks if it's empty or not. Thus, it returns empty list if it's null
                          if (reactions == null) {
                              reactions = Collections.emptyList();
                          }
                          return reactions;
                      })
                      .thenApplyAsync(reactions -> reactions
                              .stream().map(Reaction::getName).collect(Collectors.toList()));
    }
}
