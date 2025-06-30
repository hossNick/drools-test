package org.example.core.common;




import org.example.core.common.util.LocaleUtil;

import java.util.List;

public  interface BaseValidationCode {

    public String getCode();
    public List<String> getParameters();
    public void setParameters(List<String> parameters);

    default String getLocalizedMessage() {
        String[] translatedParams = null;
        if (getParameters() != null){
            translatedParams = getParameters().stream().map(item-> LocaleUtil.getFarsiMessage(item,null)).toArray(String[]::new);
        }
        return LocaleUtil.getFarsiMessage(getCode(),translatedParams);
    }

    default String getLocalizedMessage(List<String> parameters) {
        String[] translatedParams = null;
        if (parameters != null){
            translatedParams = parameters.stream().map(item-> LocaleUtil.getFarsiMessage(item,null)).toArray(String[]::new);
        }
        return LocaleUtil.getFarsiMessage(getCode(),translatedParams);
    }

}