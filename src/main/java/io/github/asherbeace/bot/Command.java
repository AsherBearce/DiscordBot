package main.java.io.github.asherbeace.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public enum Command {
    HELP( //Lists all commands and their descriptions
            (bot, channel, invoker, args) -> {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Comamnd list");

                for (Command c : Command.values()){
                    eb.addField(c.name(), c.description, false);
                }

                channel.sendMessage(eb.build()).queue();

            }, "A command that displays all available commands.", false
    ),
    SAY( //Test command. Just repeats back whatever was said to it after the command.
            (bot, channel, invoker, args) -> {
                String msg = "";

                for (int i = 0; i < args.length; i++){
                    msg += args[i] + " ";
                }

                channel.sendMessage(msg).queue();
            }, "A simple test command that repeats whatever the user has said.", true
    );

    private CommandExecutor executor;
    public final String description;
    public final boolean requiresArgs;

    private Command(CommandExecutor command, String description, boolean requiresArgs){
        executor = command;
        this.description = description;
        this.requiresArgs = requiresArgs;
    }

    public void execute(JDA bot, TextChannel channel, User invoker, String[] args){
        executor.execute(bot, channel, invoker, args);
    }
}
