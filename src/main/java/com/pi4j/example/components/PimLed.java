package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.interfaces.LED;

public class PimLed extends Component implements LED {

    protected Pim517.DigitalInOut PIN;

    protected Context pi4j;

    public PimLed(Context pi4j, Pim517.DigitalInOut PIN){

        this.pi4j = pi4j;
        this.PIN = PIN;

        this.PIN.setDirection(Pim517.DigitalInOut.Direction.OUTPUT);
        this.PIN.invert_polarity(false);
    }

    @Override
    public void setState(boolean On) {
        this.PIN.setValue(On);
    }

    @Override
    public void setStateOn() {
        this.PIN.setValue(true);
    }

    @Override
    public void setStateOff() {
        this.PIN.setValue(false);
    }

    @Override
    public boolean toggleState() {
        boolean oldState = this.PIN.value();
        this.setState(!oldState);
        return !oldState;
    }
}
