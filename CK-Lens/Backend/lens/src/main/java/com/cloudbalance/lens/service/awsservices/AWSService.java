package com.cloudbalance.lens.service.awsservices;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.awsservices.ASGDTO;
import com.cloudbalance.lens.dto.awsservices.EC2InstanceDTO;
import com.cloudbalance.lens.dto.awsservices.RDSInstanceDTO;
import java.util.List;

public interface AWSService {
    List<EC2InstanceDTO> fetchEC2Instances(Long accountId);
    List<RDSInstanceDTO> fetchRDSInstances( Long accountNumber );
    List<ASGDTO> fetchAutoScalingGroups(Long accountNumber );
    List<AssignAccountResponse>  fetchLinkedAccounts(Long id);
    List<AssignAccountResponse> fetchAllAccounts();
    List<AssignAccountResponse>  fetchAccountsByRole();

}
