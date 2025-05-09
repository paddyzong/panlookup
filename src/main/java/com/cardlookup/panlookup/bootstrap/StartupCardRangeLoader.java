package com.cardlookup.panlookup.bootstrap;

import com.cardlookup.panlookup.service.JsonCardRangeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
public class StartupCardRangeLoader {
    private static final Logger log = LoggerFactory.getLogger(StartupCardRangeLoader.class);

    private final JsonCardRangeLoader loader;
    private final boolean loadOnStartup;

    public StartupCardRangeLoader(JsonCardRangeLoader loader,
                                  @Value("${cardrange.load-on-startup:true}") boolean loadOnStartup) {
        this.loader = loader;
        this.loadOnStartup = loadOnStartup;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadCardRangesOnStartup() {
        log.info("Load on startup = " + loadOnStartup);
        if (!loadOnStartup) {
            return;
        }

        try {
            File file = new File("data/pres.json.data");
            if (!file.exists()) {
                throw new IllegalStateException("File not found: " + file.getAbsolutePath());
            }
            loader.loadFromFile(file.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load card ranges", e);
        }
    }
}
