package com.pi4j.example.helpers;

import com.pi4j.example.components.events.EventHandler;

public interface SimpleInput {
    boolean isDown();
    boolean isUp();
    void onDown(EventHandler handler);
    void onUp(EventHandler handler);
}
