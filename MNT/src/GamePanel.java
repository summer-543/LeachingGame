import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GamePanel extends JPanel {

    private List<Vegetable> vegetables;
    private GameObject draggedObject;

    private Patient patientObject;
    private GameObject knifeObject;
    private GameObject waterObject;
    private GameObject nabeObject;
    private GameObject zaruiObject;
    private GameObject sinkObject;

    private Timer contactTimer;
    private GameObject currentTool;
    private Vegetable boiledVegetable;

    private int initialNabeX;
    private int initialNabeY;

    // ボタンパネルの高さの定数を調整（実際のFlowLayoutのコンポーネント高さ+マージンを考慮）
    private static final int BUTTON_PANEL_HEIGHT = 110; // ボタンサイズとフローレイアウトの余白を考慮して調整

    private static final int UNIFORM_OBJECT_HEIGHT = 150;
    private static final int PATIENT_TARGET_WIDTH = 350;
    private static final int VEGETABLE_TARGET_WIDTH = 150;
    private static final int SPACING_FROM_RIGHT = 50;
    private static final int HITBOX_MARGIN = 20;

    public GamePanel() {
        setLayout(new BorderLayout());

        vegetables = new ArrayList<>();
        
        int currentX = 25;

        try {
            BufferedImage knifeImage = ImageIO.read(new File("knife.png"));
            int knifeWidth = (int) ((double) UNIFORM_OBJECT_HEIGHT / knifeImage.getHeight() * knifeImage.getWidth());
            knifeObject = new GameObject(currentX, 500, knifeWidth, UNIFORM_OBJECT_HEIGHT, knifeImage, false);
            currentX += knifeWidth + SPACING_FROM_RIGHT;
            
            BufferedImage waterImage = ImageIO.read(new File("sizuku.png"));
            int waterWidth = (int) ((double) UNIFORM_OBJECT_HEIGHT / waterImage.getHeight() * waterImage.getWidth());
            waterObject = new GameObject(currentX, 500, waterWidth, UNIFORM_OBJECT_HEIGHT, waterImage, false);
            currentX += waterWidth + SPACING_FROM_RIGHT;
            
            BufferedImage nabeImage = ImageIO.read(new File("nabe.png"));
            int nabeWidth = (int) ((double) UNIFORM_OBJECT_HEIGHT / nabeImage.getHeight() * nabeImage.getWidth());
            nabeObject = new GameObject(currentX, 500, nabeWidth, UNIFORM_OBJECT_HEIGHT, nabeImage, false);
            initialNabeX = currentX;
            initialNabeY = 500;
            currentX += nabeWidth + SPACING_FROM_RIGHT;
            
            BufferedImage zaruiImage = ImageIO.read(new File("zarui.png"));
            int zaruiWidth = (int) ((double) UNIFORM_OBJECT_HEIGHT / zaruiImage.getHeight() * zaruiImage.getWidth());
            zaruiObject = new GameObject(currentX, 500, zaruiWidth, UNIFORM_OBJECT_HEIGHT, zaruiImage, false);
            currentX += zaruiWidth + SPACING_FROM_RIGHT;
            
            BufferedImage sinkImage = ImageIO.read(new File("sink.png"));
            int sinkWidth = (int) ((double) UNIFORM_OBJECT_HEIGHT / sinkImage.getHeight() * sinkImage.getWidth());
            sinkObject = new GameObject(currentX, 500, sinkWidth, UNIFORM_OBJECT_HEIGHT, sinkImage, false);

            BufferedImage patientImage = ImageIO.read(new File("jinzou1.png"));
            int patientHeight = (int) ((double) PATIENT_TARGET_WIDTH / patientImage.getWidth() * patientImage.getHeight());
            patientObject = new Patient(1200 - PATIENT_TARGET_WIDTH - SPACING_FROM_RIGHT - 40, 70, PATIENT_TARGET_WIDTH, patientHeight, "jinzou1.png", "jinzou2.png", "jinzou3.png");
            
        } catch (IOException e) {
            System.err.println("Error: One or more images could not be loaded.");
            e.printStackTrace();
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        // ボタンパネルの推奨サイズを設定し、BUTTON_PANEL_HEIGHT に合わせる
        buttonPanel.setPreferredSize(new Dimension(getPreferredSize().width, BUTTON_PANEL_HEIGHT));


        JButton spinachButton = createIconButton("ほうれん草", "hourensou.png");
        spinachButton.addActionListener(e -> createVegetable("ほうれん草", "hourensou.png"));
        buttonPanel.add(spinachButton);

        JButton potatoButton = createIconButton("じゃがいも", "jagaimo.png");
        potatoButton.addActionListener(e -> createVegetable("じゃがいも", "jagaimo.png"));
        buttonPanel.add(potatoButton);

        JButton pumpkinButton = createIconButton("かぼちゃ", "kabotya.png");
        pumpkinButton.addActionListener(e -> createVegetable("かぼちゃ", "kabotya.png"));
        buttonPanel.add(pumpkinButton);
        
        JButton carrotButton = createIconButton("にんじん", "ninjin.png");
        carrotButton.addActionListener(e -> createVegetable("にんじん", "ninjin.png"));
        buttonPanel.add(carrotButton);

        JButton cucumberButton = createIconButton("きゅうり", "kyuri.png");
        cucumberButton.addActionListener(e -> createVegetable("きゅうり", "kyuri.png"));
        buttonPanel.add(cucumberButton);
        
        JButton shiitakeButton = createIconButton("しいたけ", "shiitake.png");
        shiitakeButton.addActionListener(e -> createVegetable("しいたけ", "shiitake.png"));
        buttonPanel.add(shiitakeButton);
        
        JButton resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(80, 40));
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setForeground(Color.RED);
        resetButton.setBackground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetGame());
        buttonPanel.add(resetButton);

        this.add(buttonPanel, BorderLayout.PAGE_END);

        DragMouseListener listener = new DragMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1200, 800));
    }

    private JButton createIconButton(String text, String imagePath) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            Image scaledImage = originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            JButton button = new JButton(text, icon);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setFocusPainted(false);
            return button;
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath);
            return new JButton(text);
        }
    }

    private void createVegetable(String name, String imagePath) {
        resetGame(); // 新しい野菜を作成する前にゲーム状態をリセット
        BufferedImage tempImage = null;
        try {
            tempImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Error: " + imagePath + " could not be loaded.");
            e.printStackTrace();
            return;
        }
        
        int newHeight = (int) ((double) VEGETABLE_TARGET_WIDTH / tempImage.getWidth() * tempImage.getHeight());
        
        // 野菜の初期位置をボタンエリアと被らないように調整
        Vegetable newVeg = new Vegetable(50, 50, VEGETABLE_TARGET_WIDTH, newHeight, name, tempImage, true);
        vegetables.add(newVeg);
        
        repaint();
    }

    private void resetGame() {
        // 各野菜の画像と状態をリセット
        for (Vegetable veg : vegetables) {
            veg.resetState();
        }
        vegetables.clear();
        patientObject.resetState();
        
        // 調理器具の画像を元に戻す
        knifeObject.restoreImage();
        waterObject.restoreImage();
        nabeObject.restoreImage();
        zaruiObject.restoreImage();
        sinkObject.restoreImage();

        nabeObject.setLocation(initialNabeX, initialNabeY);
        nabeObject.setDraggable(false);
        boiledVegetable = null;
        draggedObject = null;
        if (contactTimer != null) {
            contactTimer.cancel();
            contactTimer = null;
        }
        currentTool = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 腎臓エリアに枠線を描画
        g2d.setColor(Color.GRAY); // 枠線の色
        g2d.setStroke(new BasicStroke(3)); // 枠線の太さ
        g2d.drawOval(patientObject.x - 23, patientObject.y - 30, patientObject.width + 50, patientObject.height + 40); // 腎臓オブジェクトの周囲に枠を描画
        g2d.setStroke(new BasicStroke(1)); // デフォルトに戻す
        
        knifeObject.draw(g2d);
        waterObject.draw(g2d);
        zaruiObject.draw(g2d);
        sinkObject.draw(g2d);
        
        patientObject.draw(g2d);

        List<GameObject> dynamicObjects = new ArrayList<>();
        dynamicObjects.add(nabeObject);
        dynamicObjects.addAll(vegetables);

        for (GameObject obj : dynamicObjects) {
            if (obj != draggedObject) {
                obj.draw(g2d);
            }
        }
        
        if (draggedObject != null) {
            draggedObject.draw(g2d);
        }
    }

    private class DragMouseListener extends MouseAdapter {
        private int dragOffsetX, dragOffsetY;

        @Override
        public void mousePressed(MouseEvent e) {
            if (nabeObject.getBounds().contains(e.getPoint()) && nabeObject.isDraggable()) {
                draggedObject = nabeObject;
                dragOffsetX = e.getX() - draggedObject.getBounds().x;
                dragOffsetY = e.getY() - draggedObject.getBounds().y;
            } else {
                for (Vegetable veg : vegetables) {
                    if (veg != null && veg.getBounds().contains(e.getPoint()) && veg.isDraggable()) {
                        draggedObject = veg;
                        dragOffsetX = e.getX() - draggedObject.getBounds().x;
                        dragOffsetY = e.getY() - draggedObject.getBounds().y;
                        break;
                    }
                    // ドラッグされた野菜は他の野菜の上に表示されるように、リストの最後尾に移動
                    vegetables.remove(veg);
                    vegetables.add(veg);
                }
            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (draggedObject != null) {
                int newX = e.getX() - dragOffsetX;
                int newY = e.getY() - dragOffsetY;

                // 画面左端、上端の境界線チェック
                newX = Math.max(0, newX);
                newY = Math.max(0, newY);

                // 画面右端、下端の境界線チェック
                newX = Math.min(getWidth() - draggedObject.width, newX);
                newY = Math.min(getHeight() - draggedObject.height - BUTTON_PANEL_HEIGHT, newY); // ⭐ getHeight()からBUTTON_PANEL_HEIGHTを引く

                draggedObject.setLocation(newX, newY);
                
                Rectangle knifeHitbox = new Rectangle(
                    knifeObject.getBounds().x + HITBOX_MARGIN,
                    knifeObject.getBounds().y + HITBOX_MARGIN,
                    knifeObject.getBounds().width - 2 * HITBOX_MARGIN,
                    knifeObject.getBounds().height - 2 * HITBOX_MARGIN
                );
                
                Rectangle waterHitbox = new Rectangle(
                    waterObject.getBounds().x + HITBOX_MARGIN,
                    waterObject.getBounds().y + HITBOX_MARGIN,
                    waterObject.getBounds().width - 2 * HITBOX_MARGIN,
                    waterObject.getBounds().height - 2 * HITBOX_MARGIN
                );
                
                Rectangle nabeHitbox = new Rectangle(
                    nabeObject.getBounds().x + HITBOX_MARGIN,
                    nabeObject.getBounds().y + HITBOX_MARGIN,
                    nabeObject.getBounds().width - 2 * HITBOX_MARGIN,
                    nabeObject.getBounds().height - 2 * HITBOX_MARGIN
                );
                
                Rectangle zaruiHitbox = new Rectangle(
                    zaruiObject.getBounds().x + HITBOX_MARGIN,
                    zaruiObject.getBounds().y + HITBOX_MARGIN,
                    zaruiObject.getBounds().width - 2 * HITBOX_MARGIN,
                    zaruiObject.getBounds().height - 2 * HITBOX_MARGIN
                );
                
                GameObject newTool = null;

                if (draggedObject instanceof Vegetable) {
                    Vegetable draggedVegetable = (Vegetable) draggedObject;
                    if (draggedVegetable.getBounds().intersects(knifeHitbox) && !draggedVegetable.isCut()) {
                        newTool = knifeObject;
                    } else if (draggedVegetable.getBounds().intersects(waterHitbox) && !draggedVegetable.isLeached() && !draggedVegetable.isBoiled()) {
                        newTool = waterObject;
                    } else if (draggedVegetable.getBounds().intersects(nabeHitbox) && !draggedVegetable.isBoiled()) {
                        newTool = nabeObject;
                    } else if (draggedVegetable.getBounds().intersects(zaruiHitbox) && (draggedVegetable.isBoiled() || draggedVegetable.isLeached()) && !draggedVegetable.isDrained()) {
                        newTool = zaruiObject;
                    }
                } else if (draggedObject == nabeObject) {
                    Rectangle sinkHitbox = new Rectangle(
                        sinkObject.getBounds().x + HITBOX_MARGIN,
                        sinkObject.getBounds().y + HITBOX_MARGIN,
                        sinkObject.getBounds().width - 2 * HITBOX_MARGIN,
                        sinkObject.getBounds().height - 2 * HITBOX_MARGIN
                    );
                    if (draggedObject.getBounds().intersects(sinkHitbox) && draggedObject.isDraggable()) {
                        newTool = sinkObject;
                    }
                }

                if (currentTool != newTool) {
                    if (contactTimer != null) {
                        contactTimer.cancel();
                        contactTimer = null;
                    }
                    currentTool = newTool;

                    if (currentTool != null) {
                        if (currentTool == knifeObject) {
                            contactTimer = new Timer();
                            contactTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    ((Vegetable) draggedObject).setCut(true);
                                    ((Vegetable) draggedObject).setScore(((Vegetable) draggedObject).getScore() + 10);
                                    knifeObject.darkenImage(); // 調理器具を暗くする
                                    javax.swing.SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(null, "野菜を一口大にカットしました！", "アクション成功", JOptionPane.INFORMATION_MESSAGE); 
                                    });
                                    if (contactTimer != null) {
                                        contactTimer.cancel();
                                        contactTimer = null;
                                    }
                                }
                            }, 1000);
                        } else if (currentTool == waterObject) {
                            contactTimer = new Timer();
                            contactTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    ((Vegetable) draggedObject).setLeached(true);
                                    ((Vegetable) draggedObject).setScore(((Vegetable) draggedObject).getScore() + 60);
                                    waterObject.darkenImage(); // 調理器具を暗くする
                                    javax.swing.SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(null, "水にさらしました！", "アクション成功", JOptionPane.INFORMATION_MESSAGE); 
                                    });
                                    if (contactTimer != null) {
                                        contactTimer.cancel();
                                        contactTimer = null;
                                    }
                                }
                            }, 2000);
                        } else if (currentTool == nabeObject) {
                            contactTimer = new Timer();
                            contactTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    ((Vegetable) draggedObject).setBoiled(true);
                                    boiledVegetable = (Vegetable) draggedObject;
                                    nabeObject.setDraggable(true);
                                    nabeObject.darkenImage(); // 調理器具を暗くする
                                    if (boiledVegetable.isLeached()) {
                                        boiledVegetable.setScore(boiledVegetable.getScore() + (70 - 60));
                                    } else {
                                        boiledVegetable.setScore(boiledVegetable.getScore() + 70);
                                    }
                                    javax.swing.SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(null, "茹でこぼしが完了しました！\n次は鍋をシンクへドラッグして茹で汁を捨てましょう。", "アクション成功", JOptionPane.INFORMATION_MESSAGE); 
                                    });
                                    if (contactTimer != null) {
                                        contactTimer.cancel();
                                        contactTimer = null;
                                    }
                                }
                            }, 2000);
                        } else if (currentTool == zaruiObject) {
                            contactTimer = new Timer();
                            contactTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    ((Vegetable) draggedObject).setDrained(true);
                                    ((Vegetable) draggedObject).setScore(((Vegetable) draggedObject).getScore() + 20);
                                    zaruiObject.darkenImage(); // 調理器具を暗くする
                                    javax.swing.SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(null, "水切りが完了しました！", "アクション成功", JOptionPane.INFORMATION_MESSAGE); 
                                    });
                                    if (contactTimer != null) {
                                        contactTimer.cancel();
                                        contactTimer = null;
                                    }
                                }
                            }, 1000);
                        } else if (currentTool == sinkObject) {
                            contactTimer = new Timer();
                            contactTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    nabeObject.setDraggable(false);
                                    sinkObject.darkenImage(); // 調理器具を暗くする
                                    javax.swing.SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(null, "お湯を捨てました！\n茹で汁にはカリウムが溶け出しているため、使わずに捨てましょう。", "アクション成功", JOptionPane.INFORMATION_MESSAGE); 
                                    });
                                    if (contactTimer != null) {
                                        contactTimer.cancel();
                                        contactTimer = null;
                                    }
                                }
                            }, 1000);
                        }
                    }
                }
                
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (draggedObject != null && draggedObject instanceof Vegetable) {
                if (draggedObject.intersectsWithTransparent(patientObject)) {
                    Vegetable veg = (Vegetable) draggedObject;
                    int finalScore = veg.getScore();
                    String message;
                    if (veg.getName().equals("きゅうり") && veg.isBoiled()) {
                        message = String.format("調理完了！\n最終スコア: %d点\nきゅうりは生食が一般的で、茹でることはあまりありません。カリウムを減らすには、一口大に切って水にさらすのが効果的です。", finalScore);
                    } else if (veg.getName().equals("かぼちゃ")) {
                        message = String.format("調理完了！\n最終スコア: %d点\nかぼちゃは茹でこぼしなどをしても十分にカリウムを減らすことが難しいため、食べる量を少量にしなければなりません。", finalScore);
                    } else if (finalScore >= 80) {
                        message = String.format("調理完了！\n最終スコア: %d点\n素晴らしい調理法でカリウムを減らすことができました。", finalScore);
                    } else {
                        message = String.format("調理完了！\n最終スコア: %d点\n正しい調理法が不足しています。もう少しカリウムを減らす工夫をしましょう。", finalScore);
                    }
                    JOptionPane.showMessageDialog(null, message, "結果", JOptionPane.INFORMATION_MESSAGE);
                    
                    patientObject.updateState(finalScore);
                 
                    vegetables.remove((Vegetable) draggedObject);
                    draggedObject = null;
                    repaint();
                }
            }
            draggedObject = null;
            if (contactTimer != null) {
                contactTimer.cancel();
                contactTimer = null;
            }
            currentTool = null;
            repaint();
        }
    }
}
