package com.pi4j.example.components.helpers;

public class PixelColor {
    public static final int WHITE			= 0xFFFFFF;
    public static final int RED				= 0xFF0000;
    public static final int ORANGE			= 0xFFA500;
    public static final int YELLOW			= 0xFFFF00;
    public static final int GREEN			= 0x00FF00;
    public static final int LIGHT_BLUE		= 0xadd8e6;
    public static final int BLUE			= 0x0000FF;
    public static final int PURPLE			= 0x800080;
    public static final int PINK			= 0xFFC0CB;

    public static final int[] RAINBOW = { PixelColor.RED, PixelColor.ORANGE, PixelColor.YELLOW, PixelColor.GREEN,
            PixelColor.LIGHT_BLUE, PixelColor.BLUE, PixelColor.PURPLE, PixelColor.PINK };

    private static final int WHITE_MASK		= 0xffffff;
    private static final int RED_MASK		= 0xff0000;
    private static final int GREEN_MASK		= 0x00ff00;
    private static final int BLUE_MASK		= 0x0000ff;

    private static final int RED_OFF_MASK	= 0x00ffff;
    private static final int GREEN_OFF_MASK	= 0xff00ff;
    private static final int BLUE_OFF_MASK	= 0xffff00;

    public static final int Color_COMPONENT_MAX = 0xff;

    /**
     * Input a value 0 to 255 to get a Color value.
     * The Colors are a transition r - g - b - back to r.
     * @param wheelPos Position on the Color wheel (range 0..255).
     * @return 24-bit RGB Color value
     */
    public static int wheel(int wheelPos) {
        int max = Color_COMPONENT_MAX;
        int one_third = Color_COMPONENT_MAX/3;
        int two_thirds = Color_COMPONENT_MAX*2/3;

        int wheel_pos = max - wheelPos;
        if (wheel_pos < one_third) {
            return createColorRGB(max - wheel_pos * 3, 0, wheel_pos * 3);
        }
        if (wheel_pos < two_thirds) {
            wheel_pos -= one_third;
            return createColorRGB(0, wheel_pos * 3, max - wheel_pos * 3);
        }
        wheel_pos -= two_thirds;
        return createColorRGB(wheel_pos * 3, max - wheel_pos * 3, 0);
    }

    /**
     * Create a Color from relative RGB values
     * @param red Red %, {@code 0 to 1}
     * @param green Green %, {@code 0 to 1}
     * @param blue Blue %, {@code 0 to 1}
     * @return RGB Color integer value
     */
    public static int createColorRGB(float red, float green, float blue) {
        return createColorRGB(Math.round(Color_COMPONENT_MAX*red),
                Math.round(Color_COMPONENT_MAX*green), Math.round(Color_COMPONENT_MAX*blue));
    }

    /**
     * Create a Color from int RGB values
     * @param red Red component {@code 0 to 255}
     * @param green Green component {@code 0 to 255}
     * @param blue Blue component {@code 0 to 255}
     * @return RGB Color integer value
     */
    public static int createColorRGB(int red, int green, int blue) {
        validateColorComponent("Red", red);
        validateColorComponent("Green", green);
        validateColorComponent("Blue", blue);
        return red<<16 | green << 8 | blue;
    }

    /**
     * Creates a Color based on the specified values in the HSL Color model.
     *
     * @param hue The hue, in degrees, {@code 0.0 to 360.0}
     * @param saturation The saturation %, {@code 0.0 to 1.0}
     * @param luminance The luminance %, {@code 0.0 to 1.0}
     * @return RGB Color integer value
     * @throws IllegalArgumentException if {@code hue}, {@code saturation}, {@code brightness} are out of range
     */
    public static int createColorHSL(float hue, float saturation, float luminance) {
        // Hue Saturation Luminance - see https://tips4java.wordpress.com/2009/07/05/hsl-color/

        if (saturation < 0.0f) {
            saturation = 0;
        }
        if (saturation > 1.0f) {
            saturation = 1;
        }

        if (luminance < 0.0f || luminance > 1.0f) {
            String message = "Color parameter outside of expected range - Luminance";
            throw new IllegalArgumentException(message);
        }

        // Formula needs all values between 0 - 1.
        hue = hue % 360.0f;
        hue /= 360f;

        float q = 0;

        if (luminance < 0.5)
            q = luminance * (1 + saturation);
        else
            q = (luminance + saturation) - (saturation * luminance);

        float p = 2 * luminance - q;

        float r = Math.max(0, HueToRGB(p, q, hue + (1.0f / 3.0f)));
        float g = Math.max(0, HueToRGB(p, q, hue));
        float b = Math.max(0, HueToRGB(p, q, hue - (1.0f / 3.0f)));

        r = Math.min(r, 1.0f);
        g = Math.min(g, 1.0f);
        b = Math.min(b, 1.0f);

        return createColorRGB(r, g, b);
    }

    /**
     * Calculating the RGB Value of a HUE color
     *
     * @param p
     * @param q
     * @param h
     * @return RGB color
     */
    private static float HueToRGB(float p, float q, float h) {
        if (h < 0)
            h += 1;

        if (h > 1)
            h -= 1;

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return q;
        }

        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }

        return p;
    }

    /**
     * validate if the color channel is in a valid range
     * @param Color the color which is to check
     * @param value the color channel value
     */
    public static void validateColorComponent(String Color, int value) {
        if (value < 0 || value >= 256) {
            throw new IllegalArgumentException("Illegal Color value (" + value +
                    ") for '" + Color + "' - must be 0.." + Color_COMPONENT_MAX);
        }
    }

    /**
     * Get the red value of a color
     * @param Color provide the color
     * @return the red value
     */
    public static int getRedComponent(int Color) {
        return (Color & RED_MASK) >> 16;
    }

    /**
     * Set the red value of a color
     * @param Color provide the color
     * @param red provide the desired red value
     * @return the new color
     */
    public static int setRedComponent(final int Color, int red) {
        validateColorComponent("Red", red);
        int new_Color = Color & RED_OFF_MASK;
        new_Color |= red << 16;
        return new_Color;
    }

    /**
     * Get the green value of a color
     * @param Color provide the color
     * @return the green value
     */
    public static int getGreenComponent(int Color) {
        return (Color & GREEN_MASK) >> 8;
    }

    /**
     * Set the green value of a color
     * @param Color provide the color
     * @param green provide the desired red value
     * @return the new color
     */
    public static int setGreenComponent(final int Color, int green) {
        validateColorComponent("Green", green);
        int new_Color = Color & GREEN_OFF_MASK;
        new_Color |= green << 8;
        return new_Color;
    }

    /**
     * Get the blue value of a color
     * @param Color provide the color
     * @return the blue value
     */
    public static int getBlueComponent(int Color) {
        return Color & BLUE_MASK;
    }

    /**
     * Set the blue value of a color
     * @param Color provide the color
     * @param blue provide the desired red value
     * @return the new color
     */
    public static int setBlueComponent(final int Color, int blue) {
        validateColorComponent("Blue", blue);
        int new_Color = Color & BLUE_OFF_MASK;
        new_Color |= blue;
        return new_Color;
    }
}
