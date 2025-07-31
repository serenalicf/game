package com.serena.game;


import com.serena.game.GameState;
import com.serena.game.object.BodyObject;
import com.serena.game.object.FoodObject;
import com.serena.game.object.HeadObject;
import com.serena.game.util.GameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Game extends JFrame {

    //score
    public int score = 0;

    public static long startTime;
    public static long totalPausedTime = 0;
    public static long pauseStartTime;
    public static boolean timerStarted = false;
    private String formattedTime = "00:00";

    public static GameState state = GameState.NOT_STARTED;
    public int duration = 0;

    //define double buffering
    Image offScreenImage = null;

    // Constants
    public static final int INITIAL_WINDOW_WIDTH = 800;
    public static final int INITIAL_WINDOW_HEIGHT = 600;
    public static final int GRID_SIZE = 30;

    //window width height
    int windowWidth = INITIAL_WINDOW_WIDTH;
    int windowHeight = INITIAL_WINDOW_HEIGHT;

    float scaleX;
    float scaleY;


    HeadObject headObject = new HeadObject(GameUtil.rightImg, 60, 570, this);

    public List<BodyObject> bodyObjectList = new ArrayList<>();

    public FoodObject foodObject = new FoodObject().getFood();

    public void launch() {
        this.setVisible(true);
        this.setSize(windowWidth, windowHeight);
        this.setLocationRelativeTo(null);
        this.setTitle("Snake Game");

        //initalize the body
        bodyObjectList.add(new BodyObject(GameUtil.bodyImg, 30, 570, this));
        bodyObjectList.add(new BodyObject(GameUtil.bodyImg, 0, 570, this));

        //key event
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    switch (state) {
                        case NOT_STARTED:
                            if (!timerStarted) {
                                startTime = System.currentTimeMillis();
                                totalPausedTime = 0;
                                timerStarted = true;
                            }
                            state = GameState.PLAYING;
                            break;
                        case PLAYING:
                            state = GameState.PAUSED;
                            pauseStartTime = System.currentTimeMillis();
                            repaint();
                            break;
                        case PAUSED:
                            totalPausedTime += System.currentTimeMillis() - pauseStartTime;
                            state = GameState.PLAYING;
                            break;
                        case GAME_OVER: //fail then reset
                            state = GameState.RESETTING;
                            break;
                        case LEVEL_CLEARED: //pass, next level
                            if (GameUtil.level < 3) {
                                state = GameState.NEXT_LEVEL;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        // Inside the launch() method, after addKeyListener(...)
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                windowWidth = getWidth();
                windowHeight = getHeight();
                // By setting the off-screen image to null, we force it to be recreated
                // with the new dimensions in the paint() method.
                offScreenImage = null;
            }
        });

        while (true) {
            if (state == GameState.PLAYING) {
                long elapsedTime = System.currentTimeMillis() - startTime - totalPausedTime;
                long seconds = elapsedTime / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                formattedTime = String.format("%02d:%02d", minutes, seconds);
                repaint();
            }
            if (state == GameState.RESETTING) {
                //fail and reset
                state = GameState.NOT_STARTED;
                timerStarted = false;
                resetGame();
            }
            if (state == GameState.NEXT_LEVEL && GameUtil.level != 3) {
                state = GameState.PLAYING;
                GameUtil.level++;
                resetGame();
            }
            try {
                //1s = 100 ms
                if (GameUtil.level == 2) {
                    Thread.sleep(150);
                } else if (GameUtil.level == 3) {
                    Thread.sleep(100);
                } else {
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics graphics) {
        //initialize cached image
        if (offScreenImage == null) {
            offScreenImage = this.createImage(getWidth(), getHeight());
        }

        //get related graphics
        Graphics gImage = offScreenImage.getGraphics();

        scaleX = getWidth() / 800.0f;
        scaleY = getHeight() / 600.0f;

        //grey background
        gImage.setColor(Color.black);
        gImage.fillRect(0, 0, getWidth(), getHeight());

//        //grid color
        gImage.setColor(Color.white);

        gImage.drawLine((int) (600 * scaleX), 0, (int) (600 * scaleX), (int) (600 * scaleY));


        //draw snake body to avoid repeating body
        for (int i = bodyObjectList.size() - 1; i >= 0; i--) {
            // bodyObjectList.get(i).paint(gImage);
            bodyObjectList.get(i).paint(gImage, scaleX, scaleY);
        }

        //draw snake head
        // headObject.paint(gImage);
        headObject.paint(gImage, scaleX, scaleY);

        //draw food
        // foodObject.paint(gImage);
        foodObject.paint(gImage, scaleX, scaleY);

        //draw level
        // GameUtil.drawWord(gImage, "Level: " + GameUtil.level, Color.ORANGE, 30, 650, 260);
        GameUtil.drawWord(gImage, "Level " + GameUtil.level, Color.ORANGE, (int) (40 * scaleY), (int) (650 * scaleX), (int) (260 * scaleY));

        //draw score
        // GameUtil.drawWord(gImage, "Score: " + score, Color.BLUE, 30, 650, 300);
        GameUtil.drawWord(gImage,  "Score " + score, Color.BLUE, (int) (40 * scaleY), (int) (650 * scaleX), (int) (300 * scaleY));

        //draw timer
        // GameUtil.drawWord(gImage, "Time: " + formattedTime, Color.WHITE, 20, 650, 340);
        GameUtil.drawWord(gImage, "Time " + formattedTime, Color.WHITE, (int) (20 * scaleY), (int) (650 * scaleX), (int) (340 * scaleY));

        gImage.setColor(Color.gray);
        //draw hint
        prompt(gImage);

        //put cached image to pop up window
        graphics.drawImage(offScreenImage, 0, 0, null);
    }


    private void drawPromptBox(Graphics graphics) {
        graphics.fillRect((int) (120 * scaleX), (int) (240 * scaleY), (int) (400 * scaleX), (int) (70 * scaleY));
    }

    void prompt(Graphics graphics) {
        switch (state) {
            case NOT_STARTED:
                drawPromptBox(graphics);
                GameUtil.drawWord(graphics, "Press space to start", Color.YELLOW, (int) (35 * scaleY), (int) (150 * scaleX), (int) (290 * scaleY));
                break;
            case PAUSED:
                drawPromptBox(graphics);
                GameUtil.drawWord(graphics, "Press space to continue", Color.YELLOW, (int) (35 * scaleY), (int) (150 * scaleX), (int) (290 * scaleY));
                break;
            case GAME_OVER:
                drawPromptBox(graphics);
                GameUtil.drawWord(graphics, "Game Over!", Color.RED, (int) (35 * scaleY), (int) (150 * scaleX), (int) (290 * scaleY));
                break;
            case LEVEL_CLEARED:
                drawPromptBox(graphics);
                if (GameUtil.level == 3) {
                    GameUtil.drawWord(graphics, "Win! Total time: " + formattedTime, Color.GREEN, (int) (35 * scaleY), (int) (150 * scaleX), (int) (290 * scaleY));
                } else {
                    GameUtil.drawWord(graphics, "Level Clear!", Color.GREEN, (int) (35 * scaleY), (int) (150 * scaleX), (int) (290 * scaleY));
                }
                break;
        }

    }

    //reset game
    void resetGame() {
        // Reset game state variables
        score = 0;
        state = GameState.NOT_STARTED;
        timerStarted = false;
        totalPausedTime = 0;
        formattedTime = "00:00";

        // Reset snake position
        headObject.setX(60);
        headObject.setY(570);
        headObject.setDirection("right");

        // Reset snake body
        bodyObjectList.clear();
        bodyObjectList.add(new BodyObject(GameUtil.bodyImg, 30, 570, this));
        bodyObjectList.add(new BodyObject(GameUtil.bodyImg, 0, 570, this));

        // Reset food
        foodObject = new FoodObject().getFood();

        repaint();
    }


    public static void main(String[] args) {
        Game game = new Game();
        game.launch();
    }
}
