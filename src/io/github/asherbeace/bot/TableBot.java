package io.github.asherbeace.bot;

import io.github.asherbeace.bot.events.TestEvent;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import javax.security.auth.login.LoginException;

public class TableBot {
    public static JDA jda;
    public static final String PREFIX = "\\";

    public static void main(String[] args) throws LoginException {
        //Put token here
        jda = new JDABuilder(AccountType.BOT).setToken("")
                .build();

        jda.addEventListener(new TestEvent());
    }
}
