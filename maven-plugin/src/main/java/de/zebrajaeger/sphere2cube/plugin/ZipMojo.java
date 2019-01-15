package de.zebrajaeger.sphere2cube.plugin;

import de.zebrajaeger.sphere2cube.utils.ZipUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
@Mojo(name = "zip", requiresProject = false, defaultPhase = LifecyclePhase.PACKAGE)
public class ZipMojo extends BaseMojo {
    @Parameter(property = "generateZip", defaultValue = "true")
    protected boolean generateZip;
    @Parameter(property = "zipSourceDirectory", defaultValue = "${project.build.directory}/{{imageName}}")
    protected String zipSourceDirectory;
    @Parameter(property = "zipFile", defaultValue = "${project.build.directory}/{{imageName}}.zip")
    protected String zipFile;
    //    @Parameter(property = "overwriteZipIfExists", defaultValue = "false")
//    private boolean overwriteZipIfExists;

    @Override
    protected void handleSourceImage(File sourceImage) throws IOException {
        String imageName = FileUtils.removeExtension(sourceImage.getName());
        if (generateZip) {
            File convertedZipFile = convertAndCreateDirectories(zipFile, imageName, true);
            File convertedZipSourceDirectory = convertAndCreateDirectories(zipSourceDirectory, imageName, false);
            getLog().info("Generate zip archive at: '" + convertedZipFile.getAbsolutePath() + "'");
            ZipUtils.compressDirectory(
                    convertedZipSourceDirectory,
                    convertedZipFile);
        }
    }
}
