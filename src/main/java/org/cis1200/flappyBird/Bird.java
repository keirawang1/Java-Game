package org.cis1200.flappyBird;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bird extends GameObj {
    private static final String IMG_FILE = "files/pixel.png";
    private static final int SIZE = 40;
    private static final int INIT_POS_X = 0;
    private static final int INIT_POS_Y = 200;
    private static final int INIT_VEL_X = 0;
    private static final int INIT_VEL_Y = 0;

    private static BufferedImage img;

    public Bird(int courtWidth, int courtHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, courtWidth, courtHeight);

        try {
            if (img == null) {
                img = ImageIO.read(new File(IMG_FILE));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }

    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(img, this.getPx(), this.getPy(), this.getWidth(), this.getHeight(), null);
    }

    @Override
    public String toString() {
        return this.getPx() + ", " + this.getPy();
    }

}
