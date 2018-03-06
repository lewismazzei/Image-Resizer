
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CS1006P2 {

    public static void main(String[] args) {
        String filepath = "";
        String inputWidth = "";
        String inputHeight = "";

        if (args.length == 3) {
            filepath = args[0];
            inputWidth = args[1];
            inputHeight = args[2];
        } else {
            System.out.println("Incorrect usage. Please use: java CS1006P2 <filepath> <width> <height>");
        }

        BufferedImage image = null;

        try {
            image = ImageIO.read(new File("src/" + filepath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        double[][] energies = generateEnergyArray(image);
    }

    public static double[][] generateEnergyArray(BufferedImage image) {

        //get height and width of image
        int width = image.getWidth();
        int height = image.getHeight();

        //array to store the energy of each pixel
        double[][] energies = new double[width][height];

        //loop through each pixel of array
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                //temporary variables to hold the x and y values of the pixels we want to look at
                int x1; int x2 = x; int x3;
                int y1; int y2 = y; int y3;

                //if the pixel is on an edge then use the pixel on the directly opposite side (vertcally for the y value, horizontally for the x value)
                if (x == 0) {
                    x1 = image.getWidth() - 1;
                } else {
                    x1 = x - 1;
                }
                if (x == image.getWidth() - 1) {
                    x3 = 0;
                } else {
                    x3 = x + 1;
                }
                if (y == 0) {
                    y1 = image.getHeight() - 1;
                } else {
                    y1 = y - 1;
                }
                if (y == image.getHeight() - 1) {
                    y3 = 0;
                } else {
                    y3 = y + 1;
                }

                //instantiate pixel objects for the current pixel and the pixels to the left and right of it
                Color p1x = new Color(image.getRGB(x1, y));
                Color p2x = new Color(image.getRGB(x2, y));
                Color p3x = new Color(image.getRGB(x3, y));
                //calculate the central differences between the values of the red, green and blue pixel values
                int deltaRx = Math.max(Math.max(p1x.getRed(), p2x.getRed()), p3x.getRed()) - Math.min(Math.min(p1x.getRed(), p2x.getRed()), p3x.getRed());
                int deltaGx = Math.max(Math.max(p1x.getGreen(), p2x.getGreen()), p3x.getGreen()) - Math.min(Math.min(p1x.getGreen(), p2x.getGreen()), p3x.getGreen());
                int deltaBx = Math.max(Math.max(p1x.getBlue(), p2x.getBlue()), p3x.getBlue()) - Math.min(Math.min(p1x.getBlue(), p2x.getBlue()), p3x.getBlue());
                //instantiate pixel objects for the current pixel and the pixels above and below it
                Color p1y = new Color(image.getRGB(x, y1));
                Color p2y = new Color(image.getRGB(x, y2));
                Color p3y = new Color(image.getRGB(x, y3));
                //calculate the central differences between the values of the red, green and blue pixel values
                int deltaRy = Math.max(Math.max(p1y.getRed(), p2y.getRed()), p3y.getRed()) - Math.min(Math.min(p1y.getRed(), p2y.getRed()), p3y.getRed());
                int deltaGy = Math.max(Math.max(p1y.getGreen(), p2y.getGreen()), p3y.getGreen()) - Math.min(Math.min(p1y.getGreen(), p2y.getGreen()), p3y.getGreen());
                int deltaBy = Math.max(Math.max(p1y.getBlue(), p2y.getBlue()), p3y.getBlue()) - Math.min(Math.min(p1y.getBlue(), p2y.getBlue()), p3y.getBlue());
                //calculate the energy of the current pixel by summing all delta values
                double energy = (Math.pow(deltaRx, 2) + Math.pow(deltaGx, 2) + Math.pow(deltaBx, 2)) +
                                (Math.pow(deltaRy, 2) + Math.pow(deltaGy, 2) + Math.pow(deltaBy, 2));
                //add current energy to energy array in the appropriate location
                energies[x][y] = energy;
            }
        }
        //return 2d array of energy values
        return energies;
    }
}

