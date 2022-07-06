package model;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public enum ObjectMapperHolder {

    INSTANCE;
    private final ObjectMapper mapper;

    ObjectMapperHolder() {
        this.mapper = create();
    }

    private static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JobDeserializer js = new JobDeserializer(JobOffer.class);
        SimpleModule sm = new SimpleModule("JobDeserializer",
                new Version(1, 0, 0, null, null, null));
        sm.addDeserializer(JobOffer.class, js);
        mapper.registerModule(sm);

        mapper.findAndRegisterModules();
        return mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
