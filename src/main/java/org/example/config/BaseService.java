package org.example.config;

import org.kie.api.runtime.KieContainer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;

public abstract class BaseService<T extends BaseDto, S extends BaseEntity> {

    protected final String prePath = "rules/";

    protected KieContainer getKieContainer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected S getEntity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected String[] rulesFile() {
        S entity = getEntity();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String rulesPath = prePath + entity.getClass().getSimpleName().toLowerCase() + "/*";
        Resource[] resources;
        try {
            resources = resolver.getResources(rulesPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return Arrays.stream(resources).map(Resource::getFilename)
                .toArray(String[]::new);

    }

    public T evaluateDto(T dto) {
        String[] rulesFile = rulesFile();
    }

}
