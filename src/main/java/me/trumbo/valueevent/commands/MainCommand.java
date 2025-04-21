package me.trumbo.valueevent.commands;

import me.trumbo.valueevent.ValueEvent;
import me.trumbo.valueevent.gui.LibraryGUI;
import me.trumbo.valueevent.managers.EventManager;
import me.trumbo.valueevent.utils.MessageUtils;
import me.trumbo.valueevent.utils.SoundUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    private final ValueEvent main;
    private final EventManager eventManager;

    public MainCommand(ValueEvent main) {
        this.main = main;
        this.eventManager = main.getEventManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            for (String message : main.getPluginConfig().getConfigData().getHelpMessages()) {
                MessageUtils.sendMessage(sender, message.replace("%label%", label));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("delay")) {

            Player player = (Player) sender;
            String message = eventManager.getRemainingTimeMessage();
            if (message == null) {
                MessageUtils.sendMessage(player, main.getPluginConfig().getConfigData().getNotActiveMessage());
            } else {
                MessageUtils.sendMessage(player, message);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("library")) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendMessage(sender, main.getPluginConfig().getConfigData().getConsoleMessage());
                return true;
            }
            Player player = (Player) sender;
            new LibraryGUI(main).open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendMessage(sender, main.getPluginConfig().getConfigData().getConsoleMessage());
                return true;
            }

            Player player = (Player) sender;
            if (!eventManager.isEventActive()) {
                MessageUtils.sendMessage(player, main.getPluginConfig().getConfigData().getNotActiveMessage());
                return true;
            }

            if (eventManager.isPlayerInEvent(player)) {
                MessageUtils.sendMessage(player, main.getPluginConfig().getConfigData().getAlreadyJoinedMessage());
                return true;
            }

            if (!eventManager.canJoin(player)) {
                MessageUtils.sendMessage(player, main.getPluginConfig().getConfigData().getMaxPlayersMessage());
                return true;
            }

            SoundUtils.playSound(main, player, "event_join");

            eventManager.addPlayer(player);
            MessageUtils.sendMessage(player, main.getPluginConfig().getConfigData().getSuccessMessage());
            return true;
        }

        if (!sender.hasPermission("valueevent.admin")) {
            MessageUtils.sendMessage(sender, main.getPluginConfig().getConfigData().getNoPermissionMessage());
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (eventManager.isEventActive()) {
                MessageUtils.sendMessage(sender, main.getPluginConfig().getConfigData().getEventAlreadyStartedMessage());
                return true;
            }
            eventManager.stopTasks();
            eventManager.startEventTask();
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!eventManager.isEventActive()) {
                MessageUtils.sendMessage(sender, main.getPluginConfig().getConfigData().getNotActiveMessage());
                return true;
            }
            eventManager.endEvent();
        }

        if (args[0].equalsIgnoreCase("reload")) {
            main.getPluginConfig().reload();
            MessageUtils.sendMessage(sender, main.getPluginConfig().getConfigData().getReloadMessage());
            return true;
        }

        return true;
    }
}