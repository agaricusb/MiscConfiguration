package agaricus.mods.miscconfiguration;

import com.google.common.base.Splitter;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class ConfigItemStack {

    //private Map<String, ItemStack> itemNames = new HashMap<String, ItemStack>();

    private ItemStack handle = null;

    public ConfigItemStack(String name) {

        Iterator<String> parts = Splitter.on(":").limit(2).split(name).iterator();
        if (!parts.hasNext()) return;

        String idString = parts.next();

        // parse itemID or itemID:damage

        int itemID;
        int damage = OreDictionary.WILDCARD_VALUE;

        try {
            itemID = Integer.parseInt(idString, 10);
        } catch (NumberFormatException ex) {
            this.handle = null;
            return;
        }

        if (parts.hasNext()) {
            String damageString = parts.next();
            if (damageString.equals("*")) {
                damage = OreDictionary.WILDCARD_VALUE;
            } else {
                try {
                    damage = Integer.parseInt(damageString, 10);
                } catch (NumberFormatException ex) {
                    // keep as default WILDCARD_VALUE
                }
            }
        }

        int quantity = 1;

        this.handle = new ItemStack(itemID, quantity, damage);

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
    public boolean equals(Object object) {
        if (object == null || !(object instanceof ConfigItemStack)) return false;
        //return this.handle.isItemEqual(((ConfigItemStack) rhs).handle); // just compares id and damage directly
        ConfigItemStack rhs = (ConfigItemStack) object;

        if (this.handle.itemID != rhs.handle.itemID) {
            return false;
        }
        if (getDamage(this.handle) != OreDictionary.WILDCARD_VALUE && getDamage(rhs.handle) != OreDictionary.WILDCARD_VALUE) { // wildcard matches all damage values
            if (getDamage(this.handle) != getDamage(rhs.handle)) {
                return false;
            }
        }

        // TODO: NBT

        return true;
    }

    // Workaround Forge (bug?) crashing in getItemDamage() if Item null (e.g., Mystcraft writing desk, Forgotten Nature leaves, RedPower flax)
    // CraftGuide also does this:
    // "Added CommonUtilities.getItemDamage(): Unlike ItemStack.getItemDamage, doesn't NPE if the stack's Item is null, instead using reflection as a fallback to read the field directly"
    // https://github.com/Uristqwerty/CraftGuide/commit/d7fdcb02250c7ad56c5d29ed5b79fc357c33298b#L1R204
    private static int getDamage(ItemStack itemStack) {
        if (itemStack.getItem() != null) {
            return itemStack.getItemDamage();
        } else {
            return ObfuscationReflectionHelper.getPrivateValue(ItemStack.class, itemStack, "itemDamage", "field_77991_e", "e");
        }
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
