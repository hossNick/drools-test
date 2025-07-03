package org.example.config;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public abstract class BaseService<T extends BaseDto, S extends BaseEntity> {

    protected final String prePath = "rules/";

    protected KieContainer getKieContainer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected S getEntity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public T evaluateDto(T dto) {
        KieSession session = null;
        try {
             session = getKieContainer().newKieSession();
            session.insert(dto);
            session.setGlobal("service", this);
            int ruleFired = session.fireAllRules();
            return (T) dto;
        }finally {
            assert session != null;
            session.dispose();
        }

    }

}
