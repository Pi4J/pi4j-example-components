package com.pi4j.example.components;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;

public class GpioPinCfgData {

    Direction direction;
    public int number;
    public DigitalOutput output;
    public DigitalInput input;

    public enum Direction {
        in, out, none
    }

    public GpioPinCfgData(int number, Direction direction, DigitalOutput output, DigitalInput input) {

        this.number = number;
        this.output = output;
        this.input = input;
        this.direction = direction;
    }
}
