package me.trumbo.valueevent.config.data;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public class GuiData {
    private int size;
    private String title;
    private String fillerMaterial;
    private String fillerName;
    private List<String> itemLore;
    private String infoMaterial;
    private String infoName;
    private List<String> infoLore;
    private int infoSlot;

    public void load(FileConfiguration config) {
        ConfigurationSection guiSection = config.getConfigurationSection("library");

        size = guiSection.getInt("size");
        title = guiSection.getString("title");
        fillerMaterial = guiSection.getString("filler-material");
        fillerName = guiSection.getString("filler-name");
        itemLore = guiSection.getStringList("item-lore");
        infoMaterial = guiSection.getString("info-material");
        infoName = guiSection.getString("info-name");
        infoLore = guiSection.getStringList("info-lore");
        infoSlot = guiSection.getInt("info-slot");
    }
}