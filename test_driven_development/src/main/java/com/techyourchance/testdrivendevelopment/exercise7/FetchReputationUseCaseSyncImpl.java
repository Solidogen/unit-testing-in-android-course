package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;

public class FetchReputationUseCaseSyncImpl implements FetchReputationUseCaseSync {

    private GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSyncImpl(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public UseCaseResult fetchReputationSync() throws Exception {
        EndpointResult result = getReputationHttpEndpointSync.getReputationSync();
        switch (result.getStatus()) {
            case SUCCESS:
                return new UseCaseResult(Status.SUCCESS, result.getReputation());
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return new UseCaseResult(Status.FAILURE, 0);
            default:
                throw new Exception("Invalid result status: " + result.getStatus());
        }
    }
}
