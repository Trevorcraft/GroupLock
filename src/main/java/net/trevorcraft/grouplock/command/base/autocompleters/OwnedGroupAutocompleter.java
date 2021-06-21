package net.trevorcraft.grouplock.command.base.autocompleters;

import net.trevorcraft.grouplock.managers.Repo;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class OwnedGroupAutocompleter extends ArgumentAutocompleter {
  @Override
  public List<String> getCompletions(Player player, String arg) {
    return Repo.repo().getOwnedGroups(player).stream().map(group -> group.pk+"").collect(Collectors.toList());
  }
}
