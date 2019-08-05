package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.*;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private final FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private final UsersCache usersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        User cachedUser = usersCache.getUser(userId);
        if (cachedUser != null) {
            return new UseCaseResult(Status.SUCCESS, cachedUser);
        }
        try {
            EndpointResult result = fetchUserHttpEndpointSync.fetchUserSync(userId);
            if (result.getStatus() == EndpointStatus.SUCCESS) {
                usersCache.cacheUser(new User(result.getUserId(), result.getUsername()));
                return new UseCaseResult(Status.SUCCESS, new User(result.getUserId(), result.getUsername()));
            } else {
                return new UseCaseResult(Status.FAILURE, null);
            }

        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }
    }
}