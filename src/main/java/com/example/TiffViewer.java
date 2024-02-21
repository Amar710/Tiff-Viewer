package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

// Class TiffViewer creates a GUI application for viewing and manipulating TIFF images.
public class TiffViewer {

    // Member variables for the GUI components and state management.
    private JFrame frame;
    private JLabel leftImageLabel;
    private JLabel rightImageLabel;
    private JPanel imagePanel;
    private static final Logger LOGGER = Logger.getLogger(TiffViewer.class.getName());
    private int currentStep = 0;
    private BufferedImage currentImage;

    // Constructor sets up the GUI.
    public TiffViewer() {
        // Main application window
        frame = new JFrame("TIFF Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 600);

        // Menu bar for file operations
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // File menu with open and exit actions
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Menu item to open a TIFF file
        JMenuItem openFileItem = new JMenuItem("Open File");
        openFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(openFileItem);

        // Menu item to exit the application
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        // Button to advance to the next image processing step
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                advanceStep();
            }
        });

        // Panel to hold image labels
        imagePanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Arrange side by side
        leftImageLabel = new JLabel();
        rightImageLabel = new JLabel();
        imagePanel.add(leftImageLabel);
        imagePanel.add(rightImageLabel);

        // Adding components to the frame
        frame.setLayout(new BorderLayout());
        frame.add(nextButton, BorderLayout.SOUTH);
        frame.add(imagePanel, BorderLayout.CENTER);

        // Show the GUI
        frame.setVisible(true);
    }

    // Method to open a file and read an image.
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TIFF Images", "tif", "tiff");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                currentImage = ImageIO.read(fileChooser.getSelectedFile());
                processImage(currentImage);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error reading the image", ex);
            }
        }
    }

    // Process the loaded image and display the initial view.
    private void processImage(BufferedImage image) {
        currentStep = 0;
        displayImages(image, convertToGrayscale(image));
    }

    // Advance to the next image processing step based on the currentStep counter.
    private void advanceStep() {
        // Depending on the current step, apply different image processing techniques
        // and update the display.
        switch (currentStep) {
            case 0:
                displayImages(reduceBrightness(currentImage, 0.5f), reduceBrightness(convertToGrayscale(currentImage), 0.5f));
                break;
            case 1:
                BufferedImage grayscale = convertToGrayscale(currentImage);
                displayImages(grayscale, orderedDithering(grayscale));
                break;
            case 2:
                displayImages(currentImage, autoLevel(currentImage));
                break;
            case 3:
                displayImages(currentImage, convertToGrayscale(currentImage));
                break;
        }
        currentStep = (currentStep + 1) % 4; // Cycle back after the last step
    }

    // Display the given left and right images in the GUI.
    private void displayImages(BufferedImage left, BufferedImage right) {
        leftImageLabel.setIcon(new ImageIcon(left));
        rightImageLabel.setIcon(new ImageIcon(right));
        frame.revalidate();
        frame.repaint();
    }

    // Methods: convertToGrayscale, reduceBrightness, orderedDithering, autoLevel
    private BufferedImage convertToGrayscale(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage grayscale = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = original.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                int grayValue = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                int grayRGB = (grayValue << 16) + (grayValue << 8) + grayValue;
                grayscale.setRGB(x, y, grayRGB);
            }
        }
        return grayscale;
    }
    private BufferedImage reduceBrightness(BufferedImage original, float factor) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage dimmed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = original.getRGB(x, y);
                int red = (int) (((rgb >> 16) & 0xFF) * factor);
                int green = (int) (((rgb >> 8) & 0xFF) * factor);
                int blue = (int) ((rgb & 0xFF) * factor);
                int newRGB = (red << 16) + (green << 8) + blue;
                dimmed.setRGB(x, y, newRGB);
            }
        }
        return dimmed;
    }
    private BufferedImage orderedDithering(BufferedImage grayscale) {
        int width = grayscale.getWidth();
        int height = grayscale.getHeight();
        BufferedImage dithered = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        
        int[][] ditherMatrix = {
            {0, 128},
            {192, 64}
        };
    
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = new Color(grayscale.getRGB(x, y)).getRed();
                if (grayValue > ditherMatrix[y % 2][x % 2]) {
                    dithered.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    dithered.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return dithered;
    }
    private BufferedImage autoLevel(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage leveled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
        int minRed = 255, maxRed = 0;
        int minGreen = 255, maxGreen = 0;
        int minBlue = 255, maxBlue = 0;
    
        // Find the min and max values for each channel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = new Color(original.getRGB(x, y));
                minRed = Math.min(minRed, pixel.getRed());
                maxRed = Math.max(maxRed, pixel.getRed());
                minGreen = Math.min(minGreen, pixel.getGreen());
                maxGreen = Math.max(maxGreen, pixel.getGreen());
                minBlue = Math.min(minBlue, pixel.getBlue());
                maxBlue = Math.max(maxBlue, pixel.getBlue());
            }
        }
    
        // Apply auto leveling to each channel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = new Color(original.getRGB(x, y));
                int newRed = 255 * (pixel.getRed() - minRed) / (maxRed - minRed);
                int newGreen = 255 * (pixel.getGreen() - minGreen) / (maxGreen - minGreen);
                int newBlue = 255 * (pixel.getBlue() - minBlue) / (maxBlue - minBlue);
                Color newPixel = new Color(newRed, newGreen, newBlue);
                leveled.setRGB(x, y, newPixel.getRGB());
            }
        }
    
        return leveled;
    }
    
    
    
    
    // Run the Application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TiffViewer();
            }
        });
    }
}
