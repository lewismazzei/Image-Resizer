import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
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

                //if the pixel is on an edge then use the pixel on the directly opposite side (vertically for the y value, horizontally for the x value)
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
                energies[y][x] = energy;
            }
        }
        //return 2d array of energy values
        return energies;
    }

    public static BufferedImage reduceWidth(BufferedImage image, int inputWidth) throws IOException{
        int originalWidth = image.getWidth();
        for (int i = 0; i < originalWidth - inputWidth; i++) {
            double[][] energies = generateEnergyArray(image);
            int[] lowestEnergySeam = lowestVerticalEnergySeam(energies);

            //generate a set of images displaying what seams were selected

            if (JavaSwing.showSeam) {
                generateSeamOutput(image, lowestEnergySeam, i, 1);
            }

            image = removeVerticalSeam(image, lowestEnergySeam);

        }
        return image;
    }

    public static BufferedImage reduceHeight(BufferedImage image, int inputHeight) throws IOException{
        int originalHeight = image.getHeight();
        for (int i = 0; i < originalHeight - inputHeight; i++) {
            double[][] energies = generateEnergyArray(image);
            int[] lowestEnergySeam = lowestHorizontalEnergySeam(energies);

            //generate a set of images displaying what seams were selected
            if (JavaSwing.showSeam) {
                generateSeamOutput(image, lowestEnergySeam, i, 2);
            }

            image = removeHorizontalSeam(image, lowestEnergySeam);

            //File outputFile = new File("src/output/" + i + ".png");
            //ImageIO.write(image, "png", outputFile);
        }
        return image;
    }

    public static BufferedImage increaseWidth(BufferedImage image, int inputWidth) throws IOException {
        ColorModel cm = image.getColorModel();
        boolean alpha = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage shrinkImage = new BufferedImage(cm, raster, alpha, null);
        BufferedImage seamImage = new BufferedImage(cm, raster, alpha, null);
        int originalWidth = image.getWidth();
        int[][] lowestEnergySeams = new int[inputWidth - originalWidth][image.getHeight()];
        for (int i = 0; i < inputWidth - originalWidth; i++) {
            double[][] energies = generateEnergyArray(shrinkImage);
            lowestEnergySeams[i] = lowestVerticalEnergySeam(energies);
            shrinkImage = removeVerticalSeam(shrinkImage, lowestEnergySeams[i]);
        }
        for (int i = 0; i < lowestEnergySeams.length; i++) {
            if (JavaSwing.showSeam) {
                generateSeamOutput(image, lowestEnergySeams[i], i, 3);
            }

            seamImage = addVerticalSeam(seamImage, lowestEnergySeams[i]);

            image = addVerticalSeam(image, lowestEnergySeams[i]);

            //File outputFile = new File("src/output/" + i + ".png");
            //ImageIO.write(image, "png", outputFile);
        }
        return image;
    }

    public static BufferedImage increaseHeight(BufferedImage image, int inputHeight) throws IOException {
        ColorModel cm = image.getColorModel();
        boolean alpha = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage shrinkImage = new BufferedImage(cm, raster, alpha, null);
        BufferedImage seamImage = new BufferedImage(cm, raster, alpha, null);
        int originalHeight = image.getHeight();
        int[][] lowestEnergySeams = new int[inputHeight - originalHeight][image.getWidth()];
        for (int i = 0; i < inputHeight - originalHeight; i++) {
            double[][] energies = generateEnergyArray(shrinkImage);
            lowestEnergySeams[i] = lowestHorizontalEnergySeam(energies);
            shrinkImage = removeVerticalSeam(shrinkImage, lowestEnergySeams[i]);
        }
        for (int i = 0; i < lowestEnergySeams.length; i++) {
            if (JavaSwing.showSeam) {
                generateSeamOutput(image, lowestEnergySeams[i], i, 4);
            }

            //generateSeamOutput(seamImage, lowestEnergySeams[i], i, 4);
            //seamImage = addHorizontalSeam(seamImage, lowestEnergySeams[i]);
            //for (int row = 0; row < image.getHeight(); row++) {
            //    seamImage.setRGB(lowestEnergySeams[i][row], row, 16711680);
            //}
            //File outputFile = new File("src/output-seams/" + i + ".png");
            //ImageIO.write(seamImage, "png", outputFile);
            //seamImage = addVerticalSeam(seamImage, lowestEnergySeams[i]);



            image = addHorizontalSeam(image, lowestEnergySeams[i]);

            //File outputFile = new File("src/output/" + i + ".png");
            //ImageIO.write(image, "png", outputFile);
        }
        return image;
    }

    public static BufferedImage removeVerticalSeam(BufferedImage image, int[] lowestEnergySeam) {
        BufferedImage newImage = new BufferedImage(image.getWidth()-1, image.getHeight(), image.getType());
        for (int row = 0; row < newImage.getHeight(); row++) {
            boolean seamFound = false;
            for (int col = 0; col < newImage.getWidth(); col++) {
                Color color = new Color(image.getRGB(col, row));
                if (col == lowestEnergySeam[row] || seamFound) {
                    color = new Color(image.getRGB(col+1, row));
                    seamFound = true;
                }
                newImage.setRGB(col, row, color.getRGB());
            }
        }
        return newImage;
    }

    private static BufferedImage addVerticalSeam(BufferedImage image, int[] lowestEnergySeam) {
        BufferedImage newImage = new BufferedImage(image.getWidth()+1, image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int row = 0; row < newImage.getHeight(); row++) {
            boolean seamFound = false;
            for (int col = 0; col < newImage.getWidth() - 1; col++) {
                Color color = new Color(image.getRGB(col, row));
                if (col == lowestEnergySeam[row]+1 || seamFound) {
                    color = new Color(image.getRGB(col-1, row));
                    seamFound = true;
                }
                newImage.setRGB(col, row, color.getRGB());
            }
        }
        return newImage;
    }

    private static BufferedImage addHorizontalSeam(BufferedImage image, int[] lowestEnergySeam) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight()+1, BufferedImage.TYPE_3BYTE_BGR);
        for (int col = 0; col < newImage.getWidth(); col++) {
            boolean seamFound = false;
            for (int row = 0; row < newImage.getHeight() - 1; row++) {
                Color color = new Color(image.getRGB(col, row));
                if (row == lowestEnergySeam[col]+1 || seamFound) {
                    color = new Color(image.getRGB(col, row-1));
                    seamFound = true;
                }
                newImage.setRGB(col, row, color.getRGB());
            }
        }
        return newImage;
    }

    public static BufferedImage removeHorizontalSeam(BufferedImage image, int[] lowestEnergySeam) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight()-1, image.getType());
        for (int col = 0; col < newImage.getWidth(); col++) {
            boolean seamFound = false;
            for (int row = 0; row < newImage.getHeight(); row++) {
                Color color = new Color(image.getRGB(col, row));
                if (row == lowestEnergySeam[col] || seamFound) {
                    color = new Color(image.getRGB(col, row+1));
                    seamFound = true;
                }
                newImage.setRGB(col, row, color.getRGB());
            }
        }
        return newImage;
    }

    //determines the seam with lowest cumulative energy
    public static int[] lowestVerticalEnergySeam(double[][] energies) {
        //array to hold the indexes of each generated seam
        int[][] seamIndexes = new int[energies[0].length][energies.length];
        //array to hold the the summed energy value of each generated seam
        Map<Double, Integer> seamEnergies = new HashMap<>();
        //loop through each cell in the top row
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
                nextIndex = nextVerticalIndex(energies, nextIndex, row);
                //record the index
                seamIndexes[startingCol][row] = nextIndex;
                //update total energy
                totalEnergy += energies[row][nextIndex];
            }
            //add total energy for current seam to hashmap with the corresponding final column index
            seamEnergies.put(totalEnergy, startingCol);
        }

        int lowestStartingCol = seamEnergies.get(Collections.min(seamEnergies.keySet()));

        return seamIndexes[lowestStartingCol];
    }

    //determines the seam with lowest cumulative energy
    public static int[] lowestHorizontalEnergySeam(double[][] energies) {
        //array to hold the indexes of each generated seam
        int[][] seamIndexes = new int[energies.length][energies[0].length];
        //array to hold the the summed energy value of each generated seam
        Map<Double, Integer> seamEnergies = new HashMap<>();
        //loop through each cell in the top row
        for (int startingRow = 0; startingRow < energies.length; startingRow++) {
            //variable to hold the next lowest energy column
            int nextIndex = startingRow;
            //set the first index of the current seam to the index of the starting column
            seamIndexes[startingRow][0] = nextIndex;
            //variable to hold the total energy for each seam
            double totalEnergy = 0;
            //loop through each row
            for (int col = 1; col < energies[0].length - 1; col++) {
                //find the next index in the seam
                nextIndex = nextHorizontalIndex(energies, col, nextIndex);
                //record the index
                seamIndexes[startingRow][col] = nextIndex;
                //update total energy
                totalEnergy += energies[nextIndex][col];
            }
            //add total energy for current seam to hashmap with the corresponding final column index
            seamEnergies.put(totalEnergy, startingRow);
        }

        int lowestStartingRow = seamEnergies.get(Collections.min(seamEnergies.keySet()));

        return seamIndexes[lowestStartingRow];
    }

    public static int nextVerticalIndex(double[][] energies, int col, int row) {
        //if the passed x or y value is outwith the legal bounds of the energy array throw an exception
        if (col < 0 || col >= energies[0].length || row < 0 || row >= energies.length) {
            throw new IllegalArgumentException();
        }

        //get the energy values of the three cells below the current cell
        //if cell is on an edge then set the non-existent cell's energy value to an extremely high number so it isn't chosen
        double e1 = (col == 0) ? Double.MAX_VALUE : energies[row+1][col-1];
        double e2 = energies[row+1][col];
        double e3 = (col == energies[0].length - 1) ? Double.MAX_VALUE : energies[row+1][col+1];
        //
        return lowestEnergyIndex(e1, e2, e3, col);
    }

    public static int nextHorizontalIndex(double[][] energies, int col, int row) {
        //if the passed x or y value is outwith the legal bounds of the energy array throw an exception
        if (col < 0 || col >= energies[0].length || row < 0 || row >= energies.length) {
            throw new IllegalArgumentException();
        }

        //get the energy values of the three cells below the current cell
        //if cell is on an edge then set the non-existent cell's energy value to an extremely high number so it isn't chosen
        double e1 = (row == 0) ? Double.MAX_VALUE : energies[row-1][col+1];
        double e2 = energies[row][col+1];
        double e3 = (row == energies.length - 1) ? Double.MAX_VALUE : energies[row+1][col+1];

        return lowestEnergyIndex(e1, e2, e3, row);

        //mandatory defualt return value (will never be returned as it is impossible for all 3 of the above if statements to return false)
    }

    public static int lowestEnergyIndex(double e1, double e2, double e3, int col) {
        //return the x coordinate of the lowest energy cell
        if (Math.min(Math.min(e1, e2), e3) == e1) {
            return col - 1;
        } else if (Math.min(Math.min(e1, e2), e3) == e2) {
            return col;
        } else {
            return col + 1;
        }

        //double maxEnergy = Math.max(Math.max(e1, e2), e3);
        //if (e1 < e2) {
        //    if (e1 < e3) {
        //        return col - 1;
        //    } else {
        //        return col + 1;
        //    }
        //} else if (e2 < e3){
        //    return col;
        //} else {
        //    return col + 1;
        //}
    }

    private static void generateSeamOutput(BufferedImage image, int[] lowestEnergySeam, int i, int type) throws IOException{
        if (image == null || lowestEnergySeam.length == 0 || i < 0 || type < 1 || type > 4) {
            throw new IllegalArgumentException();
        }

        //lowestenergyseam is the wrong size

        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        image = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        File outputFile;

        switch (type) {
            case 1:
                for (int col = 0; col < image.getWidth(); col++) {
                    image.setRGB(lowestEnergySeam[col], col, 16711680);
                    outputFile = new File("src/output-seams/" + i + ".png");
                    ImageIO.write(image, "png", outputFile);
                }
                break;

            case 2:
                for (int row = 0; row < image.getHeight(); row++) {
                    image.setRGB(row, lowestEnergySeam[row], 16711680);
                    outputFile = new File("src/output-seams/" + i + ".png");
                    ImageIO.write(image, "png", outputFile);
                }
                break;

            case 3:
                for (int col = 0; col < image.getWidth(); col++) {
                    image.setRGB(lowestEnergySeam[col], col, 16711680);
                    outputFile = new File("src/output-seams/" + i + ".png");
                    ImageIO.write(image, "png", outputFile);
                }
                break;

            case 4:
                for (int row = 0; row < image.getHeight(); row++) {
                    image.setRGB(row, lowestEnergySeam[row], 16711680);
                    outputFile = new File("src/output-seams/" + i + ".png");
                    ImageIO.write(image, "png", outputFile);
                }
                break;
        }
    }
}
