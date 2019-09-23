package main.java.io.github.asherbeace.bot;

import main.java.io.github.asherbeace.bot.command.Command;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class TableBot {
    public static JDA jda;
    public static final String PREFIX = "!t";
    public static final long TIME_COMMAND_REFRESH = 300000;
    public static final String FILE_NAME = "bot.xml";
    public static final List<User> DISALLOWED_USERS = new LinkedList<>();
    private static BotSettings settings;
    //TODO Add the ability to ban members from using the commands for a short time. Use a List<User> for this.
    //TODO Set up multi-stage commands.

    public static void main(String[] args){
        File tokenFile = new File(FILE_NAME);
        if (tokenFile.exists()){
            try {
                XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(FILE_NAME)));
                settings = (BotSettings) decoder.readObject();

                jda = new JDABuilder(AccountType.BOT).setToken(settings.getBotToken()).build();
            } catch (Exception e){
                System.out.println("There was a problem retrieving your bots token! Performing first-time setup.");
                firstTimeSetup();
            }
        } else {
            firstTimeSetup();
        }

        jda.addEventListener(new EventHandler());

    }

    private static void firstTimeSetup(){
        boolean loginSuccess = false;

        while (!loginSuccess) {
            Scanner input = new Scanner(System.in);
            System.out.println("Please enter the valid token of your bot: ");
            String s = input.nextLine();

            try {
                //Put token here
                jda = new JDABuilder(AccountType.BOT).setToken(s).build();
                loginSuccess = true;

                settings = new BotSettings(s);

                XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(FILE_NAME)));
                encoder.writeObject(settings);
                encoder.close();
            } catch (LoginException e) {
                System.out.println("An error occurred while logging in. It may be that the token entered was invalid.");
            } catch (IOException e){
                System.out.println("There was an error storing your bots token. Please retry.");
            }
        }


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
            boolean canUse = true;

            for (User user : DISALLOWED_USERS){
                if (event.getMember().getUser().equals(user)){
                    canUse = false;
                    break;
                }
            }

            if (canUse) {
                parseCommand(event);
            }
        }

        public void onGuildMemberLeave(GuildMemberLeaveEvent event){
            String name = event.getUser().getName();
            event.getGuild().getDefaultChannel().sendMessage("Oh no! " + name + " has left the server!").queue();
        }
    }

    public static class BotSettings{
        private String botToken;

        public BotSettings(){

        }

        public BotSettings(String botToken){
            this.botToken = botToken;
        }

        public String getBotToken() {
            return botToken;
        }

        public void setBotToken(String botToken) {
            this.botToken = botToken;
        }
    }
}