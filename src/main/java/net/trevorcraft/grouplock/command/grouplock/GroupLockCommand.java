package net.trevorcraft.grouplock.command.grouplock;

import net.trevorcraft.grouplock.command.base.CommandBase;
import net.trevorcraft.grouplock.command.grouplock.subs.*;

public class GroupLockCommand extends CommandBase {

  public GroupLockCommand() {
    super("grouplock", new ListCommandSub());

    addSubCommand("list", new ListCommandSub());
    addSubCommand("new", new NewCommandSub());

    addSubCommand("add", new AddCommandSub());
    addSubCommand("remove", new RemoveSubCommand());
    addSubCommand("leave", new LeaveCommandSub());

    addSubCommand("icon", new IconCommandSub());
    addSubCommand("members", new MembersCommandSub());

  }

}
