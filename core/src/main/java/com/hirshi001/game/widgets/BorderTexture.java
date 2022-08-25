package com.hirshi001.game.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BorderTexture {

    public static Pixmap createBorderTexture(TextureRegion region, int width, int height, int borderWidth){
        Pixmap pixmap = createRepeatableRectangle(region, width, height);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(0F, 0F, 0F, 0F);
        int b2 = borderWidth * 2;
        if(b2 > width || b2>height) return pixmap;
        pixmap.fillRectangle(borderWidth, borderWidth, width-b2, height-b2);
        return pixmap;
    }

    public static Pixmap createRepeatableRectangle(TextureRegion region, int width, int height){
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Pixmap source = extractPixmapFromTextureRegion(region);

        int rows = height/source.getHeight();
        int cols = width/source.getWidth();

        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                pixmap.drawPixmap(source, j*source.getWidth(), i*source.getHeight());
            }
            pixmap.drawPixmap(source, cols*source.getWidth(), i*source.getHeight(), 0, 0, width-cols*source.getWidth(), source.getHeight());
        }
        for(int i=0; i<cols; i++){
            pixmap.drawPixmap(source, i*source.getWidth(), rows*source.getHeight(), 0, 0, source.getWidth(), height-rows*source.getHeight());
        }
        pixmap.drawPixmap(source, cols*source.getWidth(), rows*source.getHeight(), 0, 0, width-cols*source.getWidth(), height-rows*source.getHeight());
        source.dispose();
        return pixmap;
    }

    public static Pixmap extractPixmapFromTextureRegion(TextureRegion textureRegion) {
        TextureData textureData = textureRegion.getTexture().getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }
        Pixmap pixmap = new Pixmap(
                textureRegion.getRegionWidth(),
                textureRegion.getRegionHeight(),
                textureData.getFormat()
        );
        pixmap.drawPixmap(
                textureData.consumePixmap(), // The other Pixmap
                0, // The target x-coordinate (top left corner)
                0, // The target y-coordinate (top left corner)
                textureRegion.getRegionX(), // The source x-coordinate (top left corner)
                textureRegion.getRegionY(), // The source y-coordinate (top left corner)
                textureRegion.getRegionWidth(), // The width of the area from the other Pixmap in pixels
                textureRegion.getRegionHeight() // The height of the area from the other Pixmap in pixels
        );
        return pixmap;
    }

}
