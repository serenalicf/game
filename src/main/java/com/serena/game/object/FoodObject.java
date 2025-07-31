package com.serena.game.object;

import com.serena.game.Game;
import com.serena.game.util.GameUtil;

import java.awt.*;
import java.util.Random;

public class FoodObject extends GameObject {

    Random random = new Random();

    public FoodObject(Image img, int x, int y, Game frame) {
        super(img, x, y, frame);
    }

    public FoodObject() {
        super();
    }

    //get food
    public FoodObject getFood(){
        // x = 0 - 19 * 30 = 570, y = 30 - 570
        return new FoodObject(GameUtil.foodImg, random.nextInt(20) * 30, (random.nextInt(19)+1)*30, this.frame);
    }

    @Override
    public void paint(Graphics graphics, float scale, int offsetX, int offsetY) {
        super.paint(graphics, scale, offsetX, offsetY);
    }
}
