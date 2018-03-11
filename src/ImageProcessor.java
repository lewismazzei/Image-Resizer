import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageProcessor {
    public static double[][] generateEnergyArray(BufferedImage image) {

        //get height and width of image
        int width = image.getWidth();
        int height = image.getHeight();

        //array to store the energy of each pixel
        double[][] energies = new double[height][width];

        //loop through each pixel of array
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                //temporary variables to hold the x and y values of the pixels we want to look at
                int x1; int x2 = x; int x3;
                int y1; int y2 = y; int y3;

                //if the pixel is on an edge then use the pixel on the directly opposite side (vertcally for the y value, horizontally for the x value)
                if (x == 0) {
                    //System.out.println(new Color(image.getRGB(x, y)).toString() + x + " " + y);
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
                energies[y][x] = energy;
            }
        }
        //return 2d array of energy values
        return energies;
    }

    public static int[] lowestEnergySeam(double[][] energies) {
        int duplicates = 0;
        //array to hold the indexes of each generated seam
        int[][] seamIndexes = new int[energies[0].length][energies.length];
        //array to hold the the summed energy value of each generated seam
        Map<Double, Integer> seamEnergies = new HashMap<>();
        //loop through each cell in the top row
        //System.out.println(energies[0].length);

        for (int startingCol = 0; startingCol < energies[0].length; startingCol++) {
            //variable to hold the next lowest energy column
            int nextIndex = startingCol;
            //set the first index of the current seam to the index of the starting column
            seamIndexes[startingCol][0] = nextIndex;
            //variable to hold the total energy for each seam
            double totalEnergy = 0;
            //loop through each row
            for (int row = 1; row < energies.length - 1; row++) {
                //find the next index in the seam
                nextIndex = nextIndex(energies, nextIndex, row);
                //record the index
                seamIndexes[startingCol][row] = nextIndex;
                //update total energy
                totalEnergy += energies[row][nextIndex];
            }
            //add total energy for current seam to hashmap with the corresponding final column index
            //if (seamEnergies.containsKey(totalEnergy)) duplicates++;
            seamEnergies.put(totalEnergy, startingCol);
        }

        int lowestStartingCol = seamEnergies.get(Collections.min(seamEnergies.keySet()));

        return seamIndexes[lowestStartingCol];


        //or (Map.Entry<Double, Integer> energy : seamEnergies.entrySet()) {
        //System.out.println(energy);
        //}
        //System.out.println("d" + duplicates);

        //double minEnergySum = Double.MAX_VALUE;
        //int[] lowestEnergySeam = new int[energies.length];
        //for (int col = 0; col < energies[0].length; col++) {
        //    if (energies[energies.length - 1][col] < minEnergySum) {
        //        minEnergySum = energies[energies.length - 1][col];
        //        lowestEnergySeam = seamIndexes[col];
        //    }
        //}
        //return lowestEnergySeam;
    }

    public static int nextIndex(double[][] energies, int col, int row) {
        //if the passed x or y value is outwith the legal bounds of the energy array throw an exception
        if (col < 0 || col >= energies[0].length || row < 0 || row >= energies.length) {
            throw new IllegalArgumentException();
        }
        //if the current cell is on the left edge then there are only two cells to look at below it
        //if (x == 0) {
        //    //define these cells...
        //    double e1 = energies[y+1][x];
        //    double e2 = energies[y+1][x+1];
        //    //...and determine what the lowest energy out of these two cells is
        //    double lowestEnergy = Math.min(e1, e2);
        ////if the current cell is on the right edge then there are only two cells to look at below it
        //} else if (x == energies[0].length - 1) {
        //    //define these cells...
        //    double e1 = energies[y+1][x-1];
        //    double e2 = energies[y+1][x];
        //    //...and determine what the lowest energy out of these two cells is
        //    double lowestEnergy;
        ////if the current cell is not on an edge there are 3 cells to look at below it
        //} else {
        //    //define these cells...
        //    double e1 = energies[y+1][x-1];
        //    double e2 = energies[y+1][x];
        //    double e3 = energies[y+1][x+1];
        //    //...and determine what the lowest energy out of these three cells is
        //    double lowestEnergy = Math.min(Math.min(e1, e2), e3);
        //}

        //get the energy values of the three cells below the current cell
        //if cell is on an edge then set the non-existent cell's energy value to an extremely high number so it isn't chosen
        double e1 = (col == 0) ? Double.MAX_VALUE : energies[row+1][col-1];
        double e2 = energies[row+1][col];
        double e3 = (col == energies[0].length - 1) ? Double.MAX_VALUE : energies[row+1][col+1];
        //return the x coordinate of the lowest energy cell
        if      (Math.min(Math.min(e1, e2), e3) == e1) return col - 1;
        else if (Math.min(Math.min(e1, e2), e3) == e2) return col;
        else if (Math.min(Math.min(e1, e2), e3) == e3) return col + 1;
        //mandatory defualt return value (will never be returned as it is impossible for all 3 of the above if statements to return false)
        return -1;
    }

    public static BufferedImage removeSeam(BufferedImage image, int[] lowestEnergySeam) {
        BufferedImage newImage = new BufferedImage(image.getWidth()-1, image.getHeight(), 5);
        for (int row = 0; row < newImage.getHeight(); row++) {
            boolean seamFound = false;
            for (int col = 0; col < newImage.getWidth(); col++) {
                Color color = new Color(image.getRGB(col, row));
                if (color.getRed() == 255 && color.getGreen() == 0 && color.getBlue() == 0 || seamFound) {
                    seamFound = true;
                    color = new Color(image.getRGB(col+1, row));
                    newImage.setRGB(col, row, getIntFromColor(color.getRed(), color.getGreen(), color.getBlue()));
                } else {
                    newImage.setRGB(col, row, getIntFromColor(color.getRed(), color.getGreen(), color.getBlue()));
                }
            }
        }
        return newImage;
    }

    public static int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
