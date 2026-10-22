package com.redpxnda.nucleus.test.widget;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.gui.screens.Screen;

import java.util.concurrent.atomic.AtomicReference;

public final class DeferredScreen {
    private static final AtomicReference<Screen> PENDING =
            new AtomicReference<>();

    private DeferredScreen() {}

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            Screen screen = PENDING.getAndSet(null);
            if (screen != null) {
                client.setScreen(screen);
            }
        });
    }

    public static void open(Screen screen) {
        PENDING.set(screen);
    }
}