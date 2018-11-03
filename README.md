
# Discord-Webhooks

Originally part of JDA, this library provides easy to use bindings for the
Discord Webhook API.

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