package agaricus.mods.miscconfiguration;

import com.google.common.base.Splitter;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigItemStackList {

    public List<ConfigItemStack> list = new ArrayList<ConfigItemStack>();

    public ConfigItemStackList(String stringList) {
        Iterator<String> parts = Splitter.on(",").split(stringList).iterator();

        while (parts.hasNext()) {
            list.add(new ConfigItemStack(parts.next()));
        }
    }

    @SuppressWarnings("unchecked")
    public Object[] getArray() {
        List<Object> objectList = new ArrayList<Object>();
        for (ConfigItemStack cis : list) {
            objectList.add(cis.getItemStack()); // TODO: validate
        }

        return objectList.toArray();
    }
}
