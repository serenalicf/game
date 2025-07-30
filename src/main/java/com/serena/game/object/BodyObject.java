package com.serena.game.object;

import com.serena.game.Game;
import lombok.Data;

import java.awt.*;

@Data
public class BodyObject extends GameObject {

    public BodyObject(Image img, int x, int y, Game frame) {
        super(img, x, y, frame);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
    }
}
