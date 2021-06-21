package net.trevorcraft.grouplock.model.dbis;

import net.trevorcraft.grouplock.database.JavaSqlDBI;
import net.trevorcraft.grouplock.model.entities.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MemberDBI extends JavaSqlDBI<Member> {


  public MemberDBI(DataSource dataSource) {
    super(dataSource);
  }


  @Override
  public JavaSqlDBI<Member> createTable() {
    try {
      try (Connection conn = getConnection()) {
        conn.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS member (" +
                " pk int(11) NOT NULL AUTO_INCREMENT," +
                " player_uuid varchar(36) DEFAULT NULL," +
                " group_pk int(11) DEFAULT NULL," +
                " PRIMARY KEY (pk) ) "
        );
        return this;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return this;
    }
  }

  @Override
  public Member create(Member obj) {
    PreparedStatement statement;
    try {
      try (Connection conn = getConnection()) {
        statement = conn.prepareStatement(
            "INSERT INTO member (player_uuid, group_pk) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, uuidToStr(obj.playerUUID));
        statement.setInt(2, obj.groupPk);
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          int createdPk = rs.getInt(1);
          return new Member(createdPk, obj.playerUUID, obj.groupPk);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean delete(Member obj) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "DELETE FROM member WHERE pk = ?");
        statement.setInt(1, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean save(Member obj) {
    PreparedStatement statement;
    try {
      try (Connection conn = getConnection()) {
        statement = conn.prepareStatement(
            "UPDATE member SET player_uuid = ?, group_pk = ? WHERE pk = ?");
        statement.setString(1, uuidToStr(obj.playerUUID));
        statement.setInt(2, obj.groupPk);
        statement.setInt(3, obj.pk);
        statement.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Member load(int pk) {
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT player_uuid, group_pk FROM member WHERE pk = ?");
        statement.setInt(1, pk);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
          return new Member(
              pk,
              uuidFromString(results.getString("player_uuid")),
              results.getInt("group_pk")
          );
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Member refreshRelations(Member obj) {
    return new Member(obj);
  }

  @Override
  public Collection<Member> loadAll() {
    Collection<Member> objects = new ArrayList<>();
    try {
      try (Connection conn = getConnection()) {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT pk, player_uuid, group_pk FROM member");

        ResultSet results = statement.executeQuery();
        while (results.next()) {
          int pk = results.getInt("pk");
          objects.add(
              new Member(
                  pk,
                  uuidFromString(results.getString("player_uuid")),
                  results.getInt("group_pk")
              )
          );
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objects;
  }
}
