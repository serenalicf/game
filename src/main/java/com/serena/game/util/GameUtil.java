package com.serena.game.util;

import java.awt.*;

public class GameUtil {
    public static Image upImg = Toolkit.getDefaultToolkit().getImage("img/up.png");
    public static Image downImg = Toolkit.getDefaultToolkit().getImage("img/down.png");
    public static Image leftImg = Toolkit.getDefaultToolkit().getImage("img/left.png");
    public static Image rightImg = Toolkit.getDefaultToolkit().getImage("img/right.png");

    public static Image bodyImg = Toolkit.getDefaultToolkit().getImage("img/body.png");
    public static Image foodImg = Toolkit.getDefaultToolkit().getImage("img/food.png");

    public static int level = 1;


    public static void drawWord(Graphics graphics, String str, Color color, int size, int x, int y) {
        graphics.setColor(color);
        graphics.setFont(new Font("Times New Roman", Font.BOLD, size));
        graphics.drawString(str, x, y);
    }
}
