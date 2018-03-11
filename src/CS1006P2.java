
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CS1006P2 {

    public static void main(String[] args) throws IOException {
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

        for (int i = 0; i < 100; i++) {
            double[][] energies = ImageProcessor.generateEnergyArray(image);
            int[] lowestEnergySeam = ImageProcessor.lowestEnergySeam(energies);
            for (int row = 0; row < image.getHeight(); row++) {
                image.setRGB(lowestEnergySeam[row], row, 16711680);
            }

            File outputFile = new File("src/output-seams/" + i + ".png");
            ImageIO.write(image, "png", outputFile);

            image = removeSeam(image, lowestEnergySeam);

            outputFile = new File("src/output/" + i + ".png");
            ImageIO.write(image, "png", outputFile);
        }

        //System.out.println("\n----------------------------------\n");

        //for (int energy : lowestEnergySeam) {
        //System.out.println(energy);
    }
}

