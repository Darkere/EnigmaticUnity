package com.darkere.enigmaticunity.Datagen;

import com.darkere.enigmaticunity.EU;
import com.darkere.enigmaticunity.Registry;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

class EULanguageProvider extends LanguageProvider {
    public EULanguageProvider(GatherDataEvent gen) {
        super(gen.getGenerator().getPackOutput(), EU.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        Registry.sourceProducerBlocks.forEach((type, object) -> add(object.get(), type.getFName() + " Resonating Crystal"));
        Registry.sourceGeneratorBlocks.forEach((type, object) -> add(object.get(), type.getFName() + " Pulsating Crystal"));
        add("itemGroup." + EU.MODID, "Enigmatic Unity");
    }
}
