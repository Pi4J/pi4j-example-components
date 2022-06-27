package com.pi4j.components.components;

import com.pi4j.components.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CameraTest extends ComponentTest {
    private Camera camera;

    @BeforeEach
    public void setUp() {
        this.camera = new Camera();
    }


    @Test
    public void testBuilderPattern() {
        //when
        var config = Camera.PicConfig.Builder.outputPath("/home/pi/Pictures/pic.png")
                .delay(3000)
                .disablePreview(true)
                .encoding(Camera.PicEncoding.PNG)
                .quality(93)
                .width(1280)
                .height(800)
                .build();

        //then
        assertEquals("libcamera-still -o '/home/pi/Pictures/pic.png' -t 3000 --width 1280 --height 800 -q 93 --encoding png -n", config.asCommand());
    }
}
