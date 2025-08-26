import java.awt.image.BufferedImage;

public class Vegetable extends GameObject {
    private double initialPotassium; // 現段階では使用していないが、後に拡張・改良する際に使用する可能性があるので残しておく
    private boolean isCut;
    private boolean isLeached;
    private boolean isBoiled;
    private boolean isDrained;
    private int score;

    public Vegetable(int x, int y, int width, int height, String name, BufferedImage image, boolean draggable) {
        super(x, y, width, height, image, draggable);
        this.name = name;
        this.isCut = false;
        this.isLeached = false;
        this.isBoiled = false;
        this.isDrained = false;
        this.score = 0;
        setInitialPotassium(); 
    }

    public double getInitialPotassium() {
    	return this.initialPotassium;
    }
    
    private void setInitialPotassium() {
        switch (this.name) {
            case "ほうれん草":
                this.initialPotassium = 690;
                break;
            case "じゃがいも":
                this.initialPotassium = 410;
                break;
            case "かぼちゃ":
                this.initialPotassium = 450;
                break;
            case "にんじん": 
                this.initialPotassium = 300;
                break;
            case "きゅうり": 
                this.initialPotassium = 200;
                break;
            case "しいたけ": 
                this.initialPotassium = 260;
                break;
            default:
                this.initialPotassium = 0;
                break;
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean isCut) {
        this.isCut = isCut;
        // this.darkenImage(); 
    }

    public boolean isLeached() {
        return isLeached;
    }

    public void setLeached(boolean isLeached) {
        this.isLeached = isLeached;
        // this.darkenImage(); 
    }

    public boolean isBoiled() {
        return isBoiled;
    }

    public void setBoiled(boolean isBoiled) {
        this.isBoiled = isBoiled;
        // this.darkenImage(); // 
    }

    public boolean isDrained() {
        return isDrained;
    }

    public void setDrained(boolean isDrained) {
        this.isDrained = isDrained;
        // this.darkenImage(); // ⭐
    }

    public void resetState() {
        this.isCut = false;
        this.isLeached = false;
        this.isBoiled = false;
        this.isDrained = false;
        this.score = 0;
        this.restoreImage(); 
    }
}