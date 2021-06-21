package net.trevorcraft.grouplock.model.entities;

import net.trevorcraft.grouplock.database.Entity;

import java.util.UUID;

public class Member extends Entity {
  public final UUID playerUUID;
  public final int groupPk;

  public Member(int pk, UUID playerUUID, int groupPk) {
    super(pk);
    this.playerUUID = playerUUID;
    this.groupPk = groupPk;
  }

  public Member(Member other) {
    super(other.pk);
    this.playerUUID = other.playerUUID;
    this.groupPk = other.groupPk;
  }

}
