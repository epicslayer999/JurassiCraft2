package net.reuxertz.ainow.api;

import net.ilexiconn.jurassicraft.JurassiCraft;
import net.ilexiconn.jurassicraft.creativetab.JCCreativeTabs;
import net.ilexiconn.jurassicraft.item.ItemDinosaurSpawnEgg;
import net.ilexiconn.jurassicraft.item.ItemPlasterAndBandage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.reuxertz.ainow.core.AINow;
import net.reuxertz.ainow.item.ItemDocSetEntityHome;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ItemRegistry
{
    public static ItemDocSetEntityHome DocumentSetEntityHome = new ItemDocSetEntityHome();
    public static void init()
    {
        DocumentSetEntityHome.setUnlocalizedName("itemDocSetEntityHome");

        ItemRegistry.gameRegistry();
    }

    public static void initCreativeTabs()
    {
    }

    public static void gameRegistry()
    {
        initCreativeTabs();
        try
        {
            for (Field f : ItemRegistry.class.getDeclaredFields())
            {
                Object obj = f.get(null);
                if (obj instanceof Item)
                    registerItem((Item) obj);
                else if (obj instanceof Item[])
                    for (Item item : (Item[]) obj)
                        registerItem(item);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void registerItem(Item item)
    {
        String name = item.getUnlocalizedName();
        String[] strings = name.split("\\.");
        name = strings[strings.length - 1];
        GameRegistry.registerItem(item, name);
    }
}