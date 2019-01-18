package de.zebrajaeger.sphere2cube.plugin;

import de.zebrajaeger.sphere2cube.description.Description;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@SuppressWarnings("unused")
@Mojo(name = "checkDescription", requiresProject = false, defaultPhase = LifecyclePhase.VALIDATE)
public class ValidateMojo extends DescriptionMojo {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateMojo.class);

    @Override
    protected void handleDescription(File sourceImage, Description xmlDescription, Description jsonDescription, Description yamlDescription) {
        // we try to read the (possible) description files. If something goes wrong the load throws exception(s) and the build fails
        if (xmlDescription == null && jsonDescription == null && yamlDescription == null) {
            LOG.debug("No description found for image: '{}'", sourceImage.getAbsolutePath());
        } else {
            LOG.debug(
                    "Description for image: '{}' is '{}",
                    sourceImage.getAbsolutePath(),
                    new Description().merge(xmlDescription).merge(jsonDescription).merge(yamlDescription));
        }
    }
}
