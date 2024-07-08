package com.chess.gui;

import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.chess.engine.pieces.Piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCache {
    private static final String PIECE_ICON_PATH = "art/pieces/";
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    public static BufferedImage getPieceImage(Piece piece) {
        String key = piece.getPieceColor().toString().charAt(0) + piece.getPieceType().toString();
        if (!imageCache.containsKey(key)) {
            try {
                BufferedImage image = ImageIO.read(new File(PIECE_ICON_PATH + key + ".png"));
                imageCache.put(key, image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageCache.get(key);
    }
}