package com.pi4j.example.components;

import com.pi4j.example.components.helpers.Logger;

public abstract class Component {
    /**
     * Logger instance
     */
    protected final Logger logger = new Logger(this.getClass());
}
