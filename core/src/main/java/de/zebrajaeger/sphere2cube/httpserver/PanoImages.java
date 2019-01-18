package de.zebrajaeger.sphere2cube.httpserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class PanoImages {
    private List<PanoImage> panoImages = new LinkedList<>();

    public synchronized boolean add(PanoImage panoImage) {
        return panoImages.add(panoImage);
    }

    public List<PanoImage> getPanoImages() {
        return panoImages;
    }

    public void setPanoImages(List<PanoImage> panoImages) {
        this.panoImages = panoImages;
    }

    public void save(File file) throws IOException {
        new ObjectMapper().writeValue(file, this);
    }
}
