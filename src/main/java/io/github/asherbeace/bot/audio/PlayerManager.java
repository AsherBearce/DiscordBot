package io.github.asherbeace.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild){
        long guildId = guild.getIdLong();
        GuildMusicManager manager = musicManagers.get(guildId);

        if (manager == null){
            manager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, manager);
        }

        guild.getAudioManager().setSendingHandler(manager.getSendHandler());

        return manager;
    }

    public void loadAndPlay(TextChannel channel, String trackURL){
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
        playerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                channel.sendMessage("Adding to queue " + audioTrack.getInfo().title).queue();

                play(musicManager, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if (firstTrack == null){
                    firstTrack = audioPlaylist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + audioPlaylist.getName()).queue();

                play(musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Could not find URL.").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    private void play(GuildMusicManager musicManager, AudioTrack track){
        musicManager.scheduler.queue(track);
    }

    public void pause(Guild guild){
        getGuildMusicManager(guild).player.setPaused(true);
    }

    public void unpause(Guild guild){
        getGuildMusicManager(guild).player.setPaused(false);
    }

    public void skip(Guild guild){
        getGuildMusicManager(guild).scheduler.nextTrack();
    }

    public void setVolume(Guild guild, int volume){
        getGuildMusicManager(guild).player.setVolume(volume);
    }

    public void stop(Guild guild){
        getGuildMusicManager(guild).player.stopTrack();
    }

    public static synchronized PlayerManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
