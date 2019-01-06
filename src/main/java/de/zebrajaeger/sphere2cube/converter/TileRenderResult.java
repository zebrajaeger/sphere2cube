package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.img.ITargetImage;

public class TileRenderResult {
    private TileRenderInfo tileRenderInfo;
    private ITargetImage targetImage;

    public TileRenderResult(TileRenderInfo tileRenderInfo, ITargetImage targetImage) {
        this.tileRenderInfo = tileRenderInfo;
        this.targetImage = targetImage;
    }

    public TileRenderInfo getTileRenderInfo() {
        return tileRenderInfo;
    }

    public ITargetImage getTargetImage() {
        return targetImage;
    }
}
