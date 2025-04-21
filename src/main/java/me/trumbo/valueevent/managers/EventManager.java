package me.trumbo.valueevent.managers;

import me.trumbo.valueevent.ValueEvent;
import me.trumbo.valueevent.utils.MessageUtils;
import me.trumbo.valueevent.utils.RandomUtils;
import me.trumbo.valueevent.utils.SoundUtils;
import me.trumbo.valueevent.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventManager {
    private final ValueEvent main;
    private BukkitTask delayTask;
    private BukkitTask eventTask;
    private boolean isEventActive = false;
    private long delayDuration;
    private long eventDuration;
    private long delayStartTime;
    private long eventStartTime;
    private final Map<UUID, Integer> eventPlayers = new HashMap<>();

    public EventManager(ValueEvent main) {
        this.main = main;
        startDelayTask();
    }

    public boolean isEventActive() {
        return isEventActive;
    }

    public boolean canJoin(Player player) {
        return isEventActive &&
                !eventPlayers.containsKey(player.getUniqueId()) &&
                eventPlayers.size() < main.getPluginConfig().getEventData().getMaxPlayers();
    }

    public void addPlayer(Player player) {
        if (!isEventActive) return;
        eventPlayers.put(player.getUniqueId(), 0);
    }

    public void removePlayer(Player player) {
        eventPlayers.remove(player.getUniqueId());
    } //Заглушка для адм.команд

    public boolean isPlayerInEvent(Player player) {
        return eventPlayers.containsKey(player.getUniqueId());
    }

    public int calculateInventoryValue(Player player) {
        int totalValue = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                int price = main.getPluginConfig().getEventData().getItemPrice(item.getType());
                totalValue += price * item.getAmount();
            }
        }
        return totalValue;
    }

    private void startDelayTask() {
        int delaySeconds = main.getPluginConfig().getEventData().getStartDelay();
        delayDuration = delaySeconds * 20;
        delayTask = main.getServer().getScheduler().runTaskLater(main, this::startEventTask, delayDuration);
        delayStartTime = main.getServer().getCurrentTick();
    }

    public void startEventTask() {
        isEventActive = true;
        eventPlayers.clear();
        for (String message : main.getPluginConfig().getEventData().getStartMessage()) {
            MessageUtils.sendMessageToAll(message);
        }
        int eventSeconds = main.getPluginConfig().getEventData().getDuration();
        eventDuration = eventSeconds * 20;
        eventTask = main.getServer().getScheduler().runTaskLater(main, this::endEvent, eventDuration);
        SoundUtils.playSoundToAll(main, "event_start");
        eventStartTime = main.getServer().getCurrentTick();
    }

    public void stopTasks() {
        if (eventTask != null) {
            eventTask.cancel();
            eventTask = null;
        }
        if (delayTask != null) {
            delayTask.cancel();
            delayTask = null;
            isEventActive = false;
        }
    }

    public String getRemainingTimeMessage() {
        long currentTick = main.getServer().getCurrentTick();

        if (isEventActive && eventTask != null) {
            long ticksRemaining = (eventStartTime + eventDuration) - currentTick;
            if (ticksRemaining > 0) {
                TimeUtils.TimeRemaining time = TimeUtils.ticksToTime(ticksRemaining);
                return TimeUtils.formatTime(
                        main.getPluginConfig().getConfigData().getDelayMessage(),
                        time,
                        "%event_status%", "конца"
                );
            }
        } else if (delayTask != null) {
            long ticksRemaining = (delayStartTime + delayDuration) - currentTick;
            if (ticksRemaining > 0) {
                TimeUtils.TimeRemaining time = TimeUtils.ticksToTime(ticksRemaining);
                return TimeUtils.formatTime(
                        main.getPluginConfig().getConfigData().getDelayMessage(),
                        time,
                        "%event_status%", "начала"
                );
            }
        }

        return null;
    }

    public void endEvent() {

        isEventActive = false;
        SoundUtils.playSoundToAll(main, "event_end");
        for (UUID uuid : eventPlayers.keySet()) {
            Player player = main.getServer().getPlayer(uuid);
            if (player != null) {
                int totalValue = calculateInventoryValue(player);
                eventPlayers.put(uuid, totalValue);
                player.getInventory().clear();
            }
        }

        boolean hasValidScores = eventPlayers.values().stream().anyMatch(value -> value > 0);
        if (eventPlayers.isEmpty() || !hasValidScores) {
            for (String message : main.getPluginConfig().getEventData().getEmptyEventMessage()) {
                MessageUtils.sendMessageToAll(message);
            }
            stopTasks();
            eventPlayers.clear();
            startDelayTask();
            return;
        }

        List<Map.Entry<UUID, Integer>> sortedPlayers = eventPlayers.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < sortedPlayers.size(); i++) {
            UUID uuid = sortedPlayers.get(i).getKey();
            Player player = main.getServer().getPlayer(uuid);
            if (player != null) {
                int place = i + 1;
                int value = sortedPlayers.get(i).getValue();
                for (String message : main.getPluginConfig().getEventData().getPlayerResultMessage()) {
                    String formattedMessage = message
                            .replace("%place%", String.valueOf(place))
                            .replace("%value%", String.valueOf(value));
                    MessageUtils.sendMessage(player, formattedMessage);
                }
            }
        }

        Map<Integer, List<String>> rewards = main.getPluginConfig().getEventData().getTopRewards();
        for (int i = 0; i < sortedPlayers.size() && i < rewards.size(); i++) {
            UUID uuid = sortedPlayers.get(i).getKey();
            Player winner = main.getServer().getPlayer(uuid);
            if (winner != null) {
                int place = i + 1;
                List<String> commands = rewards.get(place);
                if (commands != null) {
                    for (String command : commands) {
                        String formattedCommand = processReward(command, winner.getName());
                        main.getServer().dispatchCommand(main.getServer().getConsoleSender(), formattedCommand);
                    }
                }
            }
        }

        for (String message : main.getPluginConfig().getEventData().getEventEndMessage()) {
            MessageUtils.sendMessageToAll(message);
        }

        stopTasks();
        eventPlayers.clear();
        startDelayTask();

    }

    private String processReward(String reward, String winnerName) {
        String command = reward.replace("%winner%", winnerName);

        String[] parts = command.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].contains("-")) {
                parts[i] = String.valueOf(RandomUtils.parseRandomRange(parts[i]));
            }
        }
        return String.join(" ", parts);
    }

}