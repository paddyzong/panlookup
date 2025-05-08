package com.cardlookup.panlookup.bootstrap;

import com.cardlookup.panlookup.service.JsonCardRangeLoader;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class StartupCardRangeLoader {

    private final JsonCardRangeLoader loader;

    public StartupCardRangeLoader(JsonCardRangeLoader loader) {
        this.loader = loader;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadCardRangesOnStartup() {
        try {
            File file = new File("data/700k-pres.json.data");
            if (!file.exists()) {
                throw new IllegalStateException("File not found: " + file.getAbsolutePath());
            }
            loader.loadFromFile(file.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load card ranges", e);
        }
    }
}
