package org.example.demo4;

import javafx.scene.image.*;
import javafx.scene.paint.Color;

public class ImageProcessor {

    public static Image processingImage(Image image, String effect) {
        if (effect.equals("blur")) {
            Image blurred = image;
            for (int i = 0; i < 7; i++) {
                blurred = applySingleBlur(blurred);
            }
            return blurred;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        Pixel[][] pixels = new Pixel[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = argb & 0xff;

                Pixel pixel;
                switch (effect) {
                    case "grayscale":
                        pixel = grayscale(red, green, blue);
                        break;
                    case "inverse":
                        pixel = inverse(red, green, blue);
                        break;
                    default:
                        pixel = new Pixel(red, green, blue);
                }
                pixels[y][x] = pixel;
            }
        }

        WritableImage result = new WritableImage(width, height);
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel p = pixels[y][x];
                writer.setColor(x, y, new Color(p.getRed() / 255.0, p.getGreen() / 255.0, p.getBlue() / 255.0, 1.0));
            }
        }

        return result;
    }

    private static Image applySingleBlur(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader reader = image.getPixelReader();
        WritableImage result = new WritableImage(width, height);
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel p = blur(reader, x, y, width, height);
                writer.setColor(x, y, new Color(p.getRed() / 255.0, p.getGreen() / 255.0, p.getBlue() / 255.0, 1.0));
            }
        }

        return result;
    }

    private static Pixel grayscale(int r, int g, int b) {
        int avg = (r + g + b) / 3;
        return new Pixel(avg, avg, avg);
    }

    private static Pixel inverse(int r, int g, int b) {
        return new Pixel(255 - r, 255 - g, 255 - b);
    }
    private static Pixel blur(PixelReader reader, int x, int y, int width, int height) {
        int radius = 7;
        int rSum = 0, gSum = 0, bSum = 0;
        int count = 0;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int argb = reader.getArgb(nx, ny);
                    rSum += (argb >> 16) & 0xff;
                    gSum += (argb >> 8) & 0xff;
                    bSum += argb & 0xff;
                    count++;
                }
            }
        }

        return new Pixel(rSum / count, gSum / count, bSum / count);
    }
}
