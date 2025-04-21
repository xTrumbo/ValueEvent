package me.trumbo.valueevent.utils;

import me.trumbo.valueevent.ValueEvent;
import me.trumbo.valueevent.config.data.SoundData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtils {

    public static void playSound(ValueEvent main, Player player, String soundKey) {
        SoundData.SoundConfig config = main.getPluginConfig().getSoundData().getSound(soundKey);
        if (config == null) return;

        Sound sound = Sound.valueOf(config.getSoundName().toUpperCase());
        player.playSound(player.getLocation(), sound, config.getVolume(), config.getPitch());
    }

    public static void playSoundToAll(ValueEvent main, String soundKey) {
        SoundData.SoundConfig config = main.getPluginConfig().getSoundData().getSound(soundKey);
        if (config == null) return;

        Sound sound = Sound.valueOf(config.getSoundName().toUpperCase());
        for (Player player : main.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, config.getVolume(), config.getPitch());
        }
    }
}

