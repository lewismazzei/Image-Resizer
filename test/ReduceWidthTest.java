import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReduceWidthTest {
    @Test
    public void reduceWidthTest() {
        BufferedImage inputImage = new BufferedImage(3,4, BufferedImage.TYPE_INT_RGB);
        for (int i=0; i<inputImage.getWidth(); i++) {
            for (int j=0; j<inputImage.getHeight(); j++) {
                if(j == 2) {
                    inputImage.setRGB(i, j, 5000);
                } else {
                    inputImage.setRGB(i, j, 10000);
                }
            }
        }

        BufferedImage expectedImage = new BufferedImage(2,4, BufferedImage.TYPE_INT_RGB);
        for (int i=0; i<expectedImage.getWidth(); i++) {
            for (int j=0; j<expectedImage.getHeight(); j++) {
                expectedImage.setRGB(i, j, 10000);
            }
        }


        try {
            BufferedImage outputImage = ImageProcessor.reduceWidth(inputImage, 2);

            assertTrue(outputImage.getWidth() == expectedImage.getWidth());
            assertTrue(outputImage.getHeight() == expectedImage.getHeight());
            for (int i = 0; i < expectedImage.getWidth(); i++) {
                for (int j = 0; j < expectedImage.getHeight(); j++) {
                    System.out.println(expectedImage.getRGB(i, j) + " " + outputImage.getRGB(i, j));
                    assertTrue(expectedImage.getRGB(i, j) == outputImage.getRGB(i, j));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
