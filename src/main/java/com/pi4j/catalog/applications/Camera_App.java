package com.pi4j.catalog.applications;

import com.pi4j.context.Context;
import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Camera;

/**
 * <P>
 * see <a href="https://pi4j.com/examples/components/camera/">Description on Pi4J website</a>
 */
public class Camera_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("Initializing the camera");
        Camera camera = new Camera();

        System.out.println("Taking a default picture");
        camera.takeStill();

        System.out.println("Taking a pic with different parameters");
        var config = Camera.PicConfig.Builder.newInstance()
                .outputPath("/home/pi/Pictures/")
                .delay(3000)
                .disablePreview(true)
                .encoding(Camera.PicEncoding.PNG)
                .useDate(true)
                .quality(93)
                .width(1280)
                .height(800)
                .build();

        camera.takeStill(config);

        System.out.println("Waiting for camera to take pic");
        delay(4000);

        System.out.println("Taking a video for 3 seconds");
        var vidconfig = Camera.VidConfig.Builder.newInstance()
                .outputPath("/home/pi/Videos/")
                .recordTime(3000)
                .useDate(true)
                .build();
        camera.takeVid(vidconfig);
    }
}
