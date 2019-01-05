package de.zebrajaeger.sphere2cube.converter;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class TileRenderInfo {
    private Face face;

    private int sourceEdge;
    private int targetEdge;
    private int tileEdgeXmax;
    private int tileEdgeYmax;
    private int tileEdgeX;
    private int tileEdgeY;

    private int tileSizeX;
    private int tileSizeY;

    private int tileIndexX;
    private int tileIndexY;

    private int x1;
    private int x2;
    private int y1;
    private int y2;

    private boolean mirrorX = false;
    private boolean mirrorY;
    private boolean mirrory = false;

    private File targetFile;

    public static TileRenderInfo of() {
        return new TileRenderInfo();
    }

    public TileRenderInfo tilePosition(Face face, int tileIndexX, int tileIndexY) {
        this.face = face;
        this.tileIndexX = tileIndexX;
        this.tileIndexY = tileIndexY;
        return this;
    }

    public TileRenderInfo tileSection(int x1, int x2, int y1, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.tileEdgeX = Math.abs(x2-x1);
        this.tileEdgeY = Math.abs(y2-y1);
        return this;
    }

    public TileRenderInfo mirror(boolean mirrorX, boolean mirrorY) {
        this.mirrorX = mirrorX;
        this.mirrorY = mirrorY;
        return this;
    }

    public TileRenderInfo tilesInFace(int tileSizeX, int tileSizeY) {
        this.tileSizeX = tileSizeX;
        this.tileSizeY = tileSizeY;
        return this;
    }

    public TileRenderInfo edgeSizes(int sourceEdge, int targetEdge, int tileEdgeXmax, int tileEdgeYmax) {
        this.sourceEdge = sourceEdge;
        this.targetEdge = targetEdge;
        this.tileEdgeXmax = tileEdgeXmax;
        this.tileEdgeYmax = tileEdgeYmax;
        return this;
    }

    public TileRenderInfo targetFile(File targetFile) {
        this.targetFile = targetFile;
        return this;
    }

    public int getTileCountX() {
        return tileIndexX + 1;
    }

    public int getTileCountY() {
        return tileIndexY + 1;
    }

    public boolean isMirrorX() {
        return x1 > x2;
    }

    public boolean isMirrorY() {
        return y1 > y2;
    }

    public boolean isTopTile() {
        return tileIndexY == 0;
    }

    public boolean isBottomTile() {
        return tileIndexY == tileSizeY - 1;
    }

    public boolean isLeftTile() {
        return tileIndexX == 0;
    }

    public boolean isRightTile() {
        return tileIndexX == tileSizeX - 1;
    }

    public int getTileEdgeX() {
        return tileEdgeX;
    }

    public int getTileEdgeY() {
        return tileEdgeY;
    }

    public Face getFace() {
        return face;
    }

    public double getSourcEdge() {
        return sourceEdge;
    }

    public double getTargetEdge() {
        return targetEdge;
    }

    public int getTileSizeX() {
        return tileSizeX;
    }

    public int getTileSizeY() {
        return tileSizeY;
    }

    public int getTileIndexX() {
        return tileIndexX;
    }

    public int getTileIndexY() {
        return tileIndexY;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public int getSourceEdge() {
        return sourceEdge;
    }

    public int getTileEdgeXmax() {
        return tileEdgeXmax;
    }

    public int getTileEdgeYmax() {
        return tileEdgeYmax;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
