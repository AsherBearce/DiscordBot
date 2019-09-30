package io.github.asherbeace.bot.command;

import io.github.asherbeace.bot.TableBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.util.List;

public enum Command {
    HELP( //Lists all commands and their descriptions
            (bot, channel, invoker, msg, args) -> {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Command list");

                for (Command c : Command.values()){
                    eb.addField(c.name(), c.description, false);
                }

                channel.sendMessage(eb.build()).queue();

            }, "A command that displays all available commands.", false, CommandLevel.NORMAL,
            1
    ),
    SAY( //Test command. Just repeats back whatever was said to it after the command.
            (bot, channel, invoker, msg, args) -> {
                String msgBack = args[0];

                channel.sendMessage(msgBack).queue();
            }, "A simple test command that repeats whatever the user has said.", true,
            CommandLevel.NORMAL, 50
    ),
    DISABLE(
            (bot, channel, invoker, msg, args) -> {
                String user = args[0];
                final int timeOut = Integer.parseInt(args[1]);
                List<Member> members = channel.getGuild().getMembers();

                for (Member member : members){
                    if (member.getEffectiveName().contentEquals(user)){
                        TableBot.DISALLOWED_USERS.add(member.getUser());
                        msg.addReaction("✔").queue();
                        new Thread(() -> {
                            try {
                                Thread.sleep(timeOut * 1000);
                                if (TableBot.DISALLOWED_USERS.contains(member.getUser())) {
                                    TableBot.DISALLOWED_USERS.remove(member.getUser());
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }

                //Find all users whose name matches via regex
            }, "Disables command usage for a user for a given time.", true,
            CommandLevel.ADMIN, 100
    ),
    ENABLE((bot, channel, invoker, msg, args) -> {
        String user = args[0];
        List<Member> members = channel.getGuild().getMembers();

        for (Member member : members){
            if (member.getEffectiveName().contentEquals(user)){
                TableBot.DISALLOWED_USERS.remove(member.getUser());
                msg.addReaction("✔").queue();
            }
        }
    }, "Enables command usage for a user.", true, CommandLevel.ADMIN, 100),
    GIVECOMMAND((bot, channel, invoker, msg, args) -> {
        String roleName = args[0];
        String commandName = args[1];
        String serverName = channel.getGuild().getName();

        TableBot.addCommandToRole(roleName, commandName, serverName);
        msg.addReaction("✔").queue();

        new Thread(() -> {
            try {
                TableBot.writeSettings();
            }
            catch (IOException e){
                System.out.println("OOPS!");
            }
        }).start();
    }, "Adds a command permission to a given role.", true, CommandLevel.ADMIN, 100),
    TAKECOMMAND((bot, channel, invoker, msg, args) -> {
        String roleName = args[0];
        String commandName = args[1];
        String serverName = channel.getGuild().getName();

        TableBot.removeCommandFromRole(roleName, commandName, serverName);

        new Thread(() -> {
            try {
                TableBot.writeSettings();
            }
            catch (IOException e){
                System.out.println("OOPS!");
            }
        }).start();
    }, "Takes a command permission away from a given role.", true, CommandLevel.ADMIN, 100),
    JOIN((bot, channel, invoker, msg, args) -> {
        AudioManager audioManager = channel.getGuild().getAudioManager();

        if (audioManager.isConnected()){
            channel.sendMessage("I'm already connected!").queue();
            return;
        }

        GuildVoiceState memberState = channel.getGuild().getMember(invoker).getVoiceState();

        if (!memberState.inVoiceChannel()){
            channel.sendMessage("Please join a voice channel first.").queue();
            return;
        }

        VoiceChannel vc = memberState.getChannel();
        Member self = channel.getGuild().getSelfMember();

        if (!self.hasPermission(vc, Permission.VOICE_CONNECT)){
            channel.sendMessage("I am missing a permission to join your voice channel!").queue();
            return;
        }

        audioManager.openAudioConnection(vc);
        msg.addReaction("✔").queue();

    }, "Joins the bot to whatever voice chat channel the invoker is in.", false, CommandLevel.NORMAL, 100),
    LEAVE((bot, channel, invoker, msg, args) -> {
        AudioManager audioManager = channel.getGuild().getAudioManager();

        if (!audioManager.isConnected()){
            channel.sendMessage("I'm not connected to a voice channel!").queue();
            return;
        }

        VoiceChannel vc = audioManager.getConnectedChannel();

        if (!vc.getMembers().contains(channel.getGuild().getMember(invoker))){
            channel.sendMessage("You have to be in the same voice channel to use this!").queue();
            return;
        }

        audioManager.closeAudioConnection();
        msg.addReaction("✔").queue();
    }, "Leaves whatever voice chat it is currently in.", false, CommandLevel.NORMAL, 100);

    private int callCount = 0;
    private long lastCall;
    private CommandExecutor executor;
    public final String description;
    public final boolean requiresArgs;
    public final CommandLevel authLevel;
    public final int callLimit;

    private Command(CommandExecutor command, String description, boolean requiresArgs, CommandLevel authLevel,
                    int executeLimit){
        executor = command;
        this.description = description;
        this.requiresArgs = requiresArgs;
        this.authLevel = authLevel;
        this.callLimit = executeLimit;
    }

    public void execute(JDA bot, TextChannel channel, User invoker, Message msg, String[] args){
        if (System.currentTimeMillis() - lastCall >= TableBot.TIME_COMMAND_REFRESH || callCount < callLimit){
            if (callCount >= callLimit){
                callCount = 0;
            }
            executor.execute(bot, channel, invoker, msg, args);
            lastCall = System.currentTimeMillis();
            callCount++;
        } else {
            TableBot.sendErrorMessage(channel,
                    "You are trying to call this command too many times, you must wait 5 minutes");
        }
    }
}
