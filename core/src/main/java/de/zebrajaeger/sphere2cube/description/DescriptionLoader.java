package de.zebrajaeger.sphere2cube.description;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class DescriptionLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DescriptionLoader.class);

    public static final SimpleDateFormat DATE_FORMAT1A = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT1B = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT1C = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT2A = new SimpleDateFormat("dd.MM.yyyy hh:mm");
    public static final SimpleDateFormat DATE_FORMAT2B = new SimpleDateFormat("dd.MM.yyyy hh:mm");

    private final ObjectMapper xmlMapper;
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;

    public DescriptionLoader() {
        xmlMapper = setDateFormats(new XmlMapper());
        jsonMapper = setDateFormats(new ObjectMapper());
        yamlMapper = setDateFormats(new ObjectMapper(new YAMLFactory()));
    }

    private ObjectMapper setDateFormats(ObjectMapper mapper) {
        mapper.setDateFormat(DATE_FORMAT1A);
        mapper.setDateFormat(DATE_FORMAT1B);
        mapper.setDateFormat(DATE_FORMAT1C);
        mapper.setDateFormat(DATE_FORMAT2A);
        mapper.setDateFormat(DATE_FORMAT2B);
        return mapper;
    }

    public static void main(String[] args) throws IOException {
        Optional<Description> desc = new DescriptionLoader().load(new File("C:\\temp\\sphere2cube\\test.xml"));
        if (desc.isPresent()) {
            System.out.println(desc.get());
        }
    }

    private Optional<ObjectMapper> getMapperForFile(File file) {
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        ObjectMapper mapper = null;
        if ("xml".equals(extension)) {
            mapper = xmlMapper;
        } else if ("json".equals(extension)) {
            mapper = jsonMapper;
        } else if ("yaml".equals(extension)) {
            mapper = yamlMapper;
        }
        return Optional.of(mapper);
    }

    public Optional<Description> load(File file) throws IOException {
        Description result = null;

        if (file.exists() && file.isFile()) {
            Optional<ObjectMapper> mapper = getMapperForFile(file);
            if (mapper.isPresent()) {
                result = mapper.get().readValue(file, Description.class);
            }
        }

        return Optional.ofNullable(result);
    }

    public void save(File file, Description description) throws IOException {
        jsonMapper.writeValue(file, description);
    }
}
