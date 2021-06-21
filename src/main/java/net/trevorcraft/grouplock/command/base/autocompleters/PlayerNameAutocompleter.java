package net.trevorcraft.grouplock.command.base.autocompleters;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerNameAutocompleter extends ArgumentAutocompleter {

  @Override
  public List<String> getCompletions(Player player, String arg) {
    return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
  }
}
