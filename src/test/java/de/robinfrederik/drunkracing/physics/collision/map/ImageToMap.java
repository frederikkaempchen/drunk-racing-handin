package de.robinfrederik.drunkracing.physics.collision.map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageToMap {
    public static byte[][] GetImageMatrix (String image,
                                      int[][] colors,
                                      byte[] assignments) {
        try {
            int[] packedColors = new int[colors.length];
            for (int i = 0; i < colors.length; i++) {
                packedColors[i] = ((colors[i][0] << 24) | (colors[i][1] << 16) | (colors[i][2] << 8) | (colors[i][3]));
            }

            final BufferedImage bufferedImage = ImageIO.read(new File(image));
            int imageHeight = bufferedImage.getHeight();
            int imageWidth = bufferedImage.getWidth();

            byte[][] imageMatrix = new byte[imageHeight][imageWidth];
            int currentColor = 0;
            for (int i = 0; i < imageHeight; i++) {
                for (int j = 0; j < imageWidth; j++) {
                    int colorPixel = bufferedImage.getRGB(j, i);
                    if (colorPixel != currentColor) {
                        currentColor = colorPixel;
                    }
                    for (int k = 0; k < packedColors.length; k++) {
                        if (colorPixel == packedColors[k]) {
                            imageMatrix[i][j] = assignments[k];
                            break;
                        }
                        else {
                            imageMatrix[i][j] = 0;
                        }
                    }
                }
            }

            return imageMatrix;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int[][] colors = new int[][] {
                {255, 255, 0, 0},
                {255, 0, 0, 0}
        };

        byte[] assignments = new byte[] {
                1,
                2
        };

        String imageLocation = "/Users/frederikkaempchen/IdeaProjects/drunk-racing/src/test/java/de/robinfrederik/drunkracing/physics/collision/map/assets/mapTest2.png";

        byte[][] imageMatrix = GetImageMatrix(imageLocation, colors, assignments);

        for (int i = 0; i < imageMatrix.length; i+=200) {
            String line = "";
            for (int j = 0; j < imageMatrix[0].length; j+=100) {
                line += (imageMatrix[i][j] == 2 ? imageMatrix[i][j] : " ") + " ";
            }
            System.out.println(line);
        }
    }
}