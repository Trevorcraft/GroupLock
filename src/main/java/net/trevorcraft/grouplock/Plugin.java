package net.trevorcraft.grouplock;

import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import net.trevorcraft.grouplock.command.base.CommandBase;
import net.trevorcraft.grouplock.command.grouplock.GroupLockCommand;
import net.trevorcraft.grouplock.gui.CollectionGui;
import net.trevorcraft.grouplock.gui.InventoryGui;
import net.trevorcraft.grouplock.managers.Repo;
import net.trevorcraft.grouplock.managers.SignManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
  public static Plugin plugin;
  public static Economy economy = null;
  @Override
  public void onEnable() {
    plugin = this;
    CommandBase.setPlugin(this);
    getLogger().info("GroupLock is enabled!");

    //Setup GUIs
    InventoryManager guiInventoryManager = new InventoryManager(this);
    guiInventoryManager.init();
    InventoryGui.inventoryManager = guiInventoryManager;
    CollectionGui.inventoryManager = guiInventoryManager;

    //Register the new command
    new GroupLockCommand();

    //Load the database
    Repo.repo();

    this.saveDefaultConfig();

    // Load the vault economy
    if (!setupEconomy()) {
      getLogger().info("Error enabling grouplock economy");
      return;
    }

    // Load the sign manager and populate accounts
    getServer().getPluginManager().registerEvents(new SignManager(), this);



    //    getServer().getPluginManager().registerEvents(new SignListener(this), this);
  }

  private boolean setupEconomy() {
    RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (economyProvider != null) {
      economy = economyProvider.getProvider();
    }
    return (economy != null);
  }

  @Override
  public void onDisable() {
    getLogger().info("GroupLock is disabled :(");
  }
}
