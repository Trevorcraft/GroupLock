package net.trevorcraft.grouplock.command.grouplock.subs;

import net.trevorcraft.grouplock.command.base.ModularCommandSub;
import net.trevorcraft.grouplock.command.base.autocompleters.OwnedGroupAutocompleter;
import net.trevorcraft.grouplock.command.base.validators.GroupValidator;
import net.trevorcraft.grouplock.gui.MembersListGui;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.entity.Player;

public class MembersCommandSub extends ModularCommandSub {
  public MembersCommandSub() {
    super(new GroupValidator("group_id"));
    addAutocompleter("group_id", new OwnedGroupAutocompleter());
  }


  @Override
  public void execute(Player player) {
    Group g = getArgument("group_id");
    new MembersListGui(g).show(player);
  }
}
