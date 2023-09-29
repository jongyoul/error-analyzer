package dev.jongyoul.slack.app.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Message {
    private final String role;
    private final String content;
}
