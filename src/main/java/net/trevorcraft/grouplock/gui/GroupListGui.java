package net.trevorcraft.grouplock.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import net.trevorcraft.grouplock.Util;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;

public class GroupListGui extends CollectionGui<Group> {
  public GroupListGui(Collection<Group> groups) {
    super(groups, "Player groups");

  }

  @Override
  protected void customInit(Player player, InventoryContents contents) {
    contents.set(0, 4, ClickableItem.empty(Util.playerHead("Your groups", player)));
  }

  @Override
  protected ClickableItem itemProvider(Player player, Group obj) {
    Material material = Material.getMaterial(obj.logoMaterial);
    if (material == null) material = Material.OAK_SIGN;
    ItemStack item = Util.item(material, obj.name);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.LIGHT_PURPLE + obj.name);

    if (obj.isOwner(player)) {
      meta.addEnchant(Enchantment.CHANNELING, 1, true);
    }
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setLore(Arrays.asList(
        ChatColor.GOLD + "@" + obj.pk,
        "Members: " + (obj.members.size() + 1),
        "Owner: " + Bukkit.getOfflinePlayer(obj.ownerUUID).getName()
    ));

    item.setItemMeta(meta);


    return ClickableItem.of(item, e -> player.performCommand("grouplock members " + obj.pk));

  }
}
