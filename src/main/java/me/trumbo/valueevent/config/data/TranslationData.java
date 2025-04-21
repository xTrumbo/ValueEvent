package me.trumbo.valueevent.config.data;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TranslationData {
    private Map<String, String> translations;

    public TranslationData() {
        translations = new HashMap<>();
    }

    public void load(FileConfiguration config) {
        translations.clear();
        ConfigurationSection translationSection = config.getConfigurationSection("translations");
        if (translationSection != null) {
            for (String key : translationSection.getKeys(false)) {
                translations.put(key.toUpperCase(), translationSection.getString(key));
            }
        }
    }

    public String getTranslation(String materialName) {
        return translations.get(materialName.toUpperCase());
    }
}