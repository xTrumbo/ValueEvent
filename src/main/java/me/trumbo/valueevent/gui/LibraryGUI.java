package me.trumbo.valueevent.gui;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import me.trumbo.valueevent.ValueEvent;
import me.trumbo.valueevent.utils.MessageUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.stream.Collectors;

public class LibraryGUI extends FastInv {
    public LibraryGUI(ValueEvent main) {
        super(main.getPluginConfig().getGuiData().getSize(),
                (String) MessageUtils.format(main.getPluginConfig().getGuiData().getTitle()));

        Material fillerMaterial = Material.valueOf(main.getPluginConfig().getGuiData().getFillerMaterial());
        String fillerName = (String) MessageUtils.format(main.getPluginConfig().getGuiData().getFillerName());

        ItemBuilder fillerBuilder = new ItemBuilder(fillerMaterial)
                .name(fillerName);

        setItems(getBorders(), fillerBuilder.build());

        Material infoMaterial = Material.valueOf(main.getPluginConfig().getGuiData().getInfoMaterial());
        String infoName = (String) MessageUtils.format(main.getPluginConfig().getGuiData().getInfoName());
        List<String> infoLore = main.getPluginConfig().getGuiData().getInfoLore().stream()
                .map(line -> (String) MessageUtils.format(line))
                .collect(Collectors.toList());
        int infoSlot = main.getPluginConfig().getGuiData().getInfoSlot();

        ItemBuilder infoBuilder = new ItemBuilder(infoMaterial)
                .name(infoName)
                .lore(infoLore);

        setItem(infoSlot, infoBuilder.build());

        main.getPluginConfig().getEventData().getCustomPrices().forEach((material, price) -> {
            String translatedName = main.getPluginConfig().getTranslationData().getTranslation(material.name());
            if (translatedName == null) {
                translatedName = material.name();
            }
            translatedName = (String) MessageUtils.format("&f" + translatedName);

            List<String> lore = main.getPluginConfig().getGuiData().getItemLore().stream()
                    .map(line -> (String) MessageUtils.format(line.replace("%value%", String.valueOf(price))))
                    .collect(Collectors.toList());

            ItemBuilder itemBuilder = new ItemBuilder(material)
                    .name(translatedName)
                    .lore(lore);

            for (int i = 0; i < getInventory().getSize(); i++) {
                if (i != infoSlot && getInventory().getItem(i) == null) {
                    setItem(i, itemBuilder.build());
                    break;
                }
            }
        });
    }
}