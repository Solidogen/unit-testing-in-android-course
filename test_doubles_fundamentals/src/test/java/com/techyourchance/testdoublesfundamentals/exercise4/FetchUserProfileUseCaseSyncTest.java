package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

public class FetchUserProfileUseCaseSyncTest {

    private static final String USER_ID = "userId";
    private static final String FULL_NAME = "FULL_NAME";
    private static final String IMAGE_URL = "IMAGE_URL";

    private UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSyncTd;
    private UsersCacheTd mUsersCacheTd;

    private FetchUserProfileUseCaseSync SUT;

    //----------------------------------------------------------------------------------------------
    // Helper classes

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        String mUserId = "";
        boolean mIsServerError;
        boolean mIsAuthError;
        boolean mIsGeneralError;
        boolean mIsNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, mUserId, "", "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, mUserId, "", "");
            } else if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, mUserId, "", "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, mUserId, FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {

        List<User> mCachedUserList = new ArrayList<>();

        @Override
        public void cacheUser(User user) {
            User existingUser = getUser(user.getUserId());
            if (existingUser != null) {
                mCachedUserList.remove(existingUser);
            }
            mCachedUserList.add(user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            for (User user : mCachedUserList) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }
    }

    @Before
    public void setUp() throws Exception {
        mUserProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        mUsersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSyncTd, mUsersCacheTd);
    }

    @Test
    public void fetchUser_success_userIdPassedToEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUserProfileHttpEndpointSyncTd.mUserId, is(USER_ID));
    }

    @Test
    public void fetchUser_success_userCached() {
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = mUsersCacheTd.getUser(USER_ID);
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getFullName(), is(FULL_NAME));
        assertThat(cachedUser.getImageUrl(), is(IMAGE_URL));
    }

    @Test
    public void fetchUser_authError_userNotCached() {
        mUserProfileHttpEndpointSyncTd.mIsAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = mUsersCacheTd.getUser(USER_ID);
        assertThat(cachedUser, is(nullValue()));
    }

    @Test
    public void fetchUser_serverError_userNotCached() {
        mUserProfileHttpEndpointSyncTd.mIsServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = mUsersCacheTd.getUser(USER_ID);
        assertThat(cachedUser, is(nullValue()));
    }

    @Test
    public void fetchUser_networkError_userNotCached() {
        mUserProfileHttpEndpointSyncTd.mIsNetworkError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = mUsersCacheTd.getUser(USER_ID);
        assertThat(cachedUser, is(nullValue()));
    }

    @Test
    public void fetchUser_generalError_userNotCached() {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = mUsersCacheTd.getUser(USER_ID);
        assertThat(cachedUser, is(nullValue()));
    }

    @Test
    public void fetchUser_success_successReturned() {
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchUser_authError_failureReturned() {
        mUserProfileHttpEndpointSyncTd.mIsAuthError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUser_serverError_failureReturned() {
        mUserProfileHttpEndpointSyncTd.mIsServerError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUser_generalError_failureReturned() {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUser_networkError_networkErrorReturned() {
        mUserProfileHttpEndpointSyncTd.mIsNetworkError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }
}