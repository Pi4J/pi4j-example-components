package com.pi4j.example.components.interfaces;

public interface LED {
    void setState(boolean On);
    void setStateOn();
    void setStateOff();
    boolean toggleState();
}
