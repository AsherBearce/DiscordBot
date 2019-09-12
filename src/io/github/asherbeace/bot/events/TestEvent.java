package io.github.asherbeace.bot.events;

import io.github.asherbeace.bot.TableBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TestEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args[0].equals(TableBot.PREFIX)){
            event.getChannel().sendMessage("COMMAND TEST!").queue();
        }
    }
}
