package de.zebrajaeger.sphere2cube.panoxml;

import de.zebrajaeger.sphere2cube.result.RenderedPano;
import org.apache.commons.io.IOUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class PanoXmlGenerator {
    public static PanoXmlGenerator of() {
        return new PanoXmlGenerator();
    }

    private Map<String, Object> values = new HashMap<>();

    private PanoXmlGenerator() {
    }

    public PanoXmlGenerator variable(String name, Object value){
        values.put(name,value);
        return this;
    }

    public String generate(RenderedPano pano) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("pano.xml.twig")) {
            String templateString = IOUtils.toString(is, "UTF-8");
            JtwigTemplate template = JtwigTemplate.inlineTemplate(templateString);
            JtwigModel model = JtwigModel.newModel().with("pano", pano);
            for(Map.Entry<String,Object> v : values.entrySet()){
                model.with(v.getKey(), v.getValue());
            }
            return template.render(model);
        }
    }
}
