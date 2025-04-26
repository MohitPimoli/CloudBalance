package com.cloudbalance.lens.dto.awsservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EC2InstanceDTO {
    private String id;
    private String name;
    private String region;
    private String status;
}
