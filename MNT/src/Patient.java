import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Patient extends GameObject {
    private String imagePath1, imagePath2, imagePath3;

    public Patient(int x, int y, int width, int height, String path1, String path2, String path3) {
        super(x, y, width, height, loadImage(path1), false);
        this.name = "Patient";
        this.imagePath1 = path1;
        this.imagePath2 = path2;
        this.imagePath3 = path3;
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Error loading patient image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public void updateState(int score) {
        String newImagePath;
        if (score >= 80) {
            newImagePath = imagePath2; // スコアが高い場合
        } else {
            newImagePath = imagePath3; // スコアが低い場合
        }

        try {
            this.currentImage = loadImage(newImagePath); // currentImageを更新
            this.originalImage = loadImage(newImagePath); // originalImageも更新して状態を固定
        } catch (Exception e) {
            System.err.println("Error updating patient image: " + newImagePath);
            e.printStackTrace();
        }
    }

    public void resetState() {
        try {
            this.currentImage = loadImage(imagePath1); // 初期画像に戻す
            this.originalImage = loadImage(imagePath1); // オリジナル画像も初期画像に設定
        } catch (Exception e) {
            System.err.println("Error resetting patient image.");
            e.printStackTrace();
        }
    }
}