import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnergyArrayTest {
    @Test
    public void energyArrayTest() {
        BufferedImage inputImage = new BufferedImage(3,3, BufferedImage.TYPE_INT_RGB);
        Color[] colors = new Color[]{Color.BLUE, Color.BLACK, Color.YELLOW, Color.CYAN, Color.DARK_GRAY, Color.WHITE, Color.RED, Color.GREEN, Color.MAGENTA};

        int count = 0;
        for (int i=0; i<inputImage.getHeight(); i++) {
            for (int j=0; j<inputImage.getWidth(); j++) {
                inputImage.setRGB(j, i, colors[count].getRGB());
                count++;
            }
        }

        double[][] expectedEnergyArray = new double[][]{
                {390150, 268292, 325125},
                {333062, 211204, 268037},
                {390150, 268292, 325125}
        };

        double[][] outputArray = ImageProcessor.generateEnergyArray(inputImage);

        assertTrue(Arrays.deepEquals(expectedEnergyArray, outputArray));
    }
}
