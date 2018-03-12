
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

        File seamOutputDir = new File("src/output-seams/");
        File[] files = seamOutputDir.listFiles();

        if (files != null) {
            for (File f : files) {
                if (!f.delete()) {
                    System.out.println("Issue deleting files in output folder");
                }
            }
        }
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File("src/" + filepath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        if (Integer.parseInt(inputWidth) != image.getWidth()) {
            if (Integer.parseInt(inputWidth) < image.getWidth()) image = ImageProcessor.reduceWidth(image, Integer.parseInt(inputWidth));
            if (Integer.parseInt(inputWidth) > image.getWidth()) image = ImageProcessor.increaseWidth(image, Integer.parseInt(inputWidth));
        }

        if (Integer.parseInt(inputHeight) != image.getHeight()) {
            if (Integer.parseInt(inputHeight) < image.getHeight()) image = ImageProcessor.reduceHeight(image, Integer.parseInt(inputHeight));
            if (Integer.parseInt(inputHeight) > image.getHeight()) image = ImageProcessor.increaseHeight(image, Integer.parseInt(inputHeight));
        }

        File outputDir = new File("src/output.png");
        ImageIO.write(image, "png", outputDir);
    }
}