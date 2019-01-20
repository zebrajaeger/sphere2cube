package de.zebrajaeger.sphere2cube.blackimages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class ImagesAndSizes {

    @JsonIgnore
    private Function<PanoImage, String> nameGenerator;
    private AtomicInteger count = new AtomicInteger(0);
    private List<Reference> references = new LinkedList<>();
    private List<Image> images = new LinkedList<>();

    public static ImagesAndSizes of(Function<PanoImage, String> nameGenerator){
        return new ImagesAndSizes(nameGenerator);
    }

    private ImagesAndSizes(Function<PanoImage, String> nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    public ImagesAndSizes panoImages(Collection<PanoImage> panoImages) {
        for (PanoImage pi : panoImages) {
            panoImage(pi);
        }
        return this;
    }

    public ImagesAndSizes panoImage(PanoImage panoImage) {
        Reference reference = getReference(panoImage);
        Image image = new Image(panoImage.getPath(), reference.id);
        images.add(image);
        return this;
    }

    private Reference getReference(PanoImage panoImage) {
        return references.stream().
                filter(toTest -> toTest.getW() == panoImage.getWidth() && toTest.getH() == panoImage.getHeight())
                .findFirst()
                .orElseGet(() -> {
                    Reference reference = new Reference(count.getAndIncrement(), panoImage.getWidth(), panoImage.getHeight(), nameGenerator.apply(panoImage));
                    references.add(reference);
                    return reference;
                });
    }


    public Function<PanoImage, String> getNameGenerator() {
        return nameGenerator;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public List<Image> getImages() {
        return images;
    }

    public static class Reference {
        private int id;
        private int w;
        private int h;
        private String path;

        public Reference(int id, int w, int h, String path) {
            this.id = id;
            this.w = w;
            this.h = h;
            this.path = path;
        }

        public int getId() {
            return id;
        }

        public int getW() {
            return w;
        }

        public int getH() {
            return h;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public static class Image {
        private String path;
        private int ref;

        public Image(String path, int reference) {
            this.path = path;
            this.ref = reference;
        }

        public String getPath() {
            return path;
        }

        public int getRef() {
            return ref;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
