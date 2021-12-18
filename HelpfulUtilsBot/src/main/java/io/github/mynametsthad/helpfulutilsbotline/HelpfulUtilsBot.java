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

import com.linecorp.bot.client.LineMessagingClient;
import io.github.mynametsthad.helpfulutilsbotline.core.ShoppingList;
import io.github.mynametsthad.helpfulutilsbotline.core.ShoppingListElement;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@LineMessageHandler
public class HelpfulUtilsBot {
    private final Logger log = LoggerFactory.getLogger(HelpfulUtilsBot.class);

    public static final int verID = 2;
    public static final String verString = "0.1.1-alpha";
    public static final String packageName = "io.github.mynametsthad.helpfulutilsbotline";

    public static final String ChannelID = "1656718563";
    public static final String ChannelAccessToken = "QgMnFxnTQDaCG0p3a0QduN0IA3kDU1Sk6NXCd6u4XpZZYI+6UwxG02L+2NU8a/9HfV4Fv/ZXRz/jSRvMBdNm9oG61Isa1dFBiqN9aUChDZJ1oGWzTB588lhgwlaZ9M6A/IPT9BL5MNW26RGVWDT1ZQdB04t89/1O/w1cDnyilFU=";
    public static final String ChannelSecret = "0bf3be2ae818c27aa2caec62c1592332";

    public final LineMessagingClient client = LineMessagingClient.builder(HelpfulUtilsBot.ChannelAccessToken).build();

    //usage
    public List<ShoppingList> lists = new ArrayList<>();

    public char prefix = /*'>'*/'/';

    public static void main(String[] args) {
        SpringApplication.run(HelpfulUtilsBot.class, args);
    }

    @EventMapping
    public Message handleCommandMessageEvent(MessageEvent<TextMessageContent> event) {
        if (event.getMessage().getText().startsWith(String.valueOf(prefix))){
            TextMessage returnMessage = new TextMessage("'" + event.getMessage().getText().substring(1) + "': Invalid command(s) and/or syntax!");
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
                                                    "\n(Warning: Setting the prefix to a letter may cause unexpected bot responses in some messages.)");
                }else{
                    returnMessage = new TextMessage("No prefix found! Please specify a prefix.");
                }
            } else if (args[0].equalsIgnoreCase("list") | args[0].equalsIgnoreCase("l")) {
                if (args.length > 1){
                    if (args[1].equalsIgnoreCase("new") | args[1].equalsIgnoreCase("n")) {
                        if (args.length > 2){
                            ShoppingList list = new ShoppingList(args[2], Collections.singletonList(event.getSource().getSenderId()));
                            lists.add(list);
                            returnMessage = new TextMessage("Added shopping list: " + list.name);
                        }else{
                            returnMessage = new TextMessage("Specify a Name!");
                        }
                    } else if (args[1].equalsIgnoreCase("view") | args[1].equalsIgnoreCase("v")) {
                        if (args.length > 2){
                            if (lists.isEmpty()){
                                returnMessage = new TextMessage("There are no Shopping lists!");
                            }else{
                                try {
                                    int index = Integer.parseInt(args[2]);
                                    if (index <= lists.size()){
                                        ShoppingList list = lists.get(index - 1);

                                        //time calculation
                                        long timeDifference = new Date().getTime() - list.createdTimestamp; //time in milliseconds
                                        long yearsAgo = (long) Math.floor(timeDifference / 31540000000D);
                                        long monthsAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D)) / 2628000000D);
                                        long weeksAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D)) / 604800000D);
                                        long daysAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D) - (weeksAgo * 604800000D)) / 86400000D);
                                        long hoursAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D) - (weeksAgo * 604800000D) - (daysAgo * 86400000D)) / 3600000D);
                                        long minutesAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D) - (weeksAgo * 604800000D) - (daysAgo * 86400000D) - (hoursAgo * 3600000D)) / 60000D);
                                        long secondsAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D) - (weeksAgo * 604800000D) - (daysAgo * 86400000D) - (hoursAgo * 3600000D) - (minutesAgo * 60000D)) / 1000D);

                                        StringBuilder listsMessage = new StringBuilder("Shopping List '" + list.name + "':");
                                        for (int i = 0; i < list.elements.size(); i++) {
                                            ShoppingListElement element = list.elements.get(i);
                                            if (element.crossed){
                                                listsMessage.append("\n").append(i + 1).append(". ~").append(list.name).append("~");
                                            }else{
                                                listsMessage.append("\n").append(i + 1).append(". ").append(list.name);
                                            }
                                        }
                                        listsMessage.append("\n").append("Created ")
                                                .append(yearsAgo > 0 ? (yearsAgo > 1 ? (yearsAgo + " years") : (yearsAgo + " year")) : "")
                                                .append(monthsAgo > 0 ? (monthsAgo > 1 ? (monthsAgo + " months") : (monthsAgo + " month")) : "")
                                                .append(weeksAgo > 0 ? (weeksAgo > 1 ? (weeksAgo + " weeks") : (weeksAgo + " week")) : "")
                                                .append(daysAgo > 0 ? (daysAgo > 1 ? (daysAgo + " days") : (daysAgo + " day")) : "")
                                                .append(hoursAgo > 0 ? (hoursAgo > 1 ? (hoursAgo + " hours") : (hoursAgo + " hour")) : "")
                                                .append(minutesAgo > 0 ? (minutesAgo > 1 ? (minutesAgo + " minutes") : (minutesAgo + " minute")) : "")
                                                .append(secondsAgo > 0 ? (secondsAgo > 1 ? (secondsAgo + " seconds") : (secondsAgo + " second")) : "");
                                        returnMessage = new TextMessage(listsMessage.toString());
                                    }else{
                                        returnMessage = new TextMessage(args[2] + " is outside the Shopping List's index range!");
                                    }
                                }catch (NumberFormatException e){
                                    returnMessage = new TextMessage(args[2] + " is Not a Number!");
                                }
                            }
                        }else{
                            StringBuilder message2 = new StringBuilder("Shopping Lists:");
                            for (int i = 0; i < lists.size(); i++) {
                                ShoppingList list = lists.get(i);
                                message2.append("\n").append(i + 1).append(". ").append(list.name);
                            }
                            returnMessage = new TextMessage(message2.toString());
                        }
                    }
                }else{
                    returnMessage = new TextMessage("Specify a subcommand!");
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
