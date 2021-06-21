package net.trevorcraft.grouplock.command.grouplock.subs;

import net.trevorcraft.grouplock.Plugin;
import net.trevorcraft.grouplock.command.base.ModularCommandSub;
import net.trevorcraft.grouplock.command.base.validators.ArgumentValidator;
import net.trevorcraft.grouplock.command.base.validators.StringValidator;
import net.trevorcraft.grouplock.gui.ConfirmationGui;
import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NewCommandSub extends ModularCommandSub {
  private final static double CREATION_FEE = Bukkit.getPluginManager().getPlugin("GroupLock").getConfig().getDouble("fees.groupcreation");

  public NewCommandSub() {
    super(ArgumentValidator.concatIfLast(new StringValidator("name", 24)));
//    addAutocompleter("name", new PromptAutocompleter("name"));
  }

  @Override
  public void execute(Player player) {
    String groupName = getArgument("name");
    if (groupName.isEmpty()) {
      sendMessage(player, "Name cannot be blank");
      return;
    }

    new ConfirmationGui.Builder()
        .title("Accept $" + CREATION_FEE + " creation fee?")
        .yes(() ->
            createGroup(player, groupName)).show(player);


  }

  private void createGroup(Player player, String name) {
    if (!Plugin.economy.withdrawPlayer(player, CREATION_FEE).transactionSuccess()) {
      sendMessage(player, "You don't have the sufficient funds for the $" + CREATION_FEE + " group creation fee.");
      return;
    }


    Group newGroup = Repo.repo().createGroup(player, name);
    sendMessage(player, "Created new group!");
    sendMessage(player, "New group ID: " + newGroup.pk);
    sendMessage(player, "To lock a chest with a sign: put" + ChatColor.GOLD + " @" + newGroup.pk + ChatColor.GREEN + " on the top line");
  }

}
