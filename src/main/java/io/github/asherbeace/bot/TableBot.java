package main.java.io.github.asherbeace.bot;

import main.java.io.github.asherbeace.bot.command.Command;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TableBot {
    public static JDA jda;
    public static final String PREFIX = "!t";
    public static final long TIME_COMMAND_REFRESH = 300000;
    //TODO Add a file write to keep the bot token. 
    //TODO Add the ability to ban members from using the commands for a short time. Use a List<User> for this.
    //TODO Set up multi-stage commands.

    public static void main(String[] args) throws LoginException {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the token of your bot: ");
        String s = input.nextLine();

        //Put token here
        jda = new JDABuilder(AccountType.BOT).setToken(s).build();

        jda.addEventListener(new EventHandler());


    }

    public static void parseCommand(GuildMessageReceivedEvent event){
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length < 2){
            return;
        }
        if (args[0].equals(PREFIX)){
            Command command = null;

            for (Command c : Command.values()){
                if (args[1].equalsIgnoreCase(c.name())){
                    command = c;
                    break;
                }
            }

            if (command != null){
                String[] commandArgs = {};

                if (command.requiresArgs){
                    commandArgs = Arrays.copyOfRange(args, 2, args.length);
                }

                command.execute(jda, event.getChannel(), event.getAuthor(), commandArgs);
            }
            else {
                sendErrorMessage(event.getChannel(), "Unknown command, try typing \"!t help\" for a list of commands.");
            }

        }
    }

    public static void sendErrorMessage(TextChannel channel, String msg){
        channel.sendMessage(msg).queue();
    }

    public static class EventHandler extends ListenerAdapter{
        public void onGuildMessageReceived(GuildMessageReceivedEvent event){
            if (!event.getAuthor().getId().equalsIgnoreCase("135829757324558336")){
                parseCommand(event);
            }
        }

        public void onGuildMemberLeave(GuildMemberLeaveEvent event){
            String name = event.getUser().getName();
            event.getGuild().getDefaultChannel().sendMessage("Oh no! " + name + " has left the server!").queue();
        }
    }
}
