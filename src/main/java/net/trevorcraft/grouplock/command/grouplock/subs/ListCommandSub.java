package net.trevorcraft.grouplock.command.grouplock.subs;

import net.trevorcraft.grouplock.command.base.ModularCommandSub;
import net.trevorcraft.grouplock.gui.GroupListGui;
import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListCommandSub extends ModularCommandSub {
  public ListCommandSub() {
    super();
  }

  @Override
  public void execute(Player player) {
    openGroups(player);
  }

  void openGroups(Player player) {
    // Get all the groups
    Collection<Group> groups = Stream.concat(
        Repo.repo().getOwnedGroups(player).stream(),
        Repo.repo().getGroupsWhereMember(player).stream()
    ).collect(Collectors.toList());
    // Show the groups
    new GroupListGui(groups).show(player);
  }

}
