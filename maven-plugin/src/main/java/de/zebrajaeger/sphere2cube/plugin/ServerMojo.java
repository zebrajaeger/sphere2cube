package de.zebrajaeger.sphere2cube.plugin;

import de.zebrajaeger.sphere2cube.httpserver.StaticWebServer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class ServerMojo extends AbstractMojo {

    @Parameter(property = "targetFolder", defaultValue = "${project.build.directory}")
    protected File targetFolder;

    public void execute() throws MojoExecutionException {
        try {
            StaticWebServer.of(targetFolder)
                    .start()
                    .openBrowser();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not start server", e);
        }
    }
}