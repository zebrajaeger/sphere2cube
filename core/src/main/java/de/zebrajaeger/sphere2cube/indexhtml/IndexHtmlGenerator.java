package de.zebrajaeger.sphere2cube.indexhtml;

import org.apache.commons.io.IOUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.InputStream;

public abstract class IndexHtmlGenerator {

    protected String generate(String templateName, Object values) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(templateName)) {
            String templateString = IOUtils.toString(is, "UTF-8");
            JtwigTemplate template = JtwigTemplate.inlineTemplate(templateString);
            JtwigModel model = JtwigModel.newModel().with("v", values);
            return template.render(model);
        }
    }
}
