package de.zebrajaeger.sphere2cube.indexhtml;

import org.apache.commons.io.IOUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class IndexHtmGenerator {

    public static IndexHtmGenerator of() {
        return new IndexHtmGenerator();
    }

    private IndexHtmGenerator() {
    }

    public String generate(IndexHtml indexHtml) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("index.html.twig")) {
            String templateString = IOUtils.toString(is, "UTF-8");
            JtwigTemplate template = JtwigTemplate.inlineTemplate(templateString);
            JtwigModel model = JtwigModel.newModel().with("values", indexHtml);
            return template.render(model);
        }
    }

}
