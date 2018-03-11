
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

        File outputDir = new File("src/output");
        try {
            for (File f : outputDir.listFiles()) {
                f.delete();
            }
            outputDir = new File("src/output-seams");
            for (File f : outputDir.listFiles()) {
                f.delete();
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        BufferedImage image = null;

        try {
            image = ImageIO.read(new File("src/" + filepath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        if (Integer.parseInt(inputWidth) < image.getWidth()) ImageProcessor.reduceWidth(image, Integer.parseInt(inputWidth));
        if (Integer.parseInt(inputWidth) > image.getWidth()) ImageProcessor.increaseWidth(image, Integer.parseInt(inputWidth));

        //if (Integer.parseInt(inputWidth) < image.getHeight()) ImageProcessor.reduceHeight(image, inputHeight);
        //if (Integer.parseInt(inputWidth) > image.getHeight()) ImageProcessor.increaseHeight(image, inputHeight);
    }
}

