package com.pi4j.components.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

/**
 * FHNW implementation of a camera, works with the raspberry-pi v2 camera module and
 * the crowpi-image on the raspberry-pi.
 * maybe works on other camera-modules too, but is not yet tested.
 *
 * It uses the libcamera-still and libcamera-vid bash commands. those are pre-installed
 * on all raspbian-versions after Buster. (Crowpi is raspbian Version Bullseye)
 */
public class Camera extends Component{

    /**
     * Constructor for using the picture and video functionality
     * calling the init function to test if a camera is active
     */
    public Camera() {
        init();
    }

    /**
     * Takes a picture and saves it to the default Pictures folder
     *
     * If a file already exists, the code will break. better use useDate while taking pictures
     */
    public void takeStill() {
        takeStill(PicConfig.Builder.newInstance().outputPath("/home/pi/Pictures/picam.jpg").build());
    }

    /**
     * Takes a picture using the bash commands
     *
     * @param config Use the ConfigBuilder of the CameraConfig to create the desired parameters
     */
    public void takeStill(PicConfig config) {
        logDebug("Taking Picture");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", config.asCommand());

        try {
            callBash(processBuilder);
        } catch (Exception e) {
            logError("Camera: Error while taking picture: " + e.getMessage());
        }
    }

    /**
     * Takes a video and saves it to the default Videos folder
     *
     * If a file already exists, the code will break. better use useDate while taking videos
     */
    public void takeVid() {
        takeVid(VidConfig.Builder.newInstance().outputPath("/home/pi/Videos/video.h264").recordTime(5000).build());
    }

    /**
     * Takes a video with the configuration and saves it to the output path
     *
     * @param config path to the .h264 file
     */
    public void takeVid(VidConfig config) {
        logDebug("Taking Video");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", config.asCommand());

        try {
            callBash(processBuilder);
        } catch (Exception e) {
            logError("Camera: Error while taking video: " + e.getMessage());
        }
    }

    /**
     * Uses a ProcessBuilder to call the bash of the RaspberryPI.
     * This will call the command and write the output to the console
     *
     * @param processBuilder which process needs to be built
     */
    private void callBash(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        Process process = processBuilder.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        //exitCode 0 = No Errors
        int exitCode = process.waitFor();
        System.out.println("\nExited with error code : " + exitCode);
    }

    /**
     * testing, if camera is installed on raspberrypi, and if the bash commands
     * will work
     */
    private void init(){
        logDebug("initialisation of camera");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "libcamera-still");

        try {
            callBash(processBuilder);
        } catch (Exception e) {
            logError("Camera: Error at initialisation: " + e.getMessage());
        }
    }

    /**
     * Output Format of pictures
     * These modes determine the output of the picture-file
     * <p>
     * The following encodings can be set
     * {@link #PNG}
     * {@link #JPG}
     * {@link #RGB}
     * {@link #BMP}
     * {@link #YUV420}
     */
    public enum PicEncoding {
        PNG("png"),
        JPG("jpg"),
        RGB("rgb"),
        BMP("bmp"),
        YUV420("yuv420");

        private final String encoding;

        PicEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getEncoding() {
            return encoding;
        }
    }

    /**
     * Output Format of videos
     * These modes determine the output of the video-file
     * <p>
     * The following encodings can be set
     * {@link #H264}
     * {@link #MJPEG}
     * {@link #YUV420}
     */
    public enum VidEncoding {
        H264("h264"),
        MJPEG("mjpeg"),
        YUV420("yuv420");

        private final String encoding;

        VidEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getEncoding() {
            return encoding;
        }
    }

    /**
     * Builder Pattern to create a config for a single Picture
     */
    public static class PicConfig {
        /** where should it be saved and what's the filename?*/
        public final String outputPath;
        /** using datetime as filename?
         * if yes, then the outputPath should be a path, not a file
         */
        public final boolean useDate;
        /** a delay, before taking a picture */
        public final int delay;
        /** output width of the picture */
        public final int width;
        /** output height of the picture */
        public final int height;
        /** the quality of the picture, ranging from 0 to 100
         * where 100 is the best quality of the picture, with no blurring*/
        public final int quality;
        /** The format of the output */
        public final PicEncoding encoding;
        /** when true, there is no preview on the raspberry-pi */
        public final boolean disablePreview;
        /** when true, the preview is in fullscreen */
        public final boolean allowFullscreenPreview;

        /**
         * constructor for the config
         *
         * @param builder builder with the defined options
         */
        private PicConfig(Builder builder){
            this.outputPath = builder.outputPath;
            this.useDate = builder.useDate;
            this.delay = builder.delay;
            this.width = builder.width;
            this.height = builder.height;
            this.quality = builder.quality;
            this.encoding = builder.encoding;
            this.disablePreview = builder.disablePreview;
            this.allowFullscreenPreview = builder.allowFullscreenPreview;
        }

        /**
         * Creates a callable bash command with the defined options.
         *
         * @return a string that can be called from the bash
         */
        public String asCommand(){
            StringBuilder command = new StringBuilder("libcamera-still");
            if (useDate){
                command.append(" -o '").append(outputPath).append(LocalDateTime.now()).append(".").append((encoding != null) ? encoding : "jpg").append("'");
            }else{
                command.append(" -o '").append(outputPath).append("'");}
            if (delay != 0){
                command.append(" -t ").append(delay);}
            if (width != 0){
                command.append(" --width ").append(width);}
            if (height != 0){
                command.append(" --height ").append(height);}
            if (quality != 0){
                command.append(" -q ").append(quality);}
            if (encoding != null){
                command.append(" --encoding ").append(encoding.getEncoding());}
            if (disablePreview){command.append(" -n");}
            if (allowFullscreenPreview && !disablePreview){command.append(" -f");}
            return command.toString();
        }

        /**
         * Builder Pattern, to create a config for a single picture
         *
         * A Config is buildable like this:
         * var config = Camera.PicConfig.Builder.newInstance()
         *                 .outputPath("/home/pi/Pictures/")
         *                 .delay(3000)
         *                 .disablePreview(true)
         *                 .encoding(Camera.PicEncoding.PNG)
         *                 .useDate(true)
         *                 .quality(93)
         *                 .width(1280)
         *                 .height(800)
         *                 .build();
         *
         * Every property can be added or not.
         */
        public static class Builder{
            private String outputPath;
            private boolean useDate;
            private int delay;
            private int width;
            private int height;
            private int quality;
            private PicEncoding encoding;
            private boolean disablePreview;
            private boolean allowFullscreenPreview;

            public static Builder newInstance(){
                return new Builder();
            }

            public Builder outputPath(String outputPath){
                this.outputPath = outputPath;
                return this;
            }

            public Builder useDate(boolean useDate){
                this.useDate = useDate;
                return this;
            }

            public Builder delay(int delay){
                this.delay = delay;
                return this;
            }

            public Builder width(int width){
                this.width = width;
                return this;
            }

            public Builder height(int height){
                this.height = height;
                return this;
            }

            public Builder quality(int quality){
                if(quality < 0 || quality > 100){
                    throw new IllegalArgumentException("quality must be between 0 and 100");
                }
                this.quality = quality;
                return this;
            }

            public Builder encoding(PicEncoding encoding){
                this.encoding = encoding;
                return this;
            }

            public Builder disablePreview(boolean disablePreview){
                this.disablePreview = disablePreview;
                return this;
            }

            public Builder allowFullscreenPreview(boolean allowFullscreenPreview){
                this.allowFullscreenPreview = allowFullscreenPreview;
                return this;
            }

            public PicConfig build() {
                return new PicConfig(this);
            }
        }
    }

    /**
     * Builder Pattern to create a config for a video
     */
    public static class VidConfig {
        /** where should it be saved and what's the filename?*/
        public final String outputPath;
        /** using datetime as filename?
         * if yes, then the outputPath should be a path, not a file
         */
        public final boolean useDate;
        /** the length in milliseconds, how long the camera is actively filming */
        public final int recordTime;
        /** the output-format of the video-file */
        public final VidEncoding encoding;

        /**
         * constructor for the config
         *
         * @param builder builder with the defined options
         */
        private VidConfig(Builder builder){
            this.outputPath = builder.outputPath;
            this.recordTime = builder.recordTime;
            this.encoding = builder.encoding;
            this.useDate = builder.useDate;
        }

        /**
         * Creates a callable bash command with the defined options.
         *
         * @return a string that can be called from the bash
         */
        public String asCommand(){
            StringBuilder command = new StringBuilder("libcamera-vid -t " + recordTime);
            if (useDate){
                command.append(" -o '").append(outputPath).append(LocalDateTime.now()).append(".").append((encoding != null) ? encoding : "h264").append("'");
            }else{
                command.append(" -o '").append(outputPath).append("'");}
            if(encoding != null){
                command.append(" --codec ").append(encoding.getEncoding());}
            return command.toString();
        }

        /**
         * Builder Pattern, to create a config for a video
         *
         * A Config is buildable like this:
         * var vidconfig = Camera.VidConfig.Builder.newInstance()
         *                 .outputPath("/home/pi/Videos/")
         *                 .recordTime(3000)
         *                 .useDate(true)
         *                 .build();
         *
         * Every Property can be added or not.
         */
        public static class Builder{
            private String outputPath;
            private int recordTime;
            private VidEncoding encoding;
            private boolean useDate;

            public static Builder newInstance(){
                return new Builder();
            }

            public Builder outputPath(String outputPath){
                this.outputPath = outputPath;
                return this;
            }

            public Builder recordTime(int recordTime){
                this.recordTime = recordTime;
                return this;
            }

            public Builder encoding(VidEncoding encoding){
                this.encoding = encoding;
                return this;
            }

            public Builder useDate(boolean useDate){
                this.useDate = useDate;
                return this;
            }

            public VidConfig build() {
                return new VidConfig(this);
            }
        }
    }
}
