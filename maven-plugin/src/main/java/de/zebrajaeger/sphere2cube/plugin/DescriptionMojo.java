package de.zebrajaeger.sphere2cube.plugin;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.description.Description;
import de.zebrajaeger.sphere2cube.description.DescriptionLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public abstract class DescriptionMojo extends BaseMojo {

    private DescriptionLoader loader = new DescriptionLoader();

    @Override
    protected void handleSourceImage(File sourceImage) throws IOException, ImageProcessingException {
        String name = FilenameUtils.removeExtension(sourceImage.getName());

        // we try to read the (possible) description files. If something goes wrong the load throws exception(s) and the build fails
        handleDescription(
                sourceImage,
                loader.load(new File(sourceImage.getParent(), name + ".xml")),
                loader.load(new File(sourceImage.getParent(), name + ".json")),
                loader.load(new File(sourceImage.getParent(), name + ".yaml"))
        );
    }

    protected void handleDescription(
            File sourceImage,
            Optional<Description> xmlDescription,
            Optional<Description> jsonDescription,
            Optional<Description> yamlDescription) throws IOException {
        handleDescription(
                sourceImage,
                xmlDescription.isPresent() ? xmlDescription.get() : null,
                jsonDescription.isPresent() ? jsonDescription.get() : null,
                yamlDescription.isPresent() ? yamlDescription.get() : null
        );
    }

    protected abstract void handleDescription(
            File sourceImage,
            Description xmlDescription,
            Description jsonDescription,
            Description yamlDescription) throws IOException;

    protected DescriptionLoader getLoader() {
        return loader;
    }
}
