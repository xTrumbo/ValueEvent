package me.trumbo.valueevent.config.data;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Getter
public class EventData {
    private int startDelay;
    private int duration;
    private List<String> startMessage;
    private List<String> eventEndMessage;
    private int maxPlayers;
    private int defaultPrice;
    private Map<Material, Integer> customPrices;
    private Map<Integer, List<String>> topRewards;
    private List<String> playerResultMessage;
    private List<String> emptyEventMessage;

    public EventData() {
        customPrices = new HashMap<>();
        topRewards = new HashMap<>();
    }

    public void load(FileConfiguration config) {
        ConfigurationSection eventSection = config.getConfigurationSection("event");

        startDelay = eventSection.getInt("start-delay", 15);
        duration = eventSection.getInt("duration", 35);
        startMessage = eventSection.getStringList("event-start");
        eventEndMessage = eventSection.getStringList("event-end");
        maxPlayers = eventSection.getInt("max-players", 20);
        playerResultMessage = eventSection.getStringList("player-result");
        emptyEventMessage = eventSection.getStringList("empty-event");

        defaultPrice = config.getInt("items.default-price", 1);
        ConfigurationSection customPriceSection = config.getConfigurationSection("items.custom-price");
        if (customPriceSection != null) {
            for (String key : customPriceSection.getKeys(false)) {
                String itemName = customPriceSection.getString(key + ".item");
                int price = customPriceSection.getInt(key + ".price");
                    Material material = Material.valueOf(itemName.toUpperCase());
                    customPrices.put(material, price);
            }
        }

        ConfigurationSection rewardsSection = config.getConfigurationSection("top-rewards");
        if (rewardsSection != null) {
            for (String place : rewardsSection.getKeys(false)) {
                    int position = Integer.parseInt(place);
                    List<String> commands = rewardsSection.getStringList(place);
                    topRewards.put(position, commands);
            }
        }
    }

    public int getItemPrice(Material material) {
        return customPrices.getOrDefault(material, defaultPrice);
    }
}