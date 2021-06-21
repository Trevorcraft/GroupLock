package net.trevorcraft.grouplock.command.grouplock.subs;

import net.trevorcraft.grouplock.command.base.ModularCommandSub;
import net.trevorcraft.grouplock.command.base.autocompleters.OwnedGroupAutocompleter;
import net.trevorcraft.grouplock.command.base.autocompleters.PlayerNameAutocompleter;
import net.trevorcraft.grouplock.command.base.validators.GroupValidator;
import net.trevorcraft.grouplock.command.base.validators.OnlinePlayerValidator;
import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.entity.Player;

public class RemoveSubCommand extends ModularCommandSub {

  public RemoveSubCommand() {
    super(new GroupValidator("group_id"), new OnlinePlayerValidator("player_name"));
    addAutocompleter("group_id", new OwnedGroupAutocompleter());
    addAutocompleter("player_name", new PlayerNameAutocompleter());
  }


  @Override
  public void execute(Player player) {
    Group g = getArgument("group_id");
    Player playerToRemove = getArgument("player_name");
    // Only allow the owner (or the same person) remove someone from a group
    if (!g.ownerUUID.equals(player.getUniqueId())) {
      sendMessage(player, "You do not own this group");
      return;
    }

    // Player online, create a membership
    if (!Repo.repo().isPlayerInGroup(playerToRemove, g)) {
      sendMessage(player, "This player is not in the group");
      return;
    }

    if (g.ownerUUID.equals(playerToRemove.getUniqueId())) {
      sendMessage(player, "You cannot remove the owner of the group");
      return;
    }

    // Remove the player from the group
    if (Repo.repo().removePlayerFromGroup(Repo.repo().getMember(playerToRemove, g))) {
      sendMessage(player, "Player removed successfully!");
    } else {
      sendMessage(player, "Failed to remove player from group, ping tsarcasm");
    }
  }
}
