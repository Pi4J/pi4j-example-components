package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.GpioPinCfgData;
import com.pi4j.example.components.MCP23017;

import java.util.HashMap;

public class MCP23017_App implements Application {
    @Override
    public void execute(Context pi4j) {

        HashMap<Integer, GpioPinCfgData> dioPinData = new HashMap<Integer, GpioPinCfgData>();
        final var mcp = new MCP23017(pi4j, dioPinData);
    }
}
