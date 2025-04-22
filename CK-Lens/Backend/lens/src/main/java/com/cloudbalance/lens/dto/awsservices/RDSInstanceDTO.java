package com.cloudbalance.lens.dto.awsservices;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RDSInstanceDTO {
    private String id;
    private String name;
    private String region;
    private String status;
    private String engine;
}
