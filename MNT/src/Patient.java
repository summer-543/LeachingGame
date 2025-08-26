import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Patient extends GameObject {
    private final String imagePath1;
    private final String imagePath2;
    private final String imagePath3;

    public Patient(int x, int y, int width, int height, String imagePath1, String imagePath2, String imagePath3) {
        super(x, y, width, height, null, false);
        this.imagePath1 = "/images/" + imagePath1;
        this.imagePath2 = "/images/" + imagePath2;
        this.imagePath3 = "/images/" + imagePath3;
        
        try {
            this.currentImage = ImageIO.read(getClass().getResource(this.imagePath1));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading initial patient image.");
            e.printStackTrace();
        }
    }

    public void updateState(int score) {
        try {
            BufferedImage newImage = null;
            if (score >= 80) {
                newImage = ImageIO.read(getClass().getResource(imagePath2));
            } else {
                newImage = ImageIO.read(getClass().getResource(imagePath3));
            }
            if (newImage != null) {
                this.currentImage = newImage;
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error updating patient image.");
            e.printStackTrace();
        }
    }
    
    public void resetState() {
        try {
            this.currentImage = ImageIO.read(getClass().getResource(imagePath1));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error resetting patient image.");
            e.printStackTrace();
        }
    }
}