package net.trevorcraft.grouplock.command.base.validators;

import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.model.entities.Group;
import org.apache.commons.lang.StringUtils;

public class GroupValidator extends ArgumentValidator<Group> {
  public GroupValidator(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    if (!StringUtils.isNumeric(str)) {
      return false;
    }
    value = Repo.repo().getGroupWithPk(Integer.parseInt(str));
    return value != null;
  }

  @Override
  public String getPrompt() {
    return "must be a valid group id";
  }
}
