package com.pi4j.example.helpers;

import com.pi4j.io.gpio.digital.DigitalOutput;

public interface SimpleOutput {
    void setState(boolean on);
    void setStateOn();
    void setStateOff();
    boolean toggleState();
    DigitalOutput getDigitalOutput();
}
