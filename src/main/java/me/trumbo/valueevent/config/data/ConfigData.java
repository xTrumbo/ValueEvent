package me.trumbo.valueevent.config.data;

import lombok.Getter;
import me.trumbo.valueevent.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public class ConfigData {
    private MessageUtils.FormatType format;
    private String noPermissionMessage;
    private List<String> helpMessages;
    private String successMessage;
    private String alreadyJoinedMessage;
    private String notActiveMessage;
    private String maxPlayersMessage;
    private String consoleMessage;
    private String eventAlreadyStartedMessage;
    private String reloadMessage;
    private String delayMessage;

    public void load(FileConfiguration config) {
        String formatStr = config.getString("format", "hex");
        format = formatStr.equalsIgnoreCase("hex") ? MessageUtils.FormatType.HEX : MessageUtils.FormatType.MINIMESSAGE;

        MessageUtils.setFormat(format);

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            noPermissionMessage = messagesSection.getString("no-perm");
            helpMessages = messagesSection.getStringList("help");
            successMessage = messagesSection.getString("success");
            alreadyJoinedMessage = messagesSection.getString("already-joined");
            notActiveMessage = messagesSection.getString("not-active");
            maxPlayersMessage = messagesSection.getString("max-players");
            consoleMessage = messagesSection.getString("console");
            eventAlreadyStartedMessage = messagesSection.getString("event-already-started");
            reloadMessage = messagesSection.getString("reload");
            delayMessage = messagesSection.getString("delay");
        }
    }
}