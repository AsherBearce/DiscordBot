package io.github.asherbeace.bot.botsettings;

import io.github.asherbeace.bot.command.Command;
import io.github.asherbeace.bot.command.CommandLevel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerSettings {
    private HashMap<String, List<String>> roleCommands;
    private String name;

    public ServerSettings(){
        roleCommands = new HashMap<String, List<String>>();
    }

    public HashMap<String, List<String>> getRoleCommands() {
        return roleCommands;
    }

    public void setRoleCommands(HashMap<String, List<String>> commands){
        roleCommands = commands;
    }

    public void addCommand(String role, String command){
        roleCommands.get(role).add(command);
    }

    public void removeCommand(String role, String command){
        roleCommands.get(role).remove(command);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setUp(Guild server){
        name = server.getName();
        for (Role role : server.getRoles()){
            LinkedList<String> defaultCommands = new LinkedList<String>();

            for (Command command : Command.values()) {

                if (role.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    defaultCommands.add(command.name());
                } else {
                    if (command.authLevel == CommandLevel.NORMAL){
                        defaultCommands.add(command.name());
                    }
                }
            }

            roleCommands.put(role.getName(), defaultCommands);
        }
    }
}
