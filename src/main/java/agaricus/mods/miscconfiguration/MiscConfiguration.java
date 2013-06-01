package agaricus.mods.miscconfiguration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraftforge.common.Configuration;

import java.util.EnumSet;
import java.util.logging.Level;

@Mod(modid = "MiscConfiguration", name = "MiscConfiguration", version = "1.0-SNAPSHOT") // TODO: version from resource
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MiscConfiguration {

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
}
