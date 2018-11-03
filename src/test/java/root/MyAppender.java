/*
 * Copyright 2018-2019 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package root;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;

import static ch.qos.logback.classic.Level.*;

public class MyAppender extends AppenderBase<LoggingEvent> {
    private static final String url;

    static {
        String tmp;
        try {
            tmp = Files.lines(new File("webhook_url.txt").toPath()).findFirst().orElse(null);
        }
        catch (IOException e) {
            tmp = null;
        }
        url = tmp;
    }

    private WebhookClient client;

    @Override
    public void start() {
        if (url != null)
            client = new WebhookClientBuilder(url).setDaemon(true).build();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (client == null)
            return;
        client.close();
        client = null;
    }

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
