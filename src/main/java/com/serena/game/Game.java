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

    //define double buffering
    Image offScreenImage = null;

    // Constants
    public static final int GAME_WIDTH = 600;
    public static final int GAME_HEIGHT = 600;
    public static final int INFO_WIDTH = 200;
    public static final int INITIAL_WINDOW_WIDTH = GAME_WIDTH + INFO_WIDTH;
    public static final int INITIAL_WINDOW_HEIGHT = GAME_HEIGHT;
    public static final int GRID_SIZE = 30;

    //window width height
    int windowWidth = INITIAL_WINDOW_WIDTH;
    int windowHeight = INITIAL_WINDOW_HEIGHT;

    float scale;
    int offsetX;
    int offsetY;


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

        int windowWidth = getWidth();
        int windowHeight = getHeight();

        // Calculate scale factor to maintain aspect ratio
        scale = Math.min((float) (windowWidth - INFO_WIDTH) / GAME_WIDTH, (float) windowHeight / GAME_HEIGHT);

        // Calculate offsets to center the game area
        offsetX = (int) ((windowWidth - INFO_WIDTH - (GAME_WIDTH * scale)) / 2);
        offsetY = (int) ((windowHeight - (GAME_HEIGHT * scale)) / 2);

        // Black background
        gImage.setColor(Color.BLACK);
        gImage.fillRect(0, 0, windowWidth, windowHeight);

        // Game area background
        gImage.setColor(Color.DARK_GRAY);
        gImage.fillRect(offsetX, offsetY, (int) (GAME_WIDTH * scale), (int) (GAME_HEIGHT * scale));

        // Info area background
        gImage.setColor(Color.BLACK);
        gImage.fillRect(offsetX + (int) (GAME_WIDTH * scale), 0, INFO_WIDTH, windowHeight);

        // Info area separator
        gImage.setColor(Color.WHITE);
        gImage.drawLine(offsetX + (int) (GAME_WIDTH * scale), 0, offsetX + (int) (GAME_WIDTH * scale), windowHeight);


        // Draw game objects
        for (int i = bodyObjectList.size() - 1; i >= 0; i--) {
            bodyObjectList.get(i).paint(gImage, scale, offsetX, offsetY);
        }
        headObject.paint(gImage, scale, offsetX, offsetY);
        foodObject.paint(gImage, scale, offsetX, offsetY);

        // Draw score, level, and timer in the info area
        int infoX = offsetX + (int) (GAME_WIDTH * scale) + 20;
        GameUtil.drawWord(gImage, "Level: " + GameUtil.level, Color.ORANGE, 30, infoX, 100);
        GameUtil.drawWord(gImage, "Score: " + score, Color.BLUE, 30, infoX, 150);
        GameUtil.drawWord(gImage, "Time: " + formattedTime, Color.WHITE, 30, infoX, 200);

        gImage.setColor(Color.BLACK);
        //draw hint
        prompt(gImage);

        //put cached image to pop up window
        graphics.drawImage(offScreenImage, 0, 0, null);
    }


    private void drawPromptBox(Graphics graphics) {
        graphics.fillRect(120 * offsetX, 240 * offsetY, 350 * offsetX, (70 * offsetY));
    }

    private void drawPromptMessage(Graphics graphics, String prompt, Color color) {
        GameUtil.drawWord(graphics, prompt, color,  35,  200 , 300 );

    }

    void prompt(Graphics graphics) {
        switch (state) {
            case NOT_STARTED:
                drawPromptBox(graphics);
                drawPromptMessage(graphics, "Press space to start", Color.YELLOW);
                break;
            case PAUSED:
                drawPromptBox(graphics);
                drawPromptMessage(graphics, "Press space to continue", Color.YELLOW);
                break;
            case GAME_OVER:
                drawPromptBox(graphics);
                drawPromptMessage(graphics, "Game Over!", Color.RED);
                break;
            case LEVEL_CLEARED:
                drawPromptBox(graphics);
                if (GameUtil.level == 3) {
                    drawPromptMessage(graphics, "Win! Total time: " + formattedTime, Color.GREEN);
                } else {
                    drawPromptMessage(graphics, "Level Clear!",Color.ORANGE);
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
