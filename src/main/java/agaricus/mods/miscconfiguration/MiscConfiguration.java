package agaricus.mods.miscconfiguration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;
import java.util.logging.Level;

@Mod(modid = "MiscConfiguration", name = "MiscConfiguration", version = "1.0-SNAPSHOT") // TODO: version from resource
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MiscConfiguration implements IFuelHandler {

    private Map<ConfigItemStack, Integer> fuelTimes = new HashMap<ConfigItemStack, Integer>();
    private List<IRecipe> recipes = new ArrayList<IRecipe>();


    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());

        try {
            cfg.load();

            ConfigCategory fuels = cfg.getCategory("Fuel");
            for (Map.Entry<String, Property> entry : fuels.entrySet()) {
                String key = entry.getKey();
                Property property = entry.getValue();

                ConfigItemStack cis = new ConfigItemStack(key);
                if (!cis.isValid()) {
                    FMLLog.log(Level.WARNING, "Ignoring unrecognized item name '"+key+"'");
                } else {
                    fuelTimes.put(cis, property.getInt()); // TODO: match other (possibly vanilla) fuels with getItemBurnTime()
                    //fuelTimes.put(Block.cobblestone.blockID, TileEntityFurnace.getItemBurnTime(new ItemStack(Item.coal)));
                    /* for comparison:
            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getMaterialName().equals("WOOD")) return 200;
            if (i == Item.stick.itemID) return 100;       // 1/2 item
            if (i == Item.coal.itemID) return 1600;       // 8 items
            if (i == Item.bucketLava.itemID) return 20000;
            if (i == Block.sapling.blockID) return 100;
            if (i == Item.blazeRod.itemID) return 2400;
            return GameRegistry.getFuelValue(par0ItemStack);
                     */
                }
            }

        } catch (Exception e) {
            FMLLog.log(Level.SEVERE, e, "MiscConfiguration had a problem loading it's configuration");
        } finally {
            cfg.save();
        }

        GameRegistry.registerFuelHandler(this);

        //ItemStack output = new ItemStack(12345, 1, 0);
        //recipes.add(new ShapelessOreRecipe(output, "ingotCopper")); // TODO: read from config

        FMLLog.log(Level.INFO, "MiscConfiguration enabled");
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event) {
        for (IRecipe recipe : recipes) {
            GameRegistry.addRecipe(recipe);
        }
    }

    @Override
    public int getBurnTime(ItemStack fuel) {
        ConfigItemStack cis = new ConfigItemStack(fuel);

        if (!fuelTimes.containsKey(cis)) return 0;

        return fuelTimes.get(cis);
    }
}
