package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.interfaces.LED;

public class McpLed extends Component implements LED {

    protected Mcp23017.DigitalInOut PIN;

    protected Context pi4j;

    public McpLed(Context pi4j, Mcp23017.DigitalInOut PIN){

        this.pi4j = pi4j;
        this.PIN = PIN;

        this.PIN.setDirection(Mcp23017.DigitalInOut.Direction.INPUT);
        this.PIN.setPullup(true);
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
