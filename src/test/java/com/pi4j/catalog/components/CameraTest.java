package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.catalog.ComponentTest;

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
        var config = Camera.newPictureConfigBuilder()
                .outputPath("/home/pi/Pictures/pic.png")
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

    @Test
    public void testBuilderPatternVideo() {
        //when
        var config = Camera.newVidConfigBuilder()
                .outputPath("/home/pi/Videos/video.mjpeg")
                .encoding(Camera.VidEncoding.MJPEG)
                .recordTime(5000)
                .build();

        //then
        assertEquals("libcamera-vid -t 5000 -o '/home/pi/Videos/video.mjpeg' --codec mjpeg", config.asCommand());
    }
}
