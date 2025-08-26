import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp; // RescaleOpのインポートを追加

public class GameObject {
    protected int x, y, width, height;
    protected BufferedImage originalImage; // 元の画像を保持
    protected BufferedImage currentImage;  // 現在の表示画像を保持
    protected String name;
    protected boolean draggable;

    public GameObject(int x, int y, int width, int height, BufferedImage image, boolean draggable) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.originalImage = image; // 初期化時にoriginalImageに設定
        this.currentImage = image;  // 初期化時にcurrentImageにも設定
        this.draggable = draggable;
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(currentImage, x, y, width, height, null); // ⭐currentImageを描画
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return currentImage;
    }
    
    // GameObjectが持つ画像をリセットするメソッド
    public void resetImage() {
        this.currentImage = this.originalImage;
    }

    // 画像を暗くするメソッド
    public void darkenImage() {
        // RGB値を暗くするためのファクタ (0.0f - 1.0f)
        float factor = 0.7f; // 70%の明るさに
        float offset = 0.0f;
        RescaleOp op = new RescaleOp(factor, offset, null);
        this.currentImage = op.filter(originalImage, null); // originalImageを基に暗くする
    }

    // 画像を元の明るさに戻すメソッド（リセット用）
    public void restoreImage() {
        this.currentImage = this.originalImage;
    }

    // 透過部分を考慮した当たり判定ロジック (変更なし、念のため再掲)
    public boolean intersectsWithTransparent(GameObject other) {
        Rectangle rect1 = this.getBounds();
        Rectangle rect2 = other.getBounds();

        if (!rect1.intersects(rect2)) {
            return false;
        }

        Rectangle intersection = rect1.intersection(rect2);

        BufferedImage img1 = (BufferedImage) this.getImage();
        BufferedImage img2 = (BufferedImage) other.getImage();

        if (img1 == null || img2 == null) {
            System.err.println("Warning: Image is null for transparent intersection check.");
            return rect1.intersects(rect2); // 画像がない場合は矩形判定にフォールバック
        }
        
        // 画像が描画されている実際のサイズとの比率
        double scaleX1 = (double) img1.getWidth() / this.width;
        double scaleY1 = (double) img1.getHeight() / this.height;
        double scaleX2 = (double) img2.getWidth() / other.width;
        double scaleY2 = (double) img2.getHeight() / other.height;

        for (int i = intersection.x; i < intersection.x + intersection.width; i++) {
            for (int j = intersection.y; j < intersection.y + intersection.height; j++) {
                int img1X = (int) ((i - this.x) * scaleX1);
                int img1Y = (int) ((j - this.y) * scaleY1);

                int img2X = (int) ((i - other.x) * scaleX2);
                int img2Y = (int) ((j - other.y) * scaleY2);

                if (img1X >= 0 && img1X < img1.getWidth() && img1Y >= 0 && img1Y < img1.getHeight() &&
                    img2X >= 0 && img2X < img2.getWidth() && img2Y >= 0 && img2Y < img2.getHeight()) {

                    int pixel1 = img1.getRGB(img1X, img1Y);
                    int alpha1 = (pixel1 >> 24) & 0xFF;

                    int pixel2 = img2.getRGB(img2X, img2Y);
                    int alpha2 = (pixel2 >> 24) & 0xFF;

                    if (alpha1 > 0 && alpha2 > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}