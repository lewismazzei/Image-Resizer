import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JavaSwing extends JFrame implements KeyListener {
    public static boolean showSeam = false; //the generateseam method creates an ArrayOutOfBounds because of incorrect image size hence disabled
    private BufferedImage image;
    private JLabel label;
    private GridBagConstraints gbc = new GridBagConstraints();
    private boolean resizing = false;
    private int interval;

    /**
     * The JavaSwing window that will allow manipulation of the image
     * @param image the input image
     * @param interval the interval size, in pixels, of each edit
     */
    public JavaSwing(BufferedImage image, int interval) {
        this.image = image;
        this.interval = interval;

        JPanel panel = new JPanel(new GridBagLayout()); //this panel contains all elements inside the window
        setSize(1920, 1080); //full screen on an HD monitor
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); //this allows aligning the image in a way that doesn't centre it on the screen
        gbc.anchor = GridBagConstraints.NORTHWEST; //top left corner of the screen
        gbc.weightx = 1; //the component needs a weight for the positioning to have any effect
        gbc.weighty = 1;
        add(panel, gbc);

        this.label = new JLabel(); //label is the best non editable way to contain and display the image

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        label.setFocusable(true); //this allows for the label to register key presses

        panel.add(label, gbc);

        label.setIcon(new ImageIcon(image)); //display the input image
        label.requestFocus(); //request keyboard focus
        label.addKeyListener(this); //register this class as the place where the keyboard listening methods are

        setVisible(true); //finally, display the window
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (resizing) { //this is intended to block resizing while resizing is already in process
            return;
        }
        try {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                resizing = true;
                BufferedImage outputImage = image;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        outputImage = ImageProcessor.increaseWidth(image, image.getWidth() + interval);
                        break;
                    case KeyEvent.VK_LEFT:
                        outputImage = ImageProcessor.reduceWidth(image, image.getWidth() - interval);
                        break;
                    case KeyEvent.VK_UP:
                        outputImage = ImageProcessor.increaseHeight(image, image.getHeight() + interval);
                        break;
                    case KeyEvent.VK_DOWN:
                        outputImage = ImageProcessor.reduceHeight(image, image.getHeight() - interval);
                        break;
                }
                if (outputImage != null) {
                    label.setIcon(new ImageIcon(outputImage));
                    image = outputImage;
                } else {
                    System.out.println("Error processing image!");
                    System.exit(1);
                }
            }
        } catch (IOException ex) {
            System.out.println("An error occurred while processing the file, please try again.");
        } finally {
            resizing = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java JavaSwing <image path> <change interval>");
        } else {
            try {
                BufferedImage image = ImageIO.read(new File(args[0]));
                int interval = 1;
                if (args.length == 2) {
                    interval = Integer.parseInt(args[1]);
                }
                new JavaSwing(image, interval);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found, please check file path.");
                System.exit(1);
            } catch (IOException ex) {
                System.out.println("Error reading file, please check file path and permissions.");
                System.exit(1);
            } catch (NumberFormatException ex) {
                System.out.println("One of your inputs was not the right type.");
                System.out.println("Usage: java JavaSwing <image path> <change interval>");
                System.exit(1);
            }
        }
    }
}
