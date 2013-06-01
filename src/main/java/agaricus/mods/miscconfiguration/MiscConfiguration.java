package agaricus.mods.miscconfiguration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Mod(modid = "MiscConfiguration", name = "MiscConfiguration", version = "1.0-SNAPSHOT") // TODO: version from resource
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MiscConfiguration {

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

        FMLLog.log(Level.INFO, "MiscConfiguration enabled");
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event) {
        // TODO: might have to run at first tick, if this is not late enough for all other mods to be loaded

        FMLLog.log(Level.INFO, "Scanning items");
        for (int id = 0; id < Item.itemsList.length; ++id) {
            Item item = Item.itemsList[id];
            if (item == null) continue;

            int damage = 0;
            ItemStack itemStack = new ItemStack(id, 1, damage);
            try {
                System.out.println("id "+id+" is "+itemStack.getItemName());
            } catch (Throwable t) {
                ;
            }
        }
    }

    private ItemStack itemStackByName(String name) {
        // TODO
        //OreDictionary.getOres(name); // gets a list of all matching entries
        //GameRegistry.findItemStack(modId, name, 1); // need mod id

        return null;
    }
}
