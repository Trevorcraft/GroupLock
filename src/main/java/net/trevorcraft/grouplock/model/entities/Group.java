package net.trevorcraft.grouplock.model.entities;

import net.trevorcraft.grouplock.database.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class Group extends Entity {
  public final UUID chestShopUUID;
  public final UUID ownerUUID;
  public final String logoMaterial;
  public final String name;

  public final Collection<Member> members;

  public Group(int pk, UUID chestShopUUID, UUID ownerUUID, String logoMaterial, String name, Collection<Member> members) {
    super(pk);
    this.chestShopUUID = chestShopUUID;
    this.ownerUUID = ownerUUID;
    this.logoMaterial = logoMaterial;
    this.name = name;
    this.members = members;
  }

  public Group(Group other) {
    super(other.pk);
    this.chestShopUUID = other.chestShopUUID;
    this.ownerUUID = other.ownerUUID;
    this.logoMaterial = other.logoMaterial;
    this.name = other.name;
    this.members = other.members;
  }

  public boolean isOwner(Player player) {
    return ownerUUID.equals(player.getUniqueId());
  }




}
