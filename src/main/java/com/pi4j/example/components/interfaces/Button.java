package com.pi4j.example.components.interfaces;

import com.pi4j.io.gpio.digital.DigitalState;

public interface Button {
    boolean isDown();
    boolean isUp();
    void onUp(Runnable task);
    void onDown(Runnable task);
    void whilePressed(Runnable task, long whilePressedDelay);
    void deRegisterAll();
    Runnable getOnDown();
    Runnable getOnUp();
    Runnable getWhilePressed();
}
