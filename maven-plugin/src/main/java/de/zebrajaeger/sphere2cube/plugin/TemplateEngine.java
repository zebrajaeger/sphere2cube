package de.zebrajaeger.sphere2cube.plugin;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TemplateEngine {
    private Map<String, Object> values = new HashMap<>();

    public static TemplateEngine of() {
        return new TemplateEngine();
    }

    private TemplateEngine() {
    }

    private TemplateEngine(TemplateEngine other) {
        other.values.entrySet().forEach(e -> values.put(e.getKey(), e.getValue()));
    }

    public TemplateEngine copy() {
        return new TemplateEngine(this);
    }

    public TemplateEngine with(String key, Object value) {
        values.put(key, value);
        return this;
    }

    public String convert(String templateString) {
        JtwigTemplate template = JtwigTemplate.inlineTemplate(templateString);
        JtwigModel model = JtwigModel.newModel(values);
        return template.render(model);
    }

    public File convertToFile(String templateString) {
        return new File(convert(templateString));
    }

    public File convertToFileAndCreateDirectories(String templateString, boolean isFile) {
        File result = new File(convert(templateString));
        if (!result.exists()) {
            if (isFile) {
                result.getParentFile().mkdirs();
            } else {
                result.mkdirs();
            }
        }
        return result;
    }
}
