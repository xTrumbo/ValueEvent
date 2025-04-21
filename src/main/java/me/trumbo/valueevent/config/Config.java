package me.trumbo.valueevent.config;

import lombok.Getter;
import me.trumbo.valueevent.ValueEvent;
import me.trumbo.valueevent.config.data.ConfigData;
import me.trumbo.valueevent.config.data.EventData;
import me.trumbo.valueevent.config.data.GuiData;
import me.trumbo.valueevent.config.data.SoundData;
import me.trumbo.valueevent.config.data.TranslationData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class Config {

    private ValueEvent main;
    private File configFile;
    private File eventFile;
    private File guiFile;
    private File translationFile;
    private FileConfiguration config;
    private FileConfiguration event;
    private FileConfiguration gui;
    private FileConfiguration translation;

    private ConfigData configData;
    private EventData eventData;
    private SoundData soundData;
    private GuiData guiData;
    private TranslationData translationData;

    public Config(ValueEvent main) {
        this.main = main;
        this.configData = new ConfigData();
        this.eventData = new EventData();
        this.soundData = new SoundData();
        this.guiData = new GuiData();
        this.translationData = new TranslationData();

        createFiles();
    }

    public void createFiles() {
        if (!main.getDataFolder().exists()) {
            main.getDataFolder().mkdir();
        }

        configFile = new File(main.getDataFolder(), "config.yml");
        eventFile = new File(main.getDataFolder(), "event.yml");
        guiFile = new File(main.getDataFolder(), "gui.yml");
        translationFile = new File(main.getDataFolder(), "translations.yml");

        if (!configFile.exists()) {
            main.saveResource("config.yml", false);
        }
        if (!eventFile.exists()) {
            main.saveResource("event.yml", false);
        }
        if (!guiFile.exists()) {
            main.saveResource("gui.yml", false);
        }
        if (!translationFile.exists()) {
            main.saveResource("translations.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        event = YamlConfiguration.loadConfiguration(eventFile);
        gui = YamlConfiguration.loadConfiguration(guiFile);
        translation = YamlConfiguration.loadConfiguration(translationFile);

        configData.load(config);
        eventData.load(event);
        soundData.load(event);
        guiData.load(gui);
        translationData.load(translation);
    }

    public void reload() {
        createFiles();
        main.getEventManager().endEvent();
    }
}