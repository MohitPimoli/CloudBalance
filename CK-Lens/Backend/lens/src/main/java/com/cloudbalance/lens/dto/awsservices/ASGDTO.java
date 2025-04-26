package com.cloudbalance.lens.dto.awsservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ASGDTO {
    private String id;
    private String name;
    private String region;
    private int desiredCapacity;
    private int minSize;
    private int maxSize;
    private String status;
}
