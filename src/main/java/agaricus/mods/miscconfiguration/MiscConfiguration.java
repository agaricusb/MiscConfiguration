package agaricus.mods.miscconfiguration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.relauncher.ReflectionHelper;
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
    private Map<Integer, Integer> maxStackSizes = new HashMap<Integer, Integer>();
    private Map<Integer, Float> blastResistances = new HashMap<Integer, Float>();
    private boolean dumpOreDict = false;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());

        try {
            cfg.load();

            for (Map.Entry<String, Property> entry : cfg.getCategory("Fuel").entrySet()) {
                String key = entry.getKey();
                Property property = entry.getValue();

                ConfigItemStack cis = new ConfigItemStack(key);
                if (!cis.isValid()) {
                    FMLLog.log(Level.WARNING, "MiscConfiguration ignoring unrecognized item name '"+key+"'");
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

            for (Map.Entry<String, Property> entry : cfg.getCategory("RecipesShapelessCrafting").entrySet()) {
                String key = entry.getKey();
                Property property = entry.getValue();

                ConfigItemStackList inputs = new ConfigItemStackList(key);
                ConfigItemStack output = new ConfigItemStack(property.getString());

                recipes.add(new ShapelessOreRecipe(output.getItemStack(), inputs.getArray()));
            }

            for (Map.Entry<String, Property> entry : cfg.getCategory("MaxStackSizes").entrySet()) {
                String key = entry.getKey();
                Property property = entry.getValue();

                ConfigItemStack item = new ConfigItemStack(key);
                if (!item.isValid()) {
                    FMLLog.log(Level.WARNING, "MiscConfiguration ignoring unrecognized item name '"+key+"'");
                    continue;
                }

                maxStackSizes.put(item.getItemStack().itemID, property.getInt());
            }

            for (Map.Entry<String, Property> entry : cfg.getCategory("BlastResistances").entrySet()) {
                String key = entry.getKey();
                Property property = entry.getValue();

                ConfigItemStack item = new ConfigItemStack(key);
                if (!item.isValid()) {
                    FMLLog.log(Level.WARNING, "MiscConfiguration ignoring unrecognized item name '"+key+"'");
                    continue;
                }

                blastResistances.put(item.getItemStack().itemID, (float) property.getDouble(0)); // TODO: configurable from item name (obsidian..)

                if (cfg.get(Configuration.CATEGORY_GENERAL, "DumpOreDict", false).getBoolean(false)) {
                    dumpOreDict = true;
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

        for (Map.Entry<Integer, Integer> entry : maxStackSizes.entrySet()) {
            int itemID = entry.getKey();
            int maxStackSize = entry.getValue();

            Item item = Item.itemsList[itemID];
            if (item == null) {
                FMLLog.log(Level.WARNING, "MiscConfiguration ignoring non-existent item '"+itemID+"'");
                continue;
            }

            item.setMaxStackSize(maxStackSize);
        }

        for (Map.Entry<Integer, Float> entry : blastResistances.entrySet()) {
            int blockID = entry.getKey();
            float resistance = entry.getValue();

            Block block = Block.blocksList[blockID];
            if (block == null) {
                FMLLog.log(Level.WARNING, "MiscConfiguration ignoring non-existent block '"+blockID+"'");
                continue;
            }

            block.blockResistance = resistance;
        }

        if (dumpOreDict) {
            dumpOreDict();
        }
    }

    @Override
    public int getBurnTime(ItemStack fuel) {
        ConfigItemStack cis = new ConfigItemStack(fuel);

        if (!fuelTimes.containsKey(cis)) return 0;

        return fuelTimes.get(cis);
    }

    /**
     * Dump ore dictionary
     */
    public static void dumpOreDict() {
        Map<Integer, ItemData> idMap = ReflectionHelper.getPrivateValue(GameData.class, null, "idMap");

        List<String> oreNames = Arrays.asList(OreDictionary.getOreNames());
        Collections.sort(oreNames);

        for (String oreName : oreNames) {
            StringBuffer sb = new StringBuffer();

            sb.append("ore: " + oreName + ": ");
            ArrayList<ItemStack> oreItems = OreDictionary.getOres(oreName);
            for (ItemStack oreItem : oreItems) {
                ItemData itemData = idMap.get(oreItem.itemID);
                String modID = itemData.getModId();

                sb.append(oreItem.itemID + ":" + oreItem.getItemDamage() + "=" + modID + ", ");
            }
            System.out.println(sb);
        }
    }
}
