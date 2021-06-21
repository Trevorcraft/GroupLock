package net.trevorcraft.grouplock.managers;

import net.trevorcraft.grouplock.Plugin;
import net.trevorcraft.grouplock.database.DatabaseStore;
import net.trevorcraft.grouplock.database.SyncStore;
import net.trevorcraft.grouplock.model.dbis.GroupDBI;
import net.trevorcraft.grouplock.model.dbis.MemberDBI;
import net.trevorcraft.grouplock.model.entities.Group;
import net.trevorcraft.grouplock.model.entities.Member;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Repo {
  private static Repo instance;

  public DatabaseStore<Group> groupStore;
  public DatabaseStore<Member> memberStore;

  private BasicDataSource dataSource;

  public Repo() {
    dataSource = new BasicDataSource();
    setup();
  }

  //get instance
  public static Repo repo() {
    if (instance == null) {
      instance = new Repo();
    }
    return instance;
  }

  private void createDataSource() {
    String host = Plugin.plugin.getConfig().getString("mysql.host");
    String port = Plugin.plugin.getConfig().getString("mysql.port");
    String database = Plugin.plugin.getConfig().getString("mysql.database");
    String username = Plugin.plugin.getConfig().getString("mysql.username");
    String password = Plugin.plugin.getConfig().getString("mysql.password");
    String useSsl = Plugin.plugin.getConfig().getString("mysql.ssl");
    String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
        "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL="
        + useSsl;
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    dataSource.setMinIdle(5);
    dataSource.setMaxIdle(10);
    dataSource.setMaxOpenPreparedStatements(100);
    System.out.println("Connected to database");
  }

  public void setup() {
    createDataSource();

    groupStore = new SyncStore<>();
    memberStore = new SyncStore<>();

    groupStore.setDbi(new GroupDBI(dataSource, memberStore));
    memberStore.setDbi(new MemberDBI(dataSource));


    repopulateAll();
  }
  public void repopulateAll() {
    groupStore.populate();
    memberStore.populate();
  }

  // Return all the groups owned by the player
  public List<Group> getOwnedGroups(Player player) {
    return groupStore.getAllWhere(group -> group.ownerUUID.equals(player.getUniqueId()));
  }

  // Return all the groups where the player is a member
  // Note: this excludes groups where the player is the owner
  public List<Group> getGroupsWhereMember(Player player) {
    Collection<Member> memberEntries = memberStore.getAllWhere(member -> member.playerUUID.equals(player.getUniqueId()));
    ArrayList<Group> groups = new ArrayList<>();
    // For each member this player is, collect the associated group
    for (Member member : memberEntries) {
      groups.add(groupStore.get(member.groupPk));
    }
    return groups;
  }

  public Group getGroupWithPk(int pk) {
    return groupStore.getWhere(group -> group.pk == pk);
  }



  public boolean isPlayerInGroup(Player player, Group group) {
    // Return true if the player owns the group
    if (group.ownerUUID.equals(player.getUniqueId())) return true;
    // Return true if the player is in the group
    for (Member member : group.members) {
      if (member.playerUUID.equals(player.getUniqueId())) return true;
    }
    // Otherwise return false
    return false;
  }





  public Group createGroup(Player player, String groupName) {
    Group newGroup = new Group(0, UUID.randomUUID(), player.getUniqueId(), "chest", groupName, new ArrayList<>());
    newGroup = groupStore.create(newGroup);
    return newGroup;
  }


  public Member addPlayerToGroup(Player player, Group group) {
    Member newMember = new Member(0, player.getUniqueId(), group.pk);
    newMember = memberStore.create(newMember);
    groupStore.refreshRelations(group.pk);
    return newMember;
  }

  public boolean removePlayerFromGroup(Member member) {
    boolean result = memberStore.delete(member.pk);
    groupStore.refreshRelations(member.groupPk);
    return result;
  }

  public Member getMember(Player player, Group group) {
    for (Member member : group.members) {
      if (member.playerUUID.equals(player.getUniqueId())) return member;
    }
    return null;
  }

  public void setIcon(Group group, String material) {
    Group newGroup = new Group(group.pk, group.chestShopUUID, group.ownerUUID, material, group.name, group.members);
    groupStore.save(newGroup);
  }












}
