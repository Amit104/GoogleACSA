package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;


public class PuzzleBoard {


    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };
    private ArrayList<PuzzleTile> tiles;
    int steps;

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

    PuzzleBoard previousBoard;
    MinPQ<PuzzleBoard> minPriorityQueue;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        steps = 0;
        tiles = new ArrayList<PuzzleTile>();
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            Bitmap bit = Bitmap.createBitmap(bitmap
                    , (i % NUM_TILES) * (bitmap.getWidth() / NUM_TILES)
                    , (i / NUM_TILES) * (bitmap.getHeight() / NUM_TILES)
                    , bitmap.getWidth() / NUM_TILES
                    , bitmap.getHeight() / NUM_TILES);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bit, parentWidth / NUM_TILES, parentWidth / NUM_TILES, false);
            PuzzleTile tile = new PuzzleTile(scaledBitmap, i);
            if (i == NUM_TILES * NUM_TILES - 1) {
                tile = null;
            }
            tiles.add(tile);
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        this.steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }
        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        PuzzleTile tile = null;
        int pos = 0;
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            tile = tiles.get(i);
            if (tile == null) {
                pos = i;
                break;
            }
        }
        ArrayList<PuzzleBoard> boardTemp = new ArrayList<PuzzleBoard>();
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = (pos % NUM_TILES) + delta[0];
            int nullY = (pos / NUM_TILES) + delta[1];

            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) != null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(pos % NUM_TILES, pos / NUM_TILES));
                boardTemp.add(new PuzzleBoard(this));
                swapTiles(XYtoIndex(pos % NUM_TILES, pos / NUM_TILES), XYtoIndex(nullX, nullY));
            }
        }
        return boardTemp;
    }

    public int priority() {
        PuzzleTile tile = null;
        int hammingDist = 0;
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            tile = tiles.get(i);
            if (tile == null) {
                continue;
            }
            int pos = tile.getNumber();
            int xTemp = pos % NUM_TILES;
            int yTemp = pos / NUM_TILES;
            xTemp = Math.abs(xTemp - (i % NUM_TILES));
            yTemp = Math.abs(yTemp - (i / NUM_TILES));
            hammingDist += (xTemp + yTemp);
        }
        return hammingDist + this.steps;
    }


}
