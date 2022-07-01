[version]: https://img.shields.io/maven-central/v/club.minnced/discord-webhooks
[download]: https://mvnrepository.com/artifact/club.minnced/discord-webhooks/latest
[license]: https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg
[license-file]: https://github.com/MinnDevelopment/discord-webhooks/blob/master/LICENSE

[WebhookClient#setErrorHandler]: https://minndevelopment.github.io/discord-webhooks/club/minnced/discord/webhook/WebhookClient.html#setErrorHandler(club.minnced.discord.webhook.util.WebhookErrorHandler)
[WebhookClient#setDefaultErrorHandler]: https://minndevelopment.github.io/discord-webhooks/club/minnced/discord/webhook/WebhookClient.html#setDefaultErrorHandler(club.minnced.discord.webhook.util.WebhookErrorHandler)

[ ![version] ][download]
[ ![license] ][license-file]

# Discord-Webhooks

Originally part of JDA, this library provides easy to use bindings for the
Discord Webhook API.

# Introduction

Here we will give a small overview of the proper usage and applicability of the resources provided by this library.

Documentation is available via the GitHub pages on this repository: [Javadoc](https://minndevelopment.github.io/discord-webhooks/overview-tree.html)

## Limitations

Webhooks on discord are only capable of sending messages, nothing more. For anything else you either have to use OAuth2 or a bot account. This library does not provide any functionality for creating or modifying webhooks.

## Getting Started

The first thing to do is to create either a `WebhookClient` or a `WebhookCluster`. The `WebhookClient` provides functionality to send messages to one webhook based on either a webhook URL or the ID and token of a webhook. It implements automatic rate-limit handling and can be configured to use a shared thread-pool.

### Creating a WebhookClient

```java
// Using the builder
WebhookClientBuilder builder = new WebhookClientBuilder(url); // or id, token
builder.setThreadFactory((job) -> {
    Thread thread = new Thread(job);
    thread.setName("Hello");
    thread.setDaemon(true);
    return thread;
});
builder.setWait(true);
WebhookClient client = builder.build();
```

```java
// Using the factory methods
WebhookClient client = WebhookClient.withUrl(url); // or withId(id, token)
```

### Creating a WebhookCluster

```java
// Create and initialize the cluster
WebhookCluster cluster = new WebhookCluster(5); // create an initial 5 slots (dynamic like lists)
cluster.setDefaultHttpClient(new OkHttpClient());
cluster.setDefaultDaemon(true);

// Create a webhook client
cluster.buildWebhook(id, token);

// Add an existing webhook client
cluster.addWebhook(client);
```

## Sending Messages

Sending messages happens in a background thread (configured through the pool/factory) and thus is async by default. To access the message you have to enable the `wait` mechanic (enabled by default). With this you can use the callbacks provided by `CompletableFuture<ReadonlyMessage>`.

```java
// Send and forget
client.send("Hello World");

// Send and log (using embed)
WebhookEmbed embed = new WebhookEmbedBuilder()
        .setColor(0xFF00EE)
        .setDescription("Hello World")
        .build();

client.send(embed)
      .thenAccept((message) -> System.out.printf("Message with embed has been sent [%s]%n", message.getId()));

// Change appearance of webhook message
WebhookMessageBuilder builder = new WebhookMessageBuilder();
builder.setUsername("Minn"); // use this username
builder.setAvatarUrl(avatarUrl); // use this avatar
builder.setContent("Hello World");
client.send(builder.build());
```

## Threads

You can use the webhook clients provided by this library to send messages in threads. There are two ways to accomplish this.

Set a thread id in the client builder to send all messages in that client to the thread:

```java
WebhookClient client = new WebhookClientBuilder(url)
        .setThreadId(threadId)
        .build();

client.send("Hello"); // appears in the thread
```

Use `onThread` to create a client with a thread id and all other settings inherited:

```java
try (WebhookClient client = WebhookClient.withUrl(url)) {
    WebhookClient thread = client.onThread(123L);
    thread.send("Hello"); // appears only in the thread with id 123
    client.send("Friend"); // appears in the channel instead
} // calls client.close() which automatically also closes all onThread clients as well.
```

All `WebhookClient` instances created with `onThread` will share the same thread pool used by the original client. This means that shutting down or closing any of the clients will also close all other clients associated with that underlying thread pool.

```java
WebhookClient thread = null;
try (WebhookClient client = WebhookClient.withUrl(url)) {
    thread = client.onThread(id);
} // closes client
thread.send("Hello"); // <- throws rejected execution due to pool being shutdown by client.close() above ^

WebhookClient client = WebhookClient.withUrl(url);
try (WebhookClient thread = client.onThread(id)) {
    thread.send("...");
} // closes thread
client.send("Hello");  // <- throws rejected execution due to pool being shutdown by thread.close() above ^
```

### Shutdown

Since the clients use threads for sending messages you should close the client to end the threads. This can be ignored if a shared thread-pool is used between multiple clients but that pool has to be shutdown by the user accordingly.

```java
try (WebhookClient client = WebhookClient.withUrl(url)) {
    client.send("Hello World");
} // client.close() automated

webhookCluster.close(); // closes each client and can be used again
```

## Error Handling

By default, this library will log every exception encountered when sending a message using the SLF4J logger implementation.
This can be configured using [WebhookClient#setErrorHandler] to custom behavior per client or [WebhookClient#setDefaultErrorHandler] for all clients.

### Example

```java
WebhookClient.setDefaultErrorHandler((client, message, throwable) -> {
    System.err.printf("[%s] %s%n", client.getId(), message);
    if (throwable != null)
        throwable.printStackTrace();
    // Shutdown the webhook client when you get 404 response (may also trigger for client#edit calls, be careful)
    if (throwable instanceof HttpException ex && ex.getCode() == 404) {
        client.close();
    }
});
```

## External Libraries

This library also supports sending webhook messages with integration from other libraries such as

- [JDA](/DV8FromTheWorld/JDA) (version 5.0.0-alpha.13) with [JDAWebhookClient](https://github.com/MinnDevelopment/discord-webhooks/blob/master/src/main/java/club/minnced/discord/webhook/external/JDAWebhookClient.java)
- [Discord4J](/Discord4J/Discord4J) (version 3.2.2) with [D4JWebhookClient](https://github.com/MinnDevelopment/discord-webhooks/blob/master/src/main/java/club/minnced/discord/webhook/external/D4JWebhookClient.java)
- [Javacord](/Javacord/Javacord) (version 3.4.0) with [JavacordWebhookClient](https://github.com/MinnDevelopment/discord-webhooks/blob/master/src/main/java/club/minnced/discord/webhook/external/JavacordWebhookClient.java)

### Example JDA

```java
public void sendWebhook(Webhook webhook) {
    Message message = new MessageBuilder();
    message.append("Hello World!");
    try (JDAWebhookClient client = JDAWebhookClient.from(webhook)) { // create a client instance from the JDA webhook
        client.send(message); // send a JDA message instance
    }
}
```

### Example Discord4J

```java
public void sendWebhook(Webhook webhook) {
    try (D4JWebhookClient client = D4JWebhookClient.from(webhook)) {
        client.send(MessageCreateSpec.create()
            .withContent("Hello World")
            .addFile("cat.png", new FileInputStream("cat.png"))
        );
    }
}
```

# Download

[ ![version] ][download]

Note: Replace `%VERSION%` below with the desired version.

## Gradle

```gradle
repositories {
    mavenCentral()
}
```

```gradle
dependencies {
    implementation("club.minnced:discord-webhooks:%VERSION%")
}
```

## Maven

```xml
<dependency>
    <groupId>club.minnced</groupId>
    <artifactId>discord-webhooks</artifactId>
    <version>%VERSION%</version>
</dependency>
```

## Compile Yourself

1. Clone repository
1. Run `gradlew shadowJar`
1. Use jar suffixed with `-all.jar` in `build/libs`


# Example

```java
class MyAppender extends AppenderBase<LoggingEvent> {
    private final WebhookClient client;

    @Override
    protected void append(LoggingEvent eventObject) {
        if (client == null)
            return;
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
        builder.setDescription(eventObject.getFormattedMessage());
        int color = -1;
        switch (eventObject.getLevel().toInt()) {
            case ERROR_INT:
                color = 0xFF0000;
                break;
            case INFO_INT:
                color = 0xF8F8FF;
                break;
        }
        if (color > 0)
            builder.setColor(color);
        builder.setTimestamp(Instant.ofEpochMilli(eventObject.getTimeStamp()));
        client.send(builder.build());
    }
}
```

> This is an example implementation of an Appender for logback-classic
