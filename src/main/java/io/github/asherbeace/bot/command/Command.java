package main.java.io.github.asherbeace.bot.command;

import main.java.io.github.asherbeace.bot.TableBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public enum Command {
    HELP( //Lists all commands and their descriptions
            (bot, channel, invoker, args) -> {
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
            (bot, channel, invoker, args) -> {
                String msg = "";

                for (int i = 0; i < args.length; i++){
                    msg += args[i] + " ";
                }

                channel.sendMessage(msg).queue();
            }, "A simple test command that repeats whatever the user has said.", true,
            CommandLevel.NORMAL, 50
    ),
    DISABLE(
            (bot, channel, invoker, args) -> {
                String user = args[0];
                final int timeOut = Integer.parseInt(args[1]);
                List<Member> members = channel.getGuild().getMembers();

                for (Member member : members){
                    if (member.getNickname().matches(user)){
                        TableBot.DISALLOWED_USERS.add(member.getUser());
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
    ENABLE((bot, channel, invoker, args) -> {
        //TODO implement me
    }, "Enables command usage for a user.", true, CommandLevel.ADMIN, 100);

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

    public void execute(JDA bot, TextChannel channel, User invoker, String[] args){
        if (System.currentTimeMillis() - lastCall >= TableBot.TIME_COMMAND_REFRESH || callCount < callLimit){
            if (callCount >= callLimit){
                callCount = 0;
            }
            executor.execute(bot, channel, invoker, args);
            lastCall = System.currentTimeMillis();
            callCount++;
        } else {
            TableBot.sendErrorMessage(channel,
                    "You are trying to call this command too many times, you must wait 5 minutes");
        }
    }
}
