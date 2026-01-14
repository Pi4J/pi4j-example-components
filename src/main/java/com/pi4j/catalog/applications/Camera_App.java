package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.catalog.components.base.Component;
import com.pi4j.catalog.components.Camera;

/**
 * <p>
 * see <a href="https://pi4j.com/examples/components/camera/">Description on Pi4J website</a>
 */
public class Camera_App  {

    public static void main(String[] args) {

        System.out.println("Camera demo started");

        Camera camera = new Camera();

        System.out.println("Taking a default picture");
        camera.recordPicture();

        System.out.println("Taking a pic with different parameters");
        var config = Camera.newPictureConfigBuilder()
                .outputPath("/home/pi/Pictures/")
                .delay(3000)
                .disablePreview(true)
                .encoding(Camera.PicEncoding.PNG)
                .useDate(true)
                .quality(93)
                .width(1280)
                .height(800)
                .build();

        camera.recordPicture(config);

        System.out.println("Waiting for camera to take pic");
        Component.delay(Duration.ofSeconds(4));

        System.out.println("Taking a video for 3 seconds");
        var vidconfig = Camera.newVidConfigBuilder()
                .outputPath("/home/pi/Videos/")
                .recordTime(3000)
                .useDate(true)
                .build();
        camera.recordVideo(vidconfig);

        camera.shutdown();
        System.out.println("Camera demo finished");
    }
}
