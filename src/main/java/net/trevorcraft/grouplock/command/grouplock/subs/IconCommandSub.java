package net.trevorcraft.grouplock.command.grouplock.subs;

import net.trevorcraft.grouplock.command.base.ModularCommandSub;
import net.trevorcraft.grouplock.command.base.autocompleters.OwnedGroupAutocompleter;
import net.trevorcraft.grouplock.command.base.validators.GroupValidator;
import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IconCommandSub extends ModularCommandSub {
  public IconCommandSub() {
    super(new GroupValidator("group_id"));
    addAutocompleter("group_id", new OwnedGroupAutocompleter());
  }

  @Override
  public void execute(Player player) {
    Group g = getArgument("group_id");
    if (!g.isOwner(player)) {
      sendMessage(player, "Only the owner of a group can change the icon");
      return;
    }
    ItemStack heldItem = player.getItemInHand();
    if (heldItem == null || heldItem.getType() == Material.AIR) {
      sendMessage(player, "You need to hold an item to set the icon");
      return;
    }
    //ignore ? lol idc
    Repo.repo().setIcon(g, heldItem.getType().name());
    sendMessage(player,"Updated group icon");
  }
}
