package io.github.mynametsthad.helpfulutilsbotline;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;
import io.github.mynametsthad.helpfulutilsbotline.core.ShoppingList;
import io.github.mynametsthad.helpfulutilsbotline.core.ShoppingListElement;
import io.github.mynametsthad.helpfulutilsbotline.core.Timer;
import io.github.mynametsthad.helpfulutilsbotline.core.TimerInstance;
import io.github.mynametsthad.helpfulutilsbotline.core.Utils;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@LineMessageHandler
public class HelpfulUtilsBot {
    private final Logger log = LoggerFactory.getLogger(HelpfulUtilsBot.class);

    public static final int verID = 3;
    public static final String verString = "0.1.4-alpha";
    public static final String packageName = "io.github.mynametsthad.helpfulutilsbotline";

    public static final String ChannelID = "1656718563";
    public static final String ChannelAccessToken = "QgMnFxnTQDaCG0p3a0QduN0IA3kDU1Sk6NXCd6u4XpZZYI+6UwxG02L+2NU8a/9HfV4Fv/ZXRz/jSRvMBdNm9oG61Isa1dFBiqN9aUChDZJ1oGWzTB588lhgwlaZ9M6A/IPT9BL5MNW26RGVWDT1ZQdB04t89/1O/w1cDnyilFU=";
    public static final String ChannelSecret = "0bf3be2ae818c27aa2caec62c1592332";

    public final LineMessagingClient client = LineMessagingClient.builder(HelpfulUtilsBot.ChannelAccessToken).build();

    //usage
    public List<ShoppingList> lists = new ArrayList<>();

    public static List<Timer> timers = new ArrayList<>();
    public static List<TimerInstance> runningTimers = new ArrayList<>();

    public char prefix = /*'>'*/'/';

    public static void main(String[] args) {
        SpringApplication.run(HelpfulUtilsBot.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        Runnable tick = () -> {
            if (!runningTimers.isEmpty()){
                for (TimerInstance instance : runningTimers) {
                    if (instance.isPaused()) {
                        instance.setStartTime(instance.getStartTime() + 1000L);
                    } else {
                        instance.setTimeLeft(instance.getTimeLeft() - 1000L);
                    }
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(tick, 0, 1, TimeUnit.SECONDS);
    }


    @EventMapping
    public Message handleCommandMessageEvent(MessageEvent<TextMessageContent> event) {
        if (event.getMessage().getText().startsWith(String.valueOf(prefix))) {
            TextMessage returnMessage = new TextMessage("'" + event.getMessage().getText().substring(1) + "': Invalid command(s) and/or syntax!");
            String rawCommand = event.getMessage().getText().substring(1);
            String[] args = rawCommand.split(" ");
            if (args[0].equalsIgnoreCase("ver")) {
                returnMessage = new TextMessage("HelpfulUtilsBot-LINE version " + verString +
                        "\n(" + packageName + ":" + verString + " versionID: " + verID + ")" +
                        "\nby IWant2TryHard (https://github.com/MyNameTsThad/HelpfulUtilsBot-LINE)");
            } else if (args[0].equalsIgnoreCase("prefix")) {
                if (args.length > 1) {
                    prefix = args[1].charAt(0);
                    returnMessage = new TextMessage("Prefix set to '" + prefix + "'" +
                            "\n(Warning: Setting the prefix to a letter may cause unexpected bot responses in some messages.)");
                } else {
                    returnMessage = new TextMessage("No prefix found! Please specify a prefix.");
                }
            } else if (args[0].equalsIgnoreCase("list") | args[0].equalsIgnoreCase("l")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("new") | args[1].equalsIgnoreCase("n")) {
                        if (args.length > 2) {
                            StringBuilder newListName = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                if (i >= 2) {
                                    newListName.append(args[i]).append(" ");
                                }
                            }
                            ShoppingList list = new ShoppingList(newListName.toString().trim(), Collections.singletonList(event.getSource().getSenderId()));
                            lists.add(list);
                            returnMessage = new TextMessage("Added shopping list: " + list.name);
                        } else {
                            returnMessage = new TextMessage("Specify a Name!");
                        }
                    } else if (args[1].equalsIgnoreCase("view") | args[1].equalsIgnoreCase("v")) {
                        if (args.length > 2) {
                            if (lists.isEmpty()) {
                                returnMessage = new TextMessage("There are no Shopping lists!");
                            } else {
                                try {
                                    int index = Integer.parseInt(args[2]);
                                    if (index <= lists.size()) {
                                        ShoppingList list = lists.get(index - 1);

                                        //time calculation
                                        List<Long> datesAgo = Utils.getFormattedTimeDiffrence(list.createdTimestamp, 7);
                                        long yearsAgo = datesAgo.get(0);
                                        long monthsAgo = datesAgo.get(1);
                                        long weeksAgo = datesAgo.get(2);
                                        long daysAgo = datesAgo.get(3);
                                        long hoursAgo = datesAgo.get(4);
                                        long minutesAgo = datesAgo.get(5);
                                        long secondsAgo = datesAgo.get(6);

                                        StringBuilder listsMessage = new StringBuilder("Shopping List '" + list.name + "':");
                                        for (int i = 0; i < list.elements.size(); i++) {
                                            ShoppingListElement element = list.elements.get(i);
                                            if (element.crossed) {
                                                listsMessage.append("\n").append(i + 1).append(". ~").append(element.quantity).append(" of ").append(element.name).append("~");
                                            } else {
                                                listsMessage.append("\n").append(i + 1).append(". ").append(element.quantity).append(" of ").append(element.name);
                                            }
                                        }
                                        listsMessage.append("\n").append("Created ")
                                                .append(yearsAgo > 0 ? (yearsAgo > 1 ? (yearsAgo + " years ") : (yearsAgo + " year ")) : "")
                                                .append(monthsAgo > 0 ? (monthsAgo > 1 ? (monthsAgo + " months ") : (monthsAgo + " month ")) : "")
                                                .append(weeksAgo > 0 ? (weeksAgo > 1 ? (weeksAgo + " weeks ") : (weeksAgo + " week ")) : "")
                                                .append(daysAgo > 0 ? (daysAgo > 1 ? (daysAgo + " days ") : (daysAgo + " day ")) : "")
                                                .append(hoursAgo > 0 ? (hoursAgo > 1 ? (hoursAgo + " hours ") : (hoursAgo + " hour ")) : "")
                                                .append(minutesAgo > 0 ? (minutesAgo > 1 ? (minutesAgo + " minutes ") : (minutesAgo + " minute ")) : "")
                                                .append(secondsAgo > 0 ? (secondsAgo > 1 ? (secondsAgo + " seconds ") : (secondsAgo + " second ")) : "")
                                                .append("ago");
                                        returnMessage = new TextMessage(listsMessage.toString());
                                    } else {
                                        returnMessage = new TextMessage(args[2] + " is outside the Shopping List's index range!");
                                    }
                                } catch (NumberFormatException e) {
                                    returnMessage = new TextMessage(args[2] + " is Not a Number! :1");
                                }
                            }
                        } else {
                            StringBuilder message2 = new StringBuilder("Shopping Lists:");
                            for (int i = 0; i < lists.size(); i++) {
                                ShoppingList list = lists.get(i);
                                message2.append("\n").append(i + 1).append(". ").append(list.name);
                            }
                            returnMessage = new TextMessage(message2.toString());
                        }
                    } else if (args[1].equalsIgnoreCase("edit") | args[1].equalsIgnoreCase("e")) {
                        if (args.length > 2) {
                            try {
                                int index = Integer.parseInt(args[2]);
                                if (index <= lists.size()) {
                                    ShoppingList list = lists.get(index - 1);
                                    if (args.length > 3) {
                                        if (args.length > 4) {
                                            if (args[3].equalsIgnoreCase("add") | args[3].equalsIgnoreCase("a")) {
                                                StringBuilder newItemName = new StringBuilder();
                                                try {
                                                    int count = Integer.parseInt(args[4]);
                                                    if (args.length > 5) {
                                                        for (int i = 0; i < args.length; i++) {
                                                            if (i >= 5) {
                                                                newItemName.append(args[i]).append(" ");
                                                            }
                                                        }
                                                        list.AddElements(new ShoppingListElement(newItemName.toString().trim(), count));
                                                        returnMessage = new TextMessage("Added item '" + count + " of " + newItemName.toString().trim() + "' to Shopping List '" + list.name + "'");
                                                    } else {
                                                        returnMessage = new TextMessage("No item name provided!");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    if (args.length > 5) {
                                                        for (int i = 0; i < args.length; i++) {
                                                            if (i >= 5) {
                                                                newItemName.append(args[i]).append(" ");
                                                            }
                                                        }
                                                        list.AddElements(new ShoppingListElement(newItemName.toString().trim(), 1));
                                                        returnMessage = new TextMessage(args[4] + " is Not a Number! Using default quantity of 1." +
                                                                "\nAdded item '1 of " + newItemName.toString().trim() + "' to Shopping List '" + list.name + "'");
                                                    } else {
                                                        returnMessage = new TextMessage("No item name provided!");
                                                    }
                                                }
                                            } else if (args[3].equalsIgnoreCase("remove") | args[3].equalsIgnoreCase("r")) {
                                                try {
                                                    int listIndex = Integer.parseInt(args[4]);
                                                    if (listIndex <= list.elements.size()) {
                                                        ShoppingListElement ele = list.elements.get(listIndex - 1);
                                                        list.RemoveElement(listIndex - 1);
                                                        returnMessage = new TextMessage("Removed item '" + ele.name + "' from Shopping List '" + list.name + "'.");
                                                    } else {
                                                        returnMessage = new TextMessage(args[4] + " is outside this Shopping List's items' index range!");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    returnMessage = new TextMessage(args[4] + " is Not a Number! Specify a Item Index to remove!");
                                                }
                                            } else if (args[3].equalsIgnoreCase("cross") | args[3].equalsIgnoreCase("cr")) {
                                                try {
                                                    int listIndex = Integer.parseInt(args[4]);
                                                    if (listIndex <= list.elements.size()) {
                                                        list.elements.get(listIndex - 1).crossed = true;
                                                        returnMessage = new TextMessage("Crossed item '" + list.elements.get(listIndex - 1).name + "' from Shopping List '" + list.name + "'.");
                                                    } else {
                                                        returnMessage = new TextMessage(args[4] + " is outside this Shopping List's items' index range!");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    returnMessage = new TextMessage(args[4] + " is Not a Number! Specify a Item Index to cross!");
                                                }
                                            }
                                        } else {
                                            returnMessage = new TextMessage("Specify a name/index!");
                                        }
                                    } else {
                                        returnMessage = new TextMessage("Specify a edit command! (add(a)/remove(rm)/cross(cr)/change(ch))");
                                    }
                                } else {
                                    returnMessage = new TextMessage(args[2] + " is outside the Shopping List's index range!");
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = new TextMessage(args[2] + " is Not a Number! :2");
                            }
                        } else {
                            returnMessage = new TextMessage("Specify a Shopping List to edit!");
                        }
                    }
                } else {
                    returnMessage = new TextMessage("Specify a subcommand!");
                }
            } else if (args[0].equalsIgnoreCase("timer") | args[0].equalsIgnoreCase("t")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("new") | args[1].equalsIgnoreCase("n")) {
                        //timer new <Duration> [Name]
                        if (args.length > 2) {
                            String[] durations = args[2].split(",");
                            int years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
                            if (durations.length >= 7) {
                                years = Integer.parseInt(durations[0]);
                                months = Integer.parseInt(durations[1]);
                                weeks = Integer.parseInt(durations[2]);
                                days = Integer.parseInt(durations[3]);
                                hours = Integer.parseInt(durations[4]);
                                minutes = Integer.parseInt(durations[5]);
                                seconds = Integer.parseInt(durations[6]);
                            } else if (durations.length == 6) {
                                months = Integer.parseInt(durations[0]);
                                weeks = Integer.parseInt(durations[1]);
                                days = Integer.parseInt(durations[2]);
                                hours = Integer.parseInt(durations[3]);
                                minutes = Integer.parseInt(durations[4]);
                                seconds = Integer.parseInt(durations[5]);
                            } else if (durations.length == 5) {
                                weeks = Integer.parseInt(durations[0]);
                                days = Integer.parseInt(durations[1]);
                                hours = Integer.parseInt(durations[2]);
                                minutes = Integer.parseInt(durations[3]);
                                seconds = Integer.parseInt(durations[4]);
                            } else if (durations.length == 4) {
                                days = Integer.parseInt(durations[0]);
                                hours = Integer.parseInt(durations[1]);
                                minutes = Integer.parseInt(durations[2]);
                                seconds = Integer.parseInt(durations[3]);
                            } else if (durations.length == 3) {
                                hours = Integer.parseInt(durations[0]);
                                minutes = Integer.parseInt(durations[1]);
                                seconds = Integer.parseInt(durations[2]);
                            } else if (durations.length == 2) {
                                minutes = Integer.parseInt(durations[0]);
                                seconds = Integer.parseInt(durations[1]);
                            } else if (durations.length == 1) {
                                seconds = Integer.parseInt(durations[0]);
                            }
                            StringBuilder newTimerName = new StringBuilder();
                            if (args.length > 3) {
                                for (int i = 0; i < args.length; i++) {
                                    if (i >= 3) {
                                        newTimerName.append(args[i]).append(" ");
                                    }
                                }
                            } else {
                                newTimerName.append("Timer #").append(timers.size() + 1);
                            }
                            Timer timer = new Timer(newTimerName.toString().trim(), Utils.timeToMillis(years, months, weeks, days, hours, minutes, seconds));
                            timers.add(timer);
                            StringBuilder message = new StringBuilder("Created new Timer: '" + newTimerName.toString().trim() + "' with a duration of ");
                            message.append(years > 0 ? (years > 1 ? (years + " years ") : (years + " year ")) : "")
                                    .append(months > 0 ? (months > 1 ? (months + " months ") : (months + " month ")) : "")
                                    .append(weeks > 0 ? (weeks > 1 ? (weeks + " weeks ") : (weeks + " week ")) : "")
                                    .append(days > 0 ? (days > 1 ? (days + " days ") : (days + " day ")) : "")
                                    .append(hours > 0 ? (hours > 1 ? (hours + " hours ") : (hours + " hour ")) : "")
                                    .append(minutes > 0 ? (minutes > 1 ? (minutes + " minutes ") : (minutes + " minute ")) : "")
                                    .append(seconds > 0 ? (seconds > 1 ? (seconds + " seconds ") : (seconds + " second ")) : "");
                            message.setLength(message.length() - 1);
                            message.append(".").append(" (Timer ID: ").append(timer.getId()).append(")");

                            returnMessage = new TextMessage(message.toString());
                        }
                    } else if (args[1].equalsIgnoreCase("start") | args[1].equalsIgnoreCase("s")) {
                        //timer start <id/index> <ID/Index> [Name]
                        if (args.length > 3) {
                            if (args[2].equalsIgnoreCase("id")) {
                                try {
                                    long id = Long.parseLong(args[3]);
                                    boolean a = false;
                                    for (Timer timer : timers) {
                                        if (timer.getId() == id) {
                                            a = true;
                                            StringBuilder newTimerInstanceName = new StringBuilder();
                                            if (args.length > 4) {
                                                for (int i = 0; i < args.length; i++) {
                                                    if (i >= 4) {
                                                        newTimerInstanceName.append(args[i]).append(" ");
                                                    }
                                                }
                                            } else {
                                                newTimerInstanceName.append("Timer #").append(runningTimers.size() + 1);
                                            }
                                            TimerInstance instance = new TimerInstance(newTimerInstanceName.toString().trim(), timer, false);
                                            runningTimers.add(instance);

                                            returnMessage = new TextMessage("Created new Timer Instance: '" + newTimerInstanceName.toString().trim() + "' Under parent Timer: '" + timer.getName() + "'" + "(Timer Instance ID: " + timer.getId() + ")");
                                        }
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid ID!");
                                    }
                                } catch (NumberFormatException e) {
                                    returnMessage = new TextMessage(args[3] + " is Not a Number!");
                                }
                            } else if (args[2].equalsIgnoreCase("index")) {
                                try {
                                    int index = Integer.parseInt(args[3]);
                                    index -= 1;
                                    boolean a = false;
                                    if (index < timers.size()) {
                                        Timer timer = timers.get(index);
                                        a = true;
                                        StringBuilder newTimerInstanceName = new StringBuilder();
                                        if (args.length > 4) {
                                            for (int i = 0; i < args.length; i++) {
                                                if (i >= 4) {
                                                    newTimerInstanceName.append(args[i]).append(" ");
                                                }
                                            }
                                        } else {
                                            newTimerInstanceName.append("Timer #").append(runningTimers.size() + 1);
                                        }
                                        TimerInstance instance = new TimerInstance(newTimerInstanceName.toString().trim(), timer, false);
                                        runningTimers.add(instance);

                                        returnMessage = new TextMessage("Created new Timer Instance: '" + newTimerInstanceName.toString().trim() + "' Under parent Timer: '" + timer.getName() + "'" + "(Timer Instance ID: " + timer.getId() + ")");
                                    } else {
                                        returnMessage = new TextMessage(index + " is outside the Timer list's index range!");
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid Index!");
                                    }
                                } catch (NumberFormatException e) {
                                    returnMessage = new TextMessage(args[3] + " is Not a Number!");
                                }
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("pause") | args[1].equalsIgnoreCase("p")) {
                        //timer pause <id/index/name> <ID/Index/Name>
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("id")) {
                                try {
                                    long id = Long.parseLong(args[3]);
                                    boolean a = false;
                                    for (TimerInstance timerInstance : runningTimers) {
                                        if (timerInstance.getId() == id) {
                                            a = true;
                                            timerInstance.setPaused(true);

                                            returnMessage = new TextMessage("Paused Timer Instance: '" + timerInstance.getName() + "'");
                                        }
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid ID!");
                                    }
                                } catch (NumberFormatException e) {
                                    returnMessage = new TextMessage(args[3] + " is Not a Number!");
                                }
                            } else if (args[2].equalsIgnoreCase("index")) {
                                try {
                                    int index = Integer.parseInt(args[3]);
                                    index -= 1;
                                    boolean a = false;
                                    if (index < runningTimers.size()) {
                                        TimerInstance timerInstance = runningTimers.get(index);
                                        a = true;
                                        timerInstance.setPaused(true);

                                        returnMessage = new TextMessage("Paused Timer Instance: '" + timerInstance.getName() + "'");
                                    } else {
                                        returnMessage = new TextMessage(index + " is outside the Timer list's index range!");
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid Index!");
                                    }
                                } catch (NumberFormatException e) {
                                    returnMessage = new TextMessage(args[3] + " is Not a Number!");
                                }
                            } else if (args[2].equalsIgnoreCase("name")) {
                                StringBuilder name = new StringBuilder();
                                if (args.length > 3) {
                                    for (int i = 0; i < args.length; i++) {
                                        if (i >= 3) {
                                            name.append(args[i]).append(" ");
                                        }
                                    }
                                    boolean a = false;
                                    for (TimerInstance timerInstance : runningTimers) {
                                        if (timerInstance.getName().equals(name.toString().trim())) {
                                            a = true;
                                            timerInstance.setPaused(true);

                                            returnMessage = new TextMessage("Paused Timer Instance: '" + timerInstance.getName() + "'");
                                        }
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid Name!");
                                    }
                                }
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("resume") | args[1].equalsIgnoreCase("r")) {
                        //timer resume <ID/Index/Name>
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("id")) {
                                try {
                                    long id = Long.parseLong(args[3]);
                                    boolean a = false;
                                    for (TimerInstance timerInstance : runningTimers) {
                                        if (timerInstance.getId() == id) {
                                            a = true;
                                            timerInstance.setPaused(false);

                                            returnMessage = new TextMessage("Resumed Timer Instance: '" + timerInstance.getName() + "'");
                                        }
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid ID!");
                                    }
                                } catch (NumberFormatException e) {
                                    returnMessage = new TextMessage(args[3] + " is Not a Number!");
                                }
                            } else if (args[2].equalsIgnoreCase("index")) {
                                try {
                                    int index = Integer.parseInt(args[3]);
                                    index -= 1;
                                    boolean a = false;
                                    if (index < runningTimers.size()) {
                                        TimerInstance timerInstance = runningTimers.get(index);
                                        a = true;
                                        timerInstance.setPaused(false);

                                        returnMessage = new TextMessage("Resumed Timer Instance: '" + timerInstance.getName() + "'");
                                    } else {
                                        returnMessage = new TextMessage(index + " is outside the Timer list's index range!");
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid Index!");
                                    }
                                } catch (NumberFormatException e) {
                                    returnMessage = new TextMessage(args[3] + " is Not a Number!");
                                }
                            } else if (args[2].equalsIgnoreCase("name")) {
                                StringBuilder name = new StringBuilder();
                                if (args.length > 3) {
                                    for (int i = 0; i < args.length; i++) {
                                        if (i >= 3) {
                                            name.append(args[i]).append(" ");
                                        }
                                    }
                                    boolean a = false;
                                    for (TimerInstance timerInstance : runningTimers) {
                                        if (timerInstance.getName().equals(name.toString().trim())) {
                                            a = true;
                                            timerInstance.setPaused(false);

                                            returnMessage = new TextMessage("Resumed Timer Instance: '" + timerInstance.getName() + "'");
                                        }
                                    }
                                    if (!a) {
                                        returnMessage = new TextMessage(args[3] + " is Not a Valid Name!");
                                    }
                                }
                            }
                        }
                    }else if (args[1].equalsIgnoreCase("delete") | args[1].equalsIgnoreCase("d")) {
                        //timer delete <template/running> <id/index/name>
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("template") | args[2].equalsIgnoreCase("t")) {
                                if (args[3].equalsIgnoreCase("id")) {
                                    try {
                                        long id = Long.parseLong(args[4]);
                                        boolean a = false;
                                        for (Timer timer : timers) {
                                            if (timer.getId() == id) {
                                                a = true;
                                                timers.remove(timer);

                                                returnMessage = new TextMessage("Deleted Timer: '" + timer.getName() + "'");
                                            }
                                        }
                                        if (!a) {
                                            returnMessage = new TextMessage(args[4] + " is Not a Valid ID!");
                                        }
                                    } catch (NumberFormatException e) {
                                        returnMessage = new TextMessage(args[4] + " is Not a Number!");
                                    }
                                } else if (args[3].equalsIgnoreCase("index")) {
                                    try {
                                        int index = Integer.parseInt(args[4]);
                                        index -= 1;
                                        boolean a = false;
                                        if (index < timers.size()) {
                                            Timer timer = timers.get(index);
                                            a = true;
                                            timers.remove(timer);

                                            returnMessage = new TextMessage("Deleted Timer: '" + timer.getName() + "'");
                                        } else {
                                            returnMessage = new TextMessage(index + " is outside the Timer list's index range!");
                                        }
                                        if (!a) {
                                            returnMessage = new TextMessage(args[4] + " is Not a Valid Index!");
                                        }
                                    } catch (NumberFormatException e) {
                                        returnMessage = new TextMessage(args[4] + " is Not a Number!");
                                    }
                                } else if (args[3].equalsIgnoreCase("name")) {
                                    StringBuilder name = new StringBuilder();
                                    if (args.length > 4) {
                                        for (int i = 0; i < args.length; i++) {
                                            if (i >= 4) {
                                                name.append(args[i]).append(" ");
                                            }
                                        }
                                        boolean a = false;
                                        for (Timer timer : timers) {
                                            if (timer.getName().equals(name.toString().trim())) {
                                                a = true;
                                                timers.remove(timer);

                                                returnMessage = new TextMessage("Deleted Timer: '" + timer.getName() + "'");
                                            }
                                        }
                                        if (!a) {
                                            returnMessage = new TextMessage(args[4] + " is Not a Valid Name!");
                                        }
                                    }
                                }
                            } else if (args[2].equalsIgnoreCase("running") | args[2].equalsIgnoreCase("r")) {
                                if (args[3].equalsIgnoreCase("id")) {
                                    try {
                                        long id = Long.parseLong(args[4]);
                                        boolean a = false;
                                        for (TimerInstance timerInstance : runningTimers) {
                                            if (timerInstance.getId() == id) {
                                                a = true;
                                                runningTimers.remove(timerInstance);

                                                returnMessage = new TextMessage("Deleted Timer Instance: '" + timerInstance.getName() + "'");
                                            }
                                        }
                                        if (!a) {
                                            returnMessage = new TextMessage(args[4] + " is Not a Valid ID!");
                                        }
                                    } catch (NumberFormatException e) {
                                        returnMessage = new TextMessage(args[4] + " is Not a Number!");
                                    }
                                } else if (args[3].equalsIgnoreCase("index")) {
                                    try {
                                        int index = Integer.parseInt(args[4]);
                                        index -= 1;
                                        boolean a = false;
                                        if (index < runningTimers.size()) {
                                            TimerInstance timerInstance = runningTimers.get(index);
                                            a = true;
                                            runningTimers.remove(timerInstance);

                                            returnMessage = new TextMessage("Deleted Timer Instance: '" + timerInstance.getName() + "'");
                                        } else {
                                            returnMessage = new TextMessage(index + " is outside the Timer list's index range!");
                                        }
                                        if (!a) {
                                            returnMessage = new TextMessage(args[4] + " is Not a Valid Index!");
                                        }
                                    } catch (NumberFormatException e) {
                                        returnMessage = new TextMessage(args[4] + " is Not a Number!");
                                    }
                                } else if (args[3].equalsIgnoreCase("name")) {
                                    StringBuilder name = new StringBuilder();
                                    if (args.length > 4) {
                                        for (int i = 0; i < args.length; i++) {
                                            if (i >= 4) {
                                                name.append(args[i]).append(" ");
                                            }
                                        }
                                        boolean a = false;
                                        for (TimerInstance timerInstance : runningTimers) {
                                            if (timerInstance.getName().equals(name.toString().trim())) {
                                                a = true;
                                                runningTimers.remove(timerInstance);

                                                returnMessage = new TextMessage("Deleted Timer Instance: '" + timerInstance.getName() + "'");
                                            }
                                        }
                                        if (!a) {
                                            returnMessage = new TextMessage(args[4] + " is Not a Valid Name!");
                                        }
                                    }
                                }
                            }
                        }
                    }else if (args[1].equalsIgnoreCase("view") | args[1].equalsIgnoreCase("v")) {
                        //timer view <templates/running>
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("templates") | args[2].equalsIgnoreCase("t")) {
                                StringBuilder message = new StringBuilder("Timer templates for bot instance: " + timers.size() + " instances." +
                                        "\n=======================\n");
                                for (int i = 0; i < timers.size(); i++) {
                                    Timer timer = timers.get(i);
                                    List<Long> times = Utils.getFormattedTimeDiffrence(timer.getDurationMillis(), 0, 7);
                                    message.append(i + 1).append(". ").append(timer.getName()).append(" (Duration: ")
                                            .append(times.get(0) > 0 ? (times.get(0) > 1 ? (times.get(0) + " years ") : (times.get(0) + " year ")) : "")
                                            .append(times.get(1) > 0 ? (times.get(1) > 1 ? (times.get(1) + " months ") : (times.get(1) + " month ")) : "")
                                            .append(times.get(2) > 0 ? (times.get(2) > 1 ? (times.get(2) + " weeks ") : (times.get(2) + " week ")) : "")
                                            .append(times.get(3) > 0 ? (times.get(3) > 1 ? (times.get(3) + " days ") : (times.get(3) + " day ")) : "")
                                            .append(times.get(4) > 0 ? (times.get(4) > 1 ? (times.get(4) + " hours ") : (times.get(4) + " hour ")) : "")
                                            .append(times.get(5) > 0 ? (times.get(5) > 1 ? (times.get(5) + " minutes ") : (times.get(5) + " minute ")) : "")
                                            .append(times.get(6) > 0 ? (times.get(6) > 1 ? (times.get(6) + " seconds") : (times.get(6) + " second")) : "")
                                            .append(") (Timer ID: ").append(timer.getId()).append(" )").append(i + 1 == timers.size() ? "" : "\n");
                                }
                                returnMessage = new TextMessage(message.toString());
                            } else if (args[2].equalsIgnoreCase("running") | args[2].equalsIgnoreCase("r")) {
                                StringBuilder message = new StringBuilder("Running Timers for bot instance: " + runningTimers.size() + " running." +
                                        "\n=======================\n");
                                for (int i = 0; i < runningTimers.size(); i++) {
                                    TimerInstance timer = runningTimers.get(i);
                                    List<Long> times = Utils.getFormattedTimeDiffrence(timer.getTimeLeft(), 0, 7);
                                    message.append(i + 1).append(". ").append(timer.getName()).append(" Time Left: ").append(timer.getTimeLeft() < 0 ? "-" : "")
                                            .append(times.get(0) > 0 ? (times.get(0) > 1 ? (times.get(0) + " years ") : (times.get(0) + " year ")) : "")
                                            .append(times.get(1) > 0 ? (times.get(1) > 1 ? (times.get(1) + " months ") : (times.get(1) + " month ")) : "")
                                            .append(times.get(2) > 0 ? (times.get(2) > 1 ? (times.get(2) + " weeks ") : (times.get(2) + " week ")) : "")
                                            .append(times.get(3) > 0 ? (times.get(3) > 1 ? (times.get(3) + " days ") : (times.get(3) + " day ")) : "")
                                            .append(times.get(4) > 0 ? (times.get(4) > 1 ? (times.get(4) + " hours ") : (times.get(4) + " hour ")) : "")
                                            .append(times.get(5) > 0 ? (times.get(5) > 1 ? (times.get(5) + " minutes ") : (times.get(5) + " minute ")) : "")
                                            .append(times.get(6) > 0 ? (times.get(6) > 1 ? (times.get(6) + " seconds") : (times.get(6) + " second")) : "")
                                            .append(" (Timer Instance ID: ").append(timer.getId()).append(" )").append(i + 1 == runningTimers.size() ? "" : "\n");;
                                }
                                returnMessage = new TextMessage(message.toString());
                            }
                        }
                    } else {
                        returnMessage = new TextMessage("Specify a subcommand!");
                    }
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

//    public void SendMessage(String message){
//        TextMessage msg = new TextMessage(message);
//        PushMessage pushMsg = new PushMessage(, msg)
//    }
}
