package io.github.asherbeace.bot.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public interface CommandExecutor {
    void execute(JDA bot, TextChannel channel, User invoker, Message msg, String[] args);
}
