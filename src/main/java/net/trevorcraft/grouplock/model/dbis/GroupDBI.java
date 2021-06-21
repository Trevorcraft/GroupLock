package net.trevorcraft.grouplock.model.dbis;

import net.trevorcraft.grouplock.database.JavaSqlDBI;
import net.trevorcraft.grouplock.database.Store;
import net.trevorcraft.grouplock.model.entities.Group;
import net.trevorcraft.grouplock.model.entities.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class GroupDBI extends JavaSqlDBI<Group> {

  private final Store<Member> memberStore;

  public GroupDBI(DataSource dataSource, Store<Member> memberStore) {
    super(dataSource);
    this.memberStore = memberStore;
  }


  @Override
  public JavaSqlDBI<Group> createTable() {
    try {
      try (Connection conn = getConnection()) {
        conn.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS chest_group (" +
                " pk int(11) NOT NULL AUTO_INCREMENT," +
                " chestshop_uuid varchar(36) DEFAULT NULL," +
                " owner_uuid varchar(36) DEFAULT NULL," +
                " logo_material varchar(255) DEFAULT NULL," +
                " name varchar(255) DEFAULT NULL," +
                " PRIMARY KEY (pk) ) "
        );
        return this;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Group create(Group obj) {
    PreparedStatement statement;
    try {
      try (Connection conn = getConnection()) {
        statement = conn.prepareStatement(
            "INSERT INTO chest_group (chestshop_uuid, owner_uuid, logo_material, name) VALUES (?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, uuidToStr(obj.chestShopUUID));
        statement.setString(2, uuidToStr(obj.ownerUUID));
        statement.setString(3, obj.logoMaterial);
        statement.setString(4, obj.name);
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          int createdPk = rs.getInt(1);
          return new Group(createdPk, obj.chestShopUUID, obj.ownerUUID, obj.logoMaterial, obj.name, new ArrayList<>());
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Group obj) {
    //Deleting groups is not supported at this time
    return false;
  }

  @Override
  public boolean save(Group obj) {
    PreparedStatement statement;
    try {
      try (Connection conn = getConnection()) {
        statement = conn.prepareStatement(
            "UPDATE chest_group SET chestshop_uuid = ?, owner_uuid = ?, logo_material = ?, name = ? WHERE pk = ?");
        statement.setString(1, uuidToStr(obj.chestShopUUID));
        statement.setString(2, uuidToStr(obj.ownerUUID));
        statement.setString(3, obj.logoMaterial);
        statement.setString(4, obj.name);
        statement.setInt(5, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Group load(int pk) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT chestshop_uuid, owner_pk, logo_material, name FROM chest_group WHERE pk = ?");
        statement.setInt(1, pk);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
          return new Group(
              pk,
              uuidFromString(results.getString("chestshop_uuid")),
              uuidFromString(results.getString("owner_uuid")),
              results.getString("logo_material"),
              results.getString("name"),
              new ArrayList<>(memberStore.getAllWhere(m -> m.groupPk == pk)));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Group refreshRelations(Group obj) {
    return new Group(obj.pk,
        obj.chestShopUUID,
        obj.ownerUUID,
        obj.logoMaterial,
        obj.name,
        new ArrayList<>(memberStore.getAllWhere(m -> m.groupPk == obj.pk)));
  }

  @Override
  public Collection<Group> loadAll() {
    Collection<Group> objects = new ArrayList<>();
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT pk, chestshop_uuid, owner_uuid, logo_material, name FROM chest_group");

        ResultSet results = statement.executeQuery();
        while (results.next()) {
          int pk = results.getInt("pk");
          objects.add(
              new Group(
                  pk,
                  uuidFromString(results.getString("chestshop_uuid")),
                  uuidFromString(results.getString("owner_uuid")),
                  results.getString("logo_material"),
                  results.getString("name"),
                  new ArrayList<>(memberStore.getAllWhere(m -> m.groupPk == pk))));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
