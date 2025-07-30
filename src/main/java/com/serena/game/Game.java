package com.serena.game;


import com.serena.game.object.BodyObject;
import com.serena.game.object.FoodObject;
import com.serena.game.object.HeadObject;
import com.serena.game.util.GameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Game extends JFrame {

    //score
    public int score = 0;

    //game state: 0 not start, 1 playing, 2 stop, 3 fail, 4 pass, 5 reset after fail, 6 next level
    public static int state = 0;

    //define cached images
    Image offScreenImage = null;

    //window width height
    int windowWidth = 800;
    int windowHeight = 600;

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
                        case 0:  //not start
                            state = 1;
                            break;
                        case 1:
                            //playing
                            state = 2;
                            repaint();
                            break;
                        case 2:
                            //stop
                            state = 1;
                            break;
                        case 3: //fail then reset
                            state = 5;
                            break;
                        case 4: //pass, next level
                            state = 6;
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        while (true) {
            if (state == 1) {
                repaint();
            }
            if (state == 5) {
                //fail and reset
                state = 0;
                resetGame();
            }
            if(state == 6 && GameUtil.level != 3) {
                state = 1;
                GameUtil.level++;
                resetGame();
            }
            try {
                //1s = 100 ms
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics graphics) {
        //initialize cached image
        if (offScreenImage == null) {
            offScreenImage = this.createImage(windowWidth, windowHeight);
        }

        //get related graphics
        Graphics gImage = offScreenImage.getGraphics();

        //grey background
        gImage.setColor(Color.gray);
        gImage.fillRect(0, 0, windowWidth, windowHeight);

        //grid color
        gImage.setColor(Color.black);
        //grid : 20 x 20 rows
        for (int i = 0; i <= 20; i++) {
            gImage.drawLine(0, i * 30, 600, i * 30);
            gImage.drawLine(i * 30, 0, i * 30, 600);
        }
        //draw snake body to avoid repeating body
        for (int i = bodyObjectList.size() - 1; i >= 0; i--) {
            bodyObjectList.get(i).paint(gImage);
        }

        //draw snake head
        headObject.paint(gImage);

        //draw food
        foodObject.paint(gImage);

        //draw level
        GameUtil.drawWord(gImage, "Level " + GameUtil.level, Color.ORANGE, 40, 650, 260);

        //draw score
        GameUtil.drawWord(gImage, score + " score", Color.BLUE, 50, 650, 300);

        gImage.setColor(Color.gray);
        //draw hint
        prompt(gImage);

        //put cached image to pop up window
        graphics.drawImage(offScreenImage, 0, 0, null);
    }


    void prompt(Graphics graphics) {
        //not start
        if (state == 0) {
            graphics.fillRect(120, 240, 400, 70);
            GameUtil.drawWord(graphics, "Press space to start", Color.YELLOW, 35, 150, 290);
        }
        //stop
        if (state == 2) {
            graphics.fillRect(120, 240, 400, 70);
            GameUtil.drawWord(graphics, "Press space to continue", Color.YELLOW, 35, 150, 290);
        }
        //fail
        if (state == 3) {
            graphics.fillRect(120, 240, 400, 70);
            GameUtil.drawWord(graphics, "Game Failed!", Color.RED, 35, 150, 290);
        }
        //pass
        if (state == 4) {
            graphics.fillRect(120, 240, 400, 70);
            if(GameUtil.level == 3) {
                GameUtil.drawWord(graphics, "Win!", Color.GREEN, 35, 150, 290);
            }else {
                GameUtil.drawWord(graphics, "Level Clear!", Color.GREEN, 35, 150, 290);
            }
        }

    }

    //reset game
    void resetGame() {
        //close current window
        this.dispose();

        //create new window
        String[] args = {};
        main(args);

    }


    public static void main(String[] args) {
        Game game = new Game();
        game.launch();
    }
}
