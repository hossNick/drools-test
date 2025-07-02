package org.example.config;

import java.util.ArrayList;
import java.util.List;

public class BaseDto {

    private List<String> errorDescription;

    public List<String> getErrorDescription() {
        if(errorDescription.isEmpty())
            errorDescription= new ArrayList<>();
        return errorDescription;
    }


    public void addErrorDescription(String errorDescription) {
        getErrorDescription().add(errorDescription);
    }


}
