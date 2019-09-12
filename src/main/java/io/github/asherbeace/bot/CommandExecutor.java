package main.java.io.github.asherbeace.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public interface CommandExecutor {
    void execute(JDA bot, TextChannel channel, User invoker, String[] args);
}
