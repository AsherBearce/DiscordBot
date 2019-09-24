package main.java.io.github.asherbeace.bot.botsettings;

import java.util.List;

public class BotSettings{
    private String botToken;
    private List<ServerSettings> servers;

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

    public List<ServerSettings> getServers() {
        return servers;
    }

    public void setServers(List<ServerSettings> servers) {
        this.servers = servers;
    }
}