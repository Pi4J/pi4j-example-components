package com.pi4j.example.components;

import uk.co.caprica.picam.*;
import static uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary;

import java.io.IOException;
import java.util.*;
import java.io.File;

public class RaspiCamera extends Component{

    private CameraConfiguration raspiStillConfiguration;
    private RaspiVidConfiguration raspiVidConfiguration;
    private Camera camera;
    private boolean isRaspiStillAvailable, isRaspiVidAvailable;

    /**
     * Constructor for using the picture and video functionality
     *
     * @param raspiStillConfiguration configuration object that represents the raspistill command
     * @param raspiVidConfiguration   configuration object that represents the raspivid command
     */
    public RaspiCamera(CameraConfiguration raspiStillConfiguration, RaspiVidConfiguration raspiVidConfiguration) {
        if(raspiStillConfiguration != null){
            this.raspiStillConfiguration = raspiStillConfiguration;
            isRaspiStillAvailable = true;
        }
        if(raspiVidConfiguration != null){
            this.raspiVidConfiguration = raspiVidConfiguration;
            isRaspiVidAvailable = true;
        }

        init();
        logDebug("new Component Camera is initialised");
    }

    /**
     * Constructor for only using the picture functionality
     *
     * @param raspiStillConfiguration configuration object that represents the raspistill command
     */
    public RaspiCamera(CameraConfiguration raspiStillConfiguration) {
        this(raspiStillConfiguration, null);
    }

    /**
     * Constructor for only using the video functionality
     *
     * @param raspiVidConfiguration configuration object that represents the raspivid command
     */
    public RaspiCamera(RaspiVidConfiguration raspiVidConfiguration) {
        this(null, raspiVidConfiguration);
    }

    /**
     * initialisation of the picam native library.
     */
    private void init() {
        try {
            // Extract the bundled picam native library to a temporary file and load it
            installTempLibrary();
            camera = new Camera(raspiStillConfiguration);
        } catch (Error | NativeLibraryException | CameraException error) {
            logError("RaspberryPiCameraComponent: Error while initialising: " + error.getMessage());
        }
    }

    /**
     * Takes picture and saves it to the default Pictures folder
     */
    public void takeStill() {
        takeStill("/home/pi/Pictures/picam.jpg", 0);
    }

    /**
     * takes picture and saves it to the outputPath
     *
     * @param outputPath path to save picture to
     */
    public void takeStill(String outputPath) {
        takeStill(outputPath, 0);
    }

    /**
     * Takes a picture using the picam library and saves it to the specified output after a delay
     *
     * @param outputPath path to the output file that saves the picture
     * @param delay      before taking the picture
     */
    public void takeStill(String outputPath, int delay) {
        logDebug("RaspberryPiCameraComponent: Take Picture to the path: " + outputPath + " with the delay: " + delay);
        if (!isRaspiStillAvailable) {
            ExceptionInInitializerError ex = new ExceptionInInitializerError("RaspiStill has to be initialised with the Configuration for taking pictures");
            logError("RaspberryPiCameraComponent: " + ex.getMessage());
            throw ex;
        }
        try {
            //For the first picture a longer delay is sometimes needed
            camera.takePicture(new FilePictureCaptureHandler(new File(outputPath)), delay);
        } catch (Exception e) {
            logError("RaspberryPiCameraComponent: Error while taking picture: " + e.getMessage());
        }
    }

    /**
     * The picam library has a private constructor and therefore needs this method
     *
     * @return new CameraConfiguration object of the picam library
     */
    public static CameraConfiguration createCameraConfiguration() {
        return CameraConfiguration.cameraConfiguration();
    }

    /**
     * Takes a video with the configuration and saves it to the output path
     *
     * @param outputPath path to the .h264 file
     */
    // tag::RasPiCamTakeVid[]
    public void takeVid(String outputPath) {
        logDebug("RaspberryPiCameraComponent: Taking video to the path: " + outputPath);

        if (!isRaspiVidAvailable) {
            ExceptionInInitializerError ex = new ExceptionInInitializerError("RaspiVid has to be initialised with the Configuration for taking videos");
            logError("RaspberryPiCameraComponent: " + ex.getMessage());
            throw ex;
        }
        try {
            raspiVidConfiguration.output(outputPath);
            String command = raspiVidConfiguration.toString();

            //This will just run in the background and save the video in a file
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            Thread.sleep(raspiVidConfiguration.getTime());

            p.destroy();
            if (p.isAlive())
                p.destroyForcibly();

        } catch (InterruptedException | IOException ieo) {
            logError("RaspberryPiCameraComponent: Error while taking video: " + ieo.getMessage());
        }
    }

    /**
     * This class is used to handle the raspivid commands for taking videos.
     * Here's a list of the raspivid commands
     * https://www.raspberrypi.org/documentation/usage/camera/raspicam/raspivid.md
     * <p>
     * the syntax and usage is similar to the CameraConfiguration of the picam library for raspistill.
     */
    public class RaspiVidConfiguration {
        private final HashMap<String, String> commands;

        /**
         * New Configuration without any changes will save a video with the default settings to the path "/home/pi/Videos/video.h264"
         */
        public RaspiVidConfiguration() {
            commands = new HashMap<>();
            output("/home/pi/Videos/video.h264");
        }

        /**
         * default is 5 seconds
         *
         * @param time length of the video in milliseconds
         */
        public RaspiVidConfiguration time(long time) {
            commands.put("time", "-t " + time);
            return this;
        }

        /**
         * @param path path to the file that should hold the video
         */
        public RaspiVidConfiguration output(String path) {
            commands.put("output", "-o " + path);
            return this;
        }

        /**
         * default width is 1920
         * @param w width of the video
         */
        public RaspiVidConfiguration width(int w) {
            if (w < 64 || w > 1920)
                throw new IllegalArgumentException("width must be in the range 64 - 1920");

            commands.put("width", "-w " + w);
            return this;
        }

        /**
         * default height is 1080
         * @param h height of the video
         */
        public RaspiVidConfiguration height(int h) {
            if (h < 64 || h > 1080)
                throw new IllegalArgumentException("height must be in the range 64 - 1920");

            commands.put("height", "-h " + h);
            return this;
        }

        /**
         * horizontally flips the video
         */
        public RaspiVidConfiguration horizontalFlip() {
            commands.put("flip", "-hf");
            return this;
        }

        /**
         * vertically flips the video
         */
        public RaspiVidConfiguration verticalFlip() {
            commands.put("flip", "-vf");
            return this;
        }

        /**
         * disables the preview window settings
         */
        public RaspiVidConfiguration previewOff() {
            commands.put("preview", "-n");
            return this;
        }

        /**
         * enables the preview with maximum window settings
         */
        public RaspiVidConfiguration previewFullscreen() {
            commands.put("preview", "-f");
            return this;
        }

        /**
         * enables the preview with own parameters
         *
         * @param x x position for preview
         * @param y y position for preview
         * @param w width for preview
         * @param h height for preview
         */
        public RaspiVidConfiguration preview(int x, int y, int w, int h) {
            if (x < 0 || x > w)
                throw new IllegalArgumentException("x must be in the range 0 - w");
            if (y < 0 || y > h)
                throw new IllegalArgumentException("x must be in the range 0 - h");
            if (w < 64 || w > getWidth())
                throw new IllegalArgumentException("width must be in the range 64 - specified width or default 1920");
            if (h < 64 || h > getHeight())
                throw new IllegalArgumentException("height must be in the range 64 - specified height or default 1080");

            StringBuilder sb = new StringBuilder();
            sb.append("-p ").append(x).append(",").append(y).append(",").append(w).append(",").append(h);

            commands.put("preview", sb.toString());
            return this;
        }

        public RaspiVidConfiguration videoStabilisation() {
            commands.put("stabilisation", "-vs");
            return this;
        }

        /**
         * @param fps frames per second for the video
         */
        public RaspiVidConfiguration framerate(int fps) {
            if (fps < 2 || fps > 30) {
                throw new IllegalArgumentException("FPS must be in the range 2 to 30");
            }
            commands.put("framerate", "-fps " + fps);
            return this;
        }

        /**
         * @param sharpness defines sharpness in range of
         */
        public RaspiVidConfiguration sharpness(int sharpness) {
            if (sharpness < -100 || sharpness > 100) {
                throw new IllegalArgumentException("sharpness must be in the range -100 to 100");
            }
            commands.put("sharpness", "-sh " + sharpness);
            return this;
        }

        /**
         * @param contrast contrast in range of -100 to 100
         */
        public RaspiVidConfiguration contrast(int contrast) {
            if (contrast < -100 || contrast > 100) {
                throw new IllegalArgumentException("contrast must be in the range -100 to 100");
            }
            commands.put("framerate", "-co " + contrast);
            return this;
        }

        /**
         * @param brightness in range of 0 to 100
         */
        public RaspiVidConfiguration brightness(int brightness) {
            if (brightness < 0 || brightness > 100) {
                throw new IllegalArgumentException("brightness must be in the range 0 to 100");
            }
            commands.put("brightness", "-br " + brightness);
            return this;
        }

        /**
         * @param saturation in range of -100 to 100
         */
        public RaspiVidConfiguration saturation(int saturation) {
            if (saturation < -100 || saturation > 100) {
                throw new IllegalArgumentException("saturation must be in the range -100 to 100");
            }
            commands.put("saturation", "-sa " + saturation);
            return this;
        }

        /**
         * default bitrate is 10MBit (10_000_000 Bits)
         * @param bitrate of the video in bits
         */
        public RaspiVidConfiguration bitrate(long bitrate) {
            if (bitrate < 1_000_000 || bitrate > 25_000_000) {
                throw new IllegalArgumentException("Bitrate must be in the range 2 to 30");
            }
            commands.put("bitrate", "-bitrate " + bitrate);
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> entry : commands.entrySet()) {
                if (entry.getValue() != null) {
                    sb.append(entry.getValue());
                    sb.append(" ");
                }
            }

            sb.insert(0, "raspivid ");
            return sb.toString();
        }

        public long getTime() {
            String command = commands.get("time");
            if (command == null) return 5000; //default if nothing was specified

            command = command.substring(3);
            return Long.parseLong(command);
        }

        public int getHeight(){
            String command = commands.get("height");
            if (command == null) return 1080; //default if nothing was specified

            //parse the number after -h
            return Integer.parseInt(command.substring(3));
        }

        public int getWidth(){
            String command = commands.get("width");
            if (command == null) return 1920; //default if nothing was specified

            //parse the number after -w
            return Integer.parseInt(command.substring(3));
        }
    }
}
