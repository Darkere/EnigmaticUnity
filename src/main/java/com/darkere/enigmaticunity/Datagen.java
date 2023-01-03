package com.darkere.enigmaticunity;


import com.google.common.base.CaseFormat;
import com.google.common.eventbus.Subscribe;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EU.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen   {
    @SubscribeEvent
    public static void gather(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(), new EULanguageProvider(event.getGenerator()));
    }


    static class EULanguageProvider extends LanguageProvider {
        public EULanguageProvider(DataGenerator gen) {
            super(gen, EU.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            Registry.sourceProducerBlocks.forEach((type, object) -> add(object.get(), type.getFName() + " Resonating Crystal"));
            Registry.sourceGeneratorBlocks.forEach((type, object) -> add(object.get(),type.getFName() + " Pulsating Crystal"));
            add(EU.EUCreativeTab.CREATIVE_MODE_TAB.getRecipeFolderName(),"Enigmatic Unity");
        }
    }
}
