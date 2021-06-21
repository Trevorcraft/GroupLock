package net.trevorcraft.grouplock.command.grouplock.subs;

import net.trevorcraft.grouplock.command.base.ModularCommandSub;
import net.trevorcraft.grouplock.command.base.autocompleters.MemberGroupAutocompleter;
import net.trevorcraft.grouplock.command.base.validators.GroupValidator;
import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.entity.Player;

public class LeaveCommandSub extends ModularCommandSub {

  public LeaveCommandSub() {
    super(new GroupValidator("group_id"));
    addAutocompleter("group_id", new MemberGroupAutocompleter());
  }
  @Override
  public void execute(Player player) {
    Group g = getArgument("group_id");
    if (!Repo.repo().isPlayerInGroup(player, g)) {
      sendMessage(player, "You cannot leave a group you are not in!");
      return;
    }

    if (g.isOwner(player)) {
      sendMessage(player, "You cannot leave a group you own");
      return;
    }


    if (Repo.repo().removePlayerFromGroup(Repo.repo().getMember(player, g))) {
      sendMessage(player, "You left the group!");
    } else {
      sendMessage(player, "Error leaving the group, message tsar lol");
    }
  }
}
