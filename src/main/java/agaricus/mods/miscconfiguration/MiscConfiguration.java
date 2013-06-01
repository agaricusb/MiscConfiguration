package agaricus.mods.miscconfiguration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;
import java.util.logging.Level;

@Mod(modid = "MiscConfiguration", name = "MiscConfiguration", version = "1.0-SNAPSHOT") // TODO: version from resource
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MiscConfiguration {

    private List<IRecipe> recipes = new ArrayList<IRecipe>();
    private Map<String, ItemStack> itemNames = new HashMap<String, ItemStack>();

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());

        try {
            cfg.load();

        } catch (Exception e) {
            FMLLog.log(Level.SEVERE, e, "MiscConfiguration had a problem loading it's configuration");
        } finally {
            cfg.save();
        }

        ItemStack output = new ItemStack(12345, 1, 0);
        recipes.add(new ShapelessOreRecipe(output, "ingotCopper")); // TODO: read from config

        FMLLog.log(Level.INFO, "MiscConfiguration enabled");
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event) {
        for (IRecipe recipe : recipes) {
            GameRegistry.addRecipe(recipe);
        }
    }

    /**
     * Scan item names for lookup
     */
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

    private ItemStack itemStackByName(String name) {
        // TODO
        //OreDictionary.getOres(name); // gets a list of all matching entries
        //GameRegistry.findItemStack(modId, name, 1); // need mod id

        return null;
    }
}
