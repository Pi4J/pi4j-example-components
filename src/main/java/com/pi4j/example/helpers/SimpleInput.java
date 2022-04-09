package com.pi4j.example.helpers;

import com.pi4j.example.components.events.SimpleEventHandler;

public interface SimpleInput {
    boolean isDown();
    boolean isUp();
    void onDown(SimpleEventHandler handler);
    void onUp(SimpleEventHandler handler);
}
