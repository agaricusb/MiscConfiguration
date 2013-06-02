package agaricus.mods.miscconfiguration;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigItemStack {

    //private Map<String, ItemStack> itemNames = new HashMap<String, ItemStack>();

    private ItemStack handle;

    public ConfigItemStack(String name) {
        try {
            int itemID = Integer.parseInt(name, 10);

            this.handle = new ItemStack(itemID, 1, 0); // TODO: optional damage value, ":"
        } catch (NumberFormatException ex) {
            this.handle = null;
        }
        // TODO
        //OreDictionary.getOres(name); // gets a list of all matching entries
        //GameRegistry.findItemStack(modId, name, 1); // need mod id

        // TODO: NBT
    }

    public ConfigItemStack(ItemStack itemStack) {
        this.handle = itemStack;
    }

    public boolean isValid() {
        return handle != null;
    }

    @Override
    public String toString() {
        if (!isValid()) return "null";
        return ""+handle.itemID; // TODO: damage, NBT
    }

    @Override
    public int hashCode() {
        int id = this.handle.itemID;
        int damage = 0; // TODO: this.handle.getItemDamage(), once fixed

        return id + damage * 17;
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null || !(rhs instanceof ConfigItemStack)) return false;
        return this.handle.isItemEqual(((ConfigItemStack) rhs).handle);
    }

    // TODO
    /*
    * Scan item names for lookup
    private void scanItemNames() {
        // TODO: might have to run at first tick, if this is not late enough for all other mods to be loaded

        FMLLog.log(Level.INFO, "Scanning items");
        for (int id = 0; id < Item.itemsList.length; ++id) {
            Item item = Item.itemsList[id];
            if (item == null) continue;

            int damage = 0; // TODO: scan damages?
            ItemStack itemStack = new ItemStack(id, 1, damage);
            try {
                System.out.println("id "+id+" is "+itemStack.getItemName());
            } catch (Throwable t) {
                ;
            }
        }
        // TODO: cache in itemNames
    }
    */


}
