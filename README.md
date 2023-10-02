# Error Analyzer

## Overview 

This Slack app sends error messages and receives answers analyzed by OpenAI, and sends the result to the channel as threaded messages.

## How to Run

### Prerequisites
- Java 17
- direnv
- OpenAI API key
- Slack API token
- Slack Bot token

### Setup and Run
```bash
# Copy .envrc.template to .envrc and edit the file
cp .envrc.template .envrc

# Source .envrc. You can skip it if you use `direnv`
. .envrc

./gradlew runApp
```

## Usage
- Send a message to the channel where the app is installed
- Send a message mentioning the bot id in the channel where the app is installed
- Add 🔁 reaction to the message if it's not analyzed yet