package com.cloudbalance.lens.dto.awsservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSInstanceDTO {
    private String id;
    private String name;
    private String region;
    private String status;
    private String engine;
}
