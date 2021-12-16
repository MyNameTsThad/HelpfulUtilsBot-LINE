/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.github.mynametsthad.helpfulutilsbotline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@SpringBootApplication
@LineMessageHandler
public class HelpfulUtilsBot {
    private final Logger log = LoggerFactory.getLogger(HelpfulUtilsBot.class);

    public final int verID = 1;
    public final String verString = "0.1.0-alpha";
    public final String packageName = "io.github.mynametsthad.helpfulutilsbotline";

    public final String ChannelID = "1656718563";
    public final String ChannelAccessToken = "QgMnFxnTQDaCG0p3a0QduN0IA3kDU1Sk6NXCd6u4XpZZYI+6UwxG02L+2NU8a/9HfV4Fv/ZXRz/jSRvMBdNm9oG61Isa1dFBiqN9aUChDZJ1oGWzTB588lhgwlaZ9M6A/IPT9BL5MNW26RGVWDT1ZQdB04t89/1O/w1cDnyilFU=";
    public final String ChannelSecret = "0bf3be2ae818c27aa2caec62c1592332";

    public static void main(String[] args) {
        SpringApplication.run(HelpfulUtilsBot.class, args);
    }

    @EventMapping
    public Message handleCommandMessageEvent(MessageEvent<TextMessageContent> event) {
        if (event.getMessage().getText().startsWith(">")){
            TextMessage returnMessage = new TextMessage("'" + event.getMessage().getText().substring(1) + "': Invalid command and/or syntax!");
            String rawCommand = event.getMessage().getText().substring(1);
            String[] args = rawCommand.split(" ");
            if (args[0].equalsIgnoreCase("ver")){
                returnMessage = new TextMessage("HelpfulUtilsBot-LINE version " + verString + " (" + packageName + ":" + verString + "versionID: " + verID + ") \n" +
                                                "by IWant2TryHard (https://github.com/MyNameTsThad/HelpfulUtilsBot-LINE)");
            }
            return returnMessage;
        }
        return null;
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
