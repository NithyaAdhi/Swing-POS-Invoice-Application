package com.posapp.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageUtil {


    private static final String IMAGE_STORAGE_DIR = "item_images"; // Creates a folder in the project root

    public static ImageIcon resizeImageIcon(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        try {

            System.out.println("Attempting to load image from: " + imagePath);

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.err.println("Image file not found at: " + imagePath);
                return null;
            }
            if (!imageFile.canRead()) {
                System.err.println("No read permission for image file at: " + imagePath);
                return null;
            }

            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                System.err.println("ImageIO.read returned null for: " + imagePath + ". (Possibly not a valid image format or corrupted)");
                return null;
            }

            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            System.err.println("Error loading or resizing image: " + imagePath);
            e.printStackTrace();
            return null;
        }
    }

    public static File chooseImageFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Item Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "png", "jpg", "jpeg", "gif"));
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }


    public static String copyImageToAppDirectory(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            System.err.println("Source image file is null or does not exist.");
            return null;
        }

        // Create the image storage directory if it doesn't exist
        Path storagePath = Paths.get(IMAGE_STORAGE_DIR);
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            System.err.println("Failed to create image storage directory: " + storagePath);
            e.printStackTrace();
            return null;
        }

        // Generate a unique filename
        String originalFilename = sourceFile.getName();
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path destinationPath = storagePath.resolve(uniqueFilename);

        try {
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copied to: " + destinationPath.toAbsolutePath().toString());
            return destinationPath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Failed to copy image " + originalFilename + " to " + destinationPath);
            e.printStackTrace();
            return null;
        }
    }
}