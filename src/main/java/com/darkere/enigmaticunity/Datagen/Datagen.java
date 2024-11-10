package com.darkere.enigmaticunity.Datagen;


import com.darkere.enigmaticunity.EU;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,modid = EU.MODID)
public class Datagen   {
    @SubscribeEvent
    public static void gather(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(), new EULanguageProvider(event));
        event.getGenerator().addProvider(event.includeServer(), new EULootTables(event));
        event.getGenerator().addProvider(event.includeServer(), new EURecipes(event));
        event.getGenerator().addProvider(event.includeClient(), new EUBlockStateProvider(event));
    }


}
