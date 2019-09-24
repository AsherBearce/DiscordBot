package main.java.io.github.asherbeace.bot.botsettings;

import main.java.io.github.asherbeace.bot.command.Command;
import main.java.io.github.asherbeace.bot.command.CommandLevel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerSettings {
    private HashMap<Role, List<Command>> roleCommands;
    private Member serverOwner;
    private Guild thisServer;

    public ServerSettings(){
        roleCommands = new HashMap<>();
    }

    public HashMap<Role, List<Command>> getRoleCommands() {
        return roleCommands;
    }

    public Member getServerOwner() {
        return serverOwner;
    }

    public void setServerOwner(Member serverOwner) {
        this.serverOwner = serverOwner;
    }

    public Guild getThisServer() {
        return thisServer;
    }

    public void setThisServer(Guild thisServer) {
        this.thisServer = thisServer;
    }

    public void addCommand(Role role, Command command){
        roleCommands.get(role).add(command);
    }

    public void removeCommand(Role role, Command command){
        roleCommands.get(role).remove(command);
    }

    public void setUp(Guild server){
        for (Role role : server.getRoles()){
            LinkedList<Command> defaultCommands = new LinkedList<>();

            for (Command command : Command.values()) {

                if (role.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    defaultCommands.add(command);
                } else {
                    if (command.authLevel == CommandLevel.NORMAL){
                        defaultCommands.add(command);
                    }
                }
            }

            roleCommands.put(role, defaultCommands);
        }
    }
}
