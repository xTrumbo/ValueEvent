package me.trumbo.valueevent;

import fr.mrmicky.fastinv.FastInvManager;
import me.trumbo.valueevent.bstats.Metrics;
import me.trumbo.valueevent.commands.MainCommand;
import me.trumbo.valueevent.commands.MainCompleter;
import me.trumbo.valueevent.config.Config;
import me.trumbo.valueevent.managers.EventManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ValueEvent extends JavaPlugin {

    private Config config;
    private EventManager eventManager;

    @Override
    public void onEnable() {

        new Metrics(this, 25569);

        config = new Config(this);
        eventManager = new EventManager(this);

        FastInvManager.register(this);

        getCommand("ve").setExecutor(new MainCommand(this));
        getCommand("ve").setTabCompleter(new MainCompleter(this));
    }

    public Config getPluginConfig() {
        return config;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
