package net.trevorcraft.grouplock.command.grouplock.subs;

import net.trevorcraft.grouplock.Plugin;
import net.trevorcraft.grouplock.command.base.ModularCommandSub;
import net.trevorcraft.grouplock.command.base.autocompleters.OwnedGroupAutocompleter;
import net.trevorcraft.grouplock.command.base.autocompleters.PlayerNameAutocompleter;
import net.trevorcraft.grouplock.command.base.validators.GroupValidator;
import net.trevorcraft.grouplock.command.base.validators.OnlinePlayerValidator;
import net.trevorcraft.grouplock.gui.ConfirmationGui;
import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddCommandSub extends ModularCommandSub {
  private final static double ADDITION_FEE = Bukkit.getPluginManager().getPlugin("GroupLock").getConfig().getDouble("fees.groupadd");

  public AddCommandSub() {
    super(new GroupValidator("group_id"), new OnlinePlayerValidator("player_name"));
    addAutocompleter("group_id", new OwnedGroupAutocompleter());
    addAutocompleter("player_name", new PlayerNameAutocompleter());
  }


  @Override
  public void execute(Player player) {
    Group g = getArgument("group_id");
    if (!g.ownerUUID.equals(player.getUniqueId())) {
      sendMessage(player, "You do not own this group");
      return;
    }
    Player playerToAdd = getArgument("player_name");


    // Player online, create a membership
    if (Repo.repo().isPlayerInGroup(playerToAdd, g)) {
      sendMessage(player, "This player is already in the group");
      return;
    }

    new ConfirmationGui.Builder()
        .title("Accept $" + ADDITION_FEE + " fee to add player?")
        .yes(() ->
            addToGroup(player, playerToAdd, g)).show(player);

  }

  private void addToGroup(Player owner, Player playerToAdd, Group group) {
    if (!Plugin.economy.withdrawPlayer(owner, ADDITION_FEE).transactionSuccess()) {
      sendMessage(owner, "You don't have the sufficient funds for the $" + ADDITION_FEE + " member addition fee.");
      return;
    }

    if (Repo.repo().addPlayerToGroup(playerToAdd, group) != null) {
      sendMessage(owner, "Player added successfully!");
      sendMessage(playerToAdd, "You were added to " + owner.getName() + "'s shared chest group");
      sendMessage(playerToAdd, "Their group ID: " + group.pk);
      sendMessage(playerToAdd, "To use a chest with their sign: put" + ChatColor.GOLD + " @" + group.pk + ChatColor.GREEN + " on the top line");
    } else {
      sendMessage(owner, "Failed to add player to group, ping tsarcasm");
    }
  }

}
