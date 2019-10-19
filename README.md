[version]: https://api.bintray.com/packages/minndevelopment/maven/discord-webhooks/images/download.svg
[download]: https://bintray.com/minndevelopment/maven/discord-webhooks/_latestVersion
[license]: https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg
[license-file]: https://github.com/MinnDevelopment/discord-webhooks/blob/master/LICENSE

[ ![version] ][download]
[ ![license] ][license-file]

# Discord-Webhooks

Originally part of JDA, this library provides easy to use bindings for the
Discord Webhook API.

# Introduction

Here we will give a small overview of the proper usage and applicability of the resources provided by this library.

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
WebhookClient client = WebhookClient.fromUrl(url); // or fromId(id, token)
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

client.send(embed);
      .thenAccept((message) -> System.out.printf("Message with embed has been sent [%s]%n", message.getId()));

// Change appearance of webhook message
WebhookMessageBuilder builder = new WebhookMessageBuilder();
builder.setUsername("Minn"); // use this username
builder.setAvatarUrl(avatarUrl); // use this avatar
builder.setContent("Hello World");
client.send(builder.build());
```

### Shutdown

Since the clients use threads for sending messages you should close the client to end the threads. This can be ignored if a shared thread-pool is used between multiple clients but that pool has to be shutdown by the user accordingly.

```java
try (WebhookClient client = WebhookClient.withUrl(url)) {
    client.send("Hello World");
} // client.close() automated

webhookCluster.close(); // closes each client and can be used again
```

# Download

Note: Replace `%VERSION%` below with the desired version.

## Gradle

```gradle
repositories {
    jcenter()
}
```

```gradle
dependencies {
    compile("club.minnced:discord-webhooks:%VERSION%")
}
```

## Maven

```xml
<repository>
    <name>jcenter</name>
    <id>bintray-jcenter</id>
    <url>https://jcenter.bintray.com</url>
</repository>
```

```xml
<dependency>
    <groupId>club.minnced</groupId>
    <artifactId>discord-webhooks</artifactId>
    <version>%VERSION%</version>
</dependency>
```

## Compile Yourself

1. Clone repostiory
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
