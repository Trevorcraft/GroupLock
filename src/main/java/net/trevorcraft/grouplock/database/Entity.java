package net.trevorcraft.grouplock.database;

public abstract class Entity {
  public int pk;

  public Entity(int pk) {
    this.pk = pk;
  }
}
