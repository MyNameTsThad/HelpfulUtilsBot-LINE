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

    public static final int verID = 1;
    public static final String verString = "0.1.0-alpha";
    public static final String packageName = "io.github.mynametsthad.helpfulutilsbotline";

    public static final String ChannelID = "1656718563";
    public static final String ChannelAccessToken = "QgMnFxnTQDaCG0p3a0QduN0IA3kDU1Sk6NXCd6u4XpZZYI+6UwxG02L+2NU8a/9HfV4Fv/ZXRz/jSRvMBdNm9oG61Isa1dFBiqN9aUChDZJ1oGWzTB588lhgwlaZ9M6A/IPT9BL5MNW26RGVWDT1ZQdB04t89/1O/w1cDnyilFU=";
    public static final String ChannelSecret = "0bf3be2ae818c27aa2caec62c1592332";

    public char prefix = '>';

    public static void main(String[] args) {
        SpringApplication.run(HelpfulUtilsBot.class, args);
    }

    @EventMapping
    public Message handleCommandMessageEvent(MessageEvent<TextMessageContent> event) {
        if (event.getMessage().getText().startsWith(prefix)){
            TextMessage returnMessage = new TextMessage("'" + event.getMessage().getText().substring(1) + "': Invalid command and/or syntax!");
            String rawCommand = event.getMessage().getText().substring(1);
            String[] args = rawCommand.split(" ");
            if (args[0].equalsIgnoreCase("ver")){
                returnMessage = new TextMessage("HelpfulUtilsBot-LINE version " + verString +
                                                "\n(" + packageName + ":" + verString + " versionID: " + verID + ")" +
                                                "\nby IWant2TryHard (https://github.com/MyNameTsThad/HelpfulUtilsBot-LINE)");
            } else if (args[0].equalsIgnoreCase("prefix")) {
                if (args.length > 1){
                    prefix = args[1].charAt(0);
                    returnMessage = new TextMessage("Prefix set to '" + prefix + "'" +
                                                    "\n(Warning: Setting the prefix to a letter may cause unexpected bot responses in some messages.");
                }else{
                    returnMessage = new TextMessage("No prefix found! Please specify a prefix.");
                }
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
