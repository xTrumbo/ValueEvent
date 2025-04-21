package me.trumbo.valueevent.commands;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.trumbo.valueevent.ValueEvent;

public class MainCompleter implements TabCompleter {

    private final ValueEvent main;

    public MainCompleter(ValueEvent main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("join");
            completions.add("delay");
            completions.add("library");

            if (sender.hasPermission("valueevent.admin")) {
                completions.add("start");
                completions.add("stop");
                completions.add("reload");
            }
        }

        return completions;
    }
}