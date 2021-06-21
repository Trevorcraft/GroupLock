package net.trevorcraft.grouplock.managers;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.*;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import net.trevorcraft.grouplock.model.entities.Group;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;


public class SignManager implements Listener {


  public void enable() {
    System.out.println("Creating any missing chestshop accounts");
    populateChestshopAccounts();
  }

  private void populateChestshopAccounts() {
    try {
      for (Group g : Repo.repo().groupStore.getAll()) {
        Account csAccount = NameManager.getAccount(g.chestShopUUID);
        if (csAccount == null) {
          System.out.println(" - Creating CS account for group " + g.pk + " [" + g.chestShopUUID + "]");
          //If none exists then make a new one
          String name = "@" + g.pk + "-" + g.name;
          Account newCsAccount = new Account(name, g.chestShopUUID);
          NameManager.createAccount(newCsAccount);
        }
      }
    } catch (NoSuchMethodError e) {
      System.out.println("Error populating chest shop accounts (no such method)");
      System.out.println("Are you using the correct ChestShop jar?");
    }
  }

  // This is verifying if the sign is valid
  // i.e. will it pop off or become a valid sign
  @EventHandler
  public void onPreShopCreation(PreShopCreationEvent event) {
    String accountLine = event.getSignLine(NAME_LINE);
    String priceLine = event.getSignLine(PRICE_LINE);
    String itemLine = event.getSignLine(ITEM_LINE);

    // We only care if the first line starts with an @
    if (!accountLine.startsWith("@")) {
      return;
    }

    int groupId = 0;
    if (accountLine.substring(1).matches("\\d+")) {
      groupId = Integer.parseInt(accountLine.substring(1));
    } else if (accountLine.contains("-")) {
      // Dash allows us to put extra stuff after the id
      int indexOfDash = accountLine.indexOf("-");
      if (accountLine.substring(1, indexOfDash).matches("\\d+")) {
        groupId = Integer.parseInt(accountLine.substring(1, indexOfDash));
      }
    } else {
      return;
    }


    //If an account exists for this account id
    Group group;


    if ((group = Repo.repo().getGroupWithPk(groupId)) != null) {
      // The first line represents a valid group
      //If the sign is a sell sign don't allow holdings accounts
      if (!itemLine.toLowerCase().equals("air")) {
        event.setOutcome(PreShopCreationEvent.CreationOutcome.INVALID_ITEM);
        event.getPlayer().sendMessage("You cannot create a group locked chest that sells anything");
        event.getPlayer().sendMessage("Bottom line must be: Air");
        return;
      }
      // Don't allow players outside of the group to lock the chest
      if (!Repo.repo().isPlayerInGroup(event.getPlayer(), group)) {
        event.setOutcome(PreShopCreationEvent.CreationOutcome.NO_PERMISSION);
      }
    }

  }

  @EventHandler
  public void onAccountQuery(AccountQueryEvent event) {
    String accountLine = event.getName();
    if (!accountLine.startsWith("@")) {
      return;
    }
    int groupId;
    if (accountLine.substring(1).matches("\\d+")) {
      groupId = Integer.parseInt(accountLine.substring(1));
    } else if (accountLine.contains("-")) {
      int indexOfDash = accountLine.indexOf("-");
      groupId = Integer.parseInt(accountLine.substring(1, indexOfDash));
    } else {
      return;
    }

    //If an account exists with this id
    Group group;
    if ((group = Repo.repo().getGroupWithPk(groupId)) != null) {
      //Get the chest shop account associated with this uuid
      Account csAccount = NameManager.getAccount(group.chestShopUUID);
      if (csAccount == null) {
        //If none exists then make a new one
        String name = "@" + group.pk + "-" + group.name;
        Account newCsAccount = new Account(name, group.chestShopUUID);
        event.setAccount(newCsAccount);
      } else {
        event.setAccount(csAccount);
      }
    }
  }

  @EventHandler
  public void onAccountCheck(PreAccountCheckEvent event) {
    String accountName = event.getAccount().getName();

    if (!accountName.startsWith("@")) {
      return;
    }

    int dashIndex = accountName.indexOf("-");
    if (dashIndex == -1) {
      return;
    }

    int groupId = Integer.parseInt(accountName.substring(1, dashIndex));
    Group group = Repo.repo().getGroupWithPk(groupId);
    if (group != null) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onOwnEvent(AccountOwnerCheckEvent event) {
    if (!event.getName().startsWith("@")) {
      return;
    }

    int dashIndex = event.getName().indexOf("-");
    if (dashIndex == -1) {
      event.setCancelled(true);
      return;
    }
    int groupId = Integer.parseInt(event.getName().substring(1, dashIndex));
    //Find the group for this id
    Group group = Repo.repo().getGroupWithPk(groupId);

    if (group == null) {
      //Account id doesn't exist
      return;
    }

    //Check if the player is a member of this group
    if (!Repo.repo().isPlayerInGroup(event.getPlayer(), group)) {
      return;
    }
    //If the member is an owner of the group then cancel
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEconomyCheck(AccountCheckEvent event) {
    return;
    // We shouldn't need to do anything here
//    if (event.hasAccount()) {
//      return;
//    }
  }

  @EventHandler
  public void onCurrencyAdd(PreCurrencyAddEvent event) {
//    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithUUID(event.getTarget());
//    if (account != null) {
//      event.setCancelled(true);
//      Repo.getInstance().payAccount(null, "shop transaction", account.pk, event.getAmountSent().doubleValue());
//    }
  }

  @EventHandler
  public void onCurrencySubtract(PreCurrencySubtractEvent event) {
//    dev.tycho.stonks.model.core.Account account = Repo.getInstance().accountWithUUID(event.getSender());
//    if (account != null) {
//      event.setCancelled(true);
//      IAccountVisitor visitor = new IAccountVisitor() {
//        @Override
//        public void visit(CompanyAccount a) {
//          if (a.balance < event.getAmountSent().doubleValue()) {
//            event.setBalanceSufficient(false);
//          } else {
//            //We have enough money
//            Repo.getInstance().withdrawFromAccount(null, a, event.getAmountSent().doubleValue());
//            //Transaction success
//          }
//        }
//
//        @Override
//        public void visit(HoldingsAccount a) {
//          //A holdings account can't pay out
//          System.out.println("Tried to pay out of a holdings account using a shop sign");
//          System.out.println("Holdings account ID: " + a.pk);
//          event.setBalanceSufficient(false);
//        }
//      };
//      account.accept(visitor);
//    }
  }

  @EventHandler
  public void onAmountCheck(PreAmountCheckEvent event) {
    //If they try to check an account balance for one of our groups cancel it
    Group group = Repo.repo().groupStore.getWhere(g -> g.chestShopUUID.equals(event.getAccount()));
    if (group != null) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onCurrencyCheck(PreCurrencyCheckEvent event) {
    //If they try to check an account balance for one of our groups cancel it
    Group group = Repo.repo().groupStore.getWhere(g -> g.chestShopUUID.equals(event.getAccount()));
    if (group != null) {
      event.setHasEnough(false);
      event.setCancelled(true);
    }
  }
}
