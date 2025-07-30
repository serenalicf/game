package com.serena.game.object;

import com.serena.game.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameObject {
    //image
    Image img;

    //coordinate
    int x;
    int y;

    //width height
    int width = 30;
    int height = 30;

    Game frame;


    public GameObject(Image img, int x, int y, Game frame) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.frame = frame;
    }

    public void paint(Graphics graphics) {
        graphics.drawImage(img,x,y,null);
    }


}
