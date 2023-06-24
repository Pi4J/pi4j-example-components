package com.pi4j.catalog.components.base;

import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.mock.provider.spi.MockSpi;

public class SpiDevice extends Component {
    /**
     * The PI4J SPI
     */
    protected final Spi spi;

    protected SpiDevice(Context pi4j, SpiConfig config){
        this.spi = pi4j.create(config);
    }

    // --------------- for testing --------------------

    public MockSpi mock() {
        return asMock(MockSpi.class, spi);
    }
}
