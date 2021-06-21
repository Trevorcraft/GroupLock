package net.trevorcraft.grouplock.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import net.trevorcraft.grouplock.Util;
import net.trevorcraft.grouplock.model.entities.Group;
import net.trevorcraft.grouplock.model.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MembersListGui extends CollectionGui<Member> {
  private final Group group;
  public MembersListGui(Group group) {
    super(group.members, "Members list");
    this.group = group;
  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 0, ClickableItem.of(Util.item(Material.BARRIER, "Back to list"),
        e -> player.performCommand("grouplock list")));
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(group.ownerUUID);
    contents.set(0, 4, ClickableItem.empty(Util.playerHead(offlinePlayer.getName(), offlinePlayer, "Owner")));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Member obj) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(obj.playerUUID);
    return ClickableItem.empty(Util.playerHead(offlinePlayer.getName(), offlinePlayer, "Has access"));
  }
}
