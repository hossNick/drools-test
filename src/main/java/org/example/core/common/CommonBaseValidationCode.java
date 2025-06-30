package org.example.core.common;


import java.util.ArrayList;
import java.util.List;

/**
 * format is like : ERR_{scenario_id}_{error_code_of_scenario_id}
 * ie: ERR_10000_10001 -&gt; { scenario_id: 1000,error_code:10001 }
 * scenario_id is like errors related to AnvaBimeh
 * error_code is like special error code related to the scenario_id like required of {filed} in AnvaBimeh scenario errors
 */

public enum CommonBaseValidationCode implements BaseValidationCode {

    //REQUIRED
    ERR_10000_1001,
    //DUPLICATED
    ERR_10000_1003,
    //NOT_FOUND
    ERR_10000_1004,
    //VERSIONING_SAME_DAY
    ERR_10000_1005,
    //SHOULD_BE_NULL
    ERR_10000_1006,
    //ExternalServiceError
    ERR_10000_1007,
    //IS_DELETED
    ERR_10000_1008,
    //UN_EDITABLE
    ERR_10000_1009,
    //MIN_VALUE
    ERR_10000_1010,
    //MAX_VALUE
    ERR_10000_1011,
    //BETWEEN
    ERR_10000_1012,
    //UN_DELETABLE
    ERR_10000_1013,
    //MoreThanOneEntityFoundException
    ERR_10000_1014,
    // Faal entity not found
    ERR_10000_1015,
    //ENTITY NOT FOUND
    ERR_10000_1016,
    //ENTITY NOT FOUND with property
    ERR_10000_1017,
    //File
    ERR_10000_1018,
    //ENTITY is deleted
    ERR_10000_1019,
    //Ghatei entity is eddited
    ERR_10000_1020,
    //gheir faal is eddited
    ERR_10000_1021,
    //entity movaghat found
    ERR_10000_1022,
    // podSpace
    ERR_10000_1023,
    ;

    private List<String> params;
    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public List<String> getParameters() {
        if (this.params == null) return new ArrayList<String>();
        return this.params;
    }

    @Override
    public void setParameters(List<String> parameters) {
        this.params = parameters;
    }
}