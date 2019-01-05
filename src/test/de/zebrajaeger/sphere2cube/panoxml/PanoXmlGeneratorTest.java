package de.zebrajaeger.sphere2cube.panoxml;

import de.zebrajaeger.sphere2cube.result.Level;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.result.View;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class PanoXmlGeneratorTest {
    @Test
    public void test1() throws IOException {
        List<Level> levels = new LinkedList<>();
        levels.add(new Level(1, 100, 100, 1, 1));
        levels.add(new Level(2, 200, 200, 2, 2));
        levels.add(new Level(3, 400, 400, 5, 5));

        RenderedPano renderedPano = new RenderedPano(RenderedPano.Type.CUBIC, 512, View.of(), levels);

        String result = PanoXmlGenerator.of().generate(renderedPano);
        System.out.println(result);
    }
}