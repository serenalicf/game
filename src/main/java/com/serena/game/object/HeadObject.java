package com.serena.game.object;

import com.serena.game.Game;
import com.serena.game.GameState;
import com.serena.game.util.GameUtil;
import lombok.Data;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

@Data
public class HeadObject extends GameObject {

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    //control directions
    private String direction = "right";

    public HeadObject(Image img, int x, int y, Game frame) {
        super(img, x, y, frame);

        this.frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                changeDirection(e);
            }
        });
    }

    //w : up,  s:down, a: left, d: right
    public void changeDirection(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT:
                if (!"right".equals(direction)) {
                    direction = "left";
                    img = GameUtil.leftImg;
                }
                break;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT:
                if (!"left".equals(direction)) {
                    direction = "right";
                    img = GameUtil.rightImg;
                }
                break;
            case KeyEvent.VK_W, KeyEvent.VK_UP:
                if (!"down".equals(direction)) {
                    direction = "up";
                    img = GameUtil.upImg;
                }
                break;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN:
                if (!"up".equals(direction)) {
                    direction = "down";
                    img = GameUtil.downImg;
                }
                break;
            default:
                break;

        }
    }

    public void move() {
        //body move
        List<BodyObject> bodyObjectList = this.frame.bodyObjectList;

        for(int i=bodyObjectList.size() - 1; i >= 1; i--){
            //movement of body is the previous coordinate
            bodyObjectList.get(i).x = bodyObjectList.get(i-1).x;
            bodyObjectList.get(i).y = bodyObjectList.get(i-1).y;

            //check head and body crush
            if(this.x == bodyObjectList.get(i).x && this.y == bodyObjectList.get(i).y){
                //game failed
                Game.state = GameState.GAME_OVER;
            }
        }

        bodyObjectList.get(0).x = this.x;
        bodyObjectList.get(0).y = this.y;

        //head move
        switch (direction) {
            case "up":
                y -= height;
                break;
            case "down":
                y += height;
                break;
            case "left":
                x -= width;
                break;
            case "right":
                x += width;
                break;
            default:
                break;
        }
    }

    @Override
    public void paint(Graphics graphics, float scale, int scaleX, int scaleY) {
        super.paint(graphics, scale, scaleX, scaleY);

        FoodObject food = this.frame.foodObject;
        Integer newX = null;
        Integer newY = null;

        if(this.x == food.x && this.y == food.y ){
            this.frame.foodObject = food.getFood();
            //get body last part
            BodyObject lastBodyObject = this.frame.bodyObjectList.get(this.frame.bodyObjectList.size()-1);
            newX = lastBodyObject.x;
            newY = lastBodyObject.y;

            //add score
            this.frame.score ++;
        }

        //check if pass the level
        if( this.frame.score >= 15) {
            Game.state = GameState.LEVEL_CLEARED;
        }

        move();

        if(newX != null && newY != null){
            this.frame.bodyObjectList.add(new BodyObject(GameUtil.bodyImg, newX, newY, this.frame));
        }

        //handle reach edge
        if(x <0){
            x = 570;
        }else if (x >570) {
            x = 0;
        } else if (y < 30){
            y = 570;
        } else if (y >570) {
            y = 30;
        }
    }
}
