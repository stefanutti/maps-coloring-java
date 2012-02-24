package it.tac.ct.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

// RB or GW --> CYAN (red is graphically better)
// RG or BW --> ORANGE (green)
// RW or GB --> MAGENTA (blue)
//
public class EdgeToFaceColoring {

    public static void main(String[] args) throws Exception {

        // Read the input image and create the empty output image
        //
        BufferedImage inputImage = ImageIO.read(new File("inputImage.png"));
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Scan the image
        //
        int previousColor = 0;
        int currentColor = 0;
        int nextColor = 0;
        for (int iY = 1; iY < inputImage.getHeight(); iY++) {
            for (int iX = 1; iX < inputImage.getWidth() - 1; iX++) {
                currentColor = inputImage.getRGB(iX, iY);

                if (currentColor == Color.BLACK.getRGB()) {

                    // Y (Vertical) scan - previous = LEFT, next = RIGHT
                    //
                    previousColor = inputImage.getRGB(iX - 1, iY);
                    nextColor = inputImage.getRGB(iX + 1, iY);

                    if (((previousColor == Color.RED.getRGB()) && (nextColor == Color.BLUE.getRGB())) || ((previousColor == Color.BLUE.getRGB()) && (nextColor == Color.RED.getRGB())) || ((previousColor == Color.GREEN.getRGB()) && (nextColor == Color.WHITE.getRGB())) || ((previousColor == Color.WHITE.getRGB()) && (nextColor == Color.GREEN.getRGB()))) {
                        outputImage.setRGB(iX - 1, iY, Color.RED.getRGB());
                        outputImage.setRGB(iX, iY, Color.RED.getRGB());
                        outputImage.setRGB(iX + 1, iY, Color.RED.getRGB());
                    }
                    if (((previousColor == Color.RED.getRGB()) && (nextColor == Color.GREEN.getRGB())) || ((previousColor == Color.GREEN.getRGB()) && (nextColor == Color.RED.getRGB())) || ((previousColor == Color.BLUE.getRGB()) && (nextColor == Color.WHITE.getRGB())) || ((previousColor == Color.WHITE.getRGB()) && (nextColor == Color.BLUE.getRGB()))) {
                        outputImage.setRGB(iX - 1, iY, Color.GREEN.getRGB());
                        outputImage.setRGB(iX, iY, Color.GREEN.getRGB());
                        outputImage.setRGB(iX + 1, iY, Color.GREEN.getRGB());
                    }
                    if (((previousColor == Color.RED.getRGB()) && (nextColor == Color.WHITE.getRGB())) || ((previousColor == Color.WHITE.getRGB()) && (nextColor == Color.RED.getRGB())) || ((previousColor == Color.GREEN.getRGB()) && (nextColor == Color.BLUE.getRGB())) || ((previousColor == Color.BLUE.getRGB()) && (nextColor == Color.GREEN.getRGB()))) {
                        outputImage.setRGB(iX - 1, iY, Color.BLUE.getRGB());
                        outputImage.setRGB(iX, iY, Color.BLUE.getRGB());
                        outputImage.setRGB(iX + 1, iY, Color.BLUE.getRGB());
                    }

                    // Y (Vertical) scan - previous = ABOVE, next = BELOW
                    //
                    previousColor = inputImage.getRGB(iX, iY - 1);
                    nextColor = inputImage.getRGB(iX, iY + 1);

                    if (((previousColor == Color.RED.getRGB()) && (nextColor == Color.BLUE.getRGB())) || ((previousColor == Color.BLUE.getRGB()) && (nextColor == Color.RED.getRGB())) || ((previousColor == Color.GREEN.getRGB()) && (nextColor == Color.WHITE.getRGB())) || ((previousColor == Color.WHITE.getRGB()) && (nextColor == Color.GREEN.getRGB()))) {
                        outputImage.setRGB(iX, iY - 1, Color.RED.getRGB());
                        outputImage.setRGB(iX, iY, Color.RED.getRGB());
                        outputImage.setRGB(iX, iY + 1, Color.RED.getRGB());
                    }
                    if (((previousColor == Color.RED.getRGB()) && (nextColor == Color.GREEN.getRGB())) || ((previousColor == Color.GREEN.getRGB()) && (nextColor == Color.RED.getRGB())) || ((previousColor == Color.BLUE.getRGB()) && (nextColor == Color.WHITE.getRGB())) || ((previousColor == Color.WHITE.getRGB()) && (nextColor == Color.BLUE.getRGB()))) {
                        outputImage.setRGB(iX, iY - 1, Color.GREEN.getRGB());
                        outputImage.setRGB(iX, iY, Color.GREEN.getRGB());
                        outputImage.setRGB(iX, iY + 1, Color.GREEN.getRGB());
                    }
                    if (((previousColor == Color.RED.getRGB()) && (nextColor == Color.WHITE.getRGB())) || ((previousColor == Color.WHITE.getRGB()) && (nextColor == Color.RED.getRGB())) || ((previousColor == Color.GREEN.getRGB()) && (nextColor == Color.BLUE.getRGB())) || ((previousColor == Color.BLUE.getRGB()) && (nextColor == Color.GREEN.getRGB()))) {
                        outputImage.setRGB(iX, iY - 1, Color.BLUE.getRGB());
                        outputImage.setRGB(iX, iY, Color.BLUE.getRGB());
                        outputImage.setRGB(iX, iY + 1, Color.BLUE.getRGB());
                    }
                } else {
                    outputImage.setRGB(iX, iY, Color.WHITE.getRGB());
                }
            }
        }

        // Store the image using the PNG format
        //
        ImageIO.write(outputImage, "png", new File("outputImage.png"));
    }
}
