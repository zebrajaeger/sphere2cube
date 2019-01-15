package de.zebrajaeger.sphere2cube.plugin;

import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@SuppressWarnings("unused")
@Mojo(name = "krpano", requiresProject = false, defaultPhase = LifecyclePhase.COMPILE)
public class KrPanoMojo extends PanoMojo {

    @Parameter(property = "krPanoExe", defaultValue = "${krpano.exe}")
    private File krPanoExe;
    @Parameter(property = "krPanoConfig", defaultValue = "${krpano.config}")
    private File krPanoConfig;
    @Parameter(property = "krPanoRenderTimeout", defaultValue = "7200")
    private long krPanoRenderTimeout;
    @Parameter(property = "modifyKrPanoConfig", defaultValue = "true")
    private boolean modifyKrPanoConfig;

    @Override
    protected void createPano(String imageName, SourceImage sourceImage, ViewCalculator.PanoView panoView) throws IOException {
        // TODO implement me
    }
}