package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

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
        delay(Duration.ofSeconds(4));

        System.out.println("Taking a video for 3 seconds");
        var vidconfig = Camera.newVidConfigBuilder()
                .outputPath("/home/pi/Videos/")
                .recordTime(3000)
                .useDate(true)
                .build();
        camera.recordVideo(vidconfig);

        camera.reset();
        System.out.println("Camera demo finished");
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param duration Time to sleep
     */
    private static  void delay(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
