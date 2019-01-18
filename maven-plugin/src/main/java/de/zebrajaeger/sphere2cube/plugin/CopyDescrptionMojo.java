package de.zebrajaeger.sphere2cube.plugin;

import de.zebrajaeger.sphere2cube.description.Description;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */

@SuppressWarnings("unused")
@Mojo(name = "copyDescription", requiresProject = false, defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class CopyDescrptionMojo extends DescriptionMojo {

    @Parameter(property = "descriptionFile", defaultValue = "${project.build.directory}/{{imageName}}/description.json")
    protected String descriptionFile;

    @Override
    protected void handleDescription(File sourceImage, Description xmlDescription, Description jsonDescription, Description yamlDescription) throws IOException {
        if (xmlDescription != null || jsonDescription != null || yamlDescription != null) {
            String sourceImageName = FilenameUtils.removeExtension(sourceImage.getName());
            File descriptionFile = convertAndCreateDirectories(this.descriptionFile, sourceImageName, true);

            Description description = new Description().merge(xmlDescription).merge(jsonDescription).merge(yamlDescription);
            getLoader().save(descriptionFile, description);
        }
    }
}
