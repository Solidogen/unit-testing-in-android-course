package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncTest {

    // region constants

    // endregion constants

    // region helper fields

    @Mock UsersCache usersCacheMock;
    @Mock FetchUserHttpEndpointSync fetchUserHttpEndpointSyncMock;

    // endregion helper fields

    private FetchUserUseCaseSync SUT;
    private String USER_ID = "user_id";
    private String USERNAME = "username";
    private User USER = new User(USER_ID, USERNAME);

    @Before
    public void setup() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSyncMock, usersCacheMock);
    }

    @Test
    public void fetchUser_parametersPassedToCache() throws Exception {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        userInCache();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, times(1)).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUser_userExistsInCache_successReturned() throws Exception {
        // Arrange
        userInCache();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUser_userExistsInCache_correctUserReturned() throws Exception {
        // Arrange
        userInCache();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUser_userExistsInCache_endpointNotInteractedWith() throws Exception {
        // Arrange
        userInCache();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verifyNoMoreInteractions(fetchUserHttpEndpointSyncMock);
    }

    @Test
    public void fetchUser_userNotInCache_paramsPassedToEndpoint() throws Exception {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        userNotInCache();
        endpointNetworkSuccess();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(fetchUserHttpEndpointSyncMock, times(1)).fetchUserSync(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointSuccess_correctUserReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointNetworkSuccess();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointSuccess_successReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointNetworkSuccess();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointSuccess_userSavedToCache() throws Exception {
        // Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        userNotInCache();
        endpointNetworkSuccess();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue(), is(USER));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointAuthError_nullReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointAuthError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), nullValue());
    }

    @Test
    public void fetchUser_userNotInCacheEndpointAuthError_failureReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointAuthError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointAuthError_userNotCached() throws Exception {
        // Arrange
        userNotInCache();
        endpointAuthError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointServerError_nullReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointServerError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), nullValue());
    }

    @Test
    public void fetchUser_userNotInCacheEndpointServerError_failureReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointServerError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointServerError_userNotCached() throws Exception {
        // Arrange
        userNotInCache();
        endpointServerError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointNetworkError_nullReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointNetworkError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), nullValue());
    }

    @Test
    public void fetchUser_userNotInCacheEndpointNetworkError_networkErrorReturned() throws Exception {
        // Arrange
        userNotInCache();
        endpointNetworkError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUser_userNotInCacheEndpointNetwork_userNotCached() throws Exception {
        // Arrange
        userNotInCache();
        endpointNetworkError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    // region helper methods

    private void endpointNetworkSuccess() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString())).thenReturn(new EndpointResult(SUCCESS, USER_ID, USERNAME));
    }

    private void endpointAuthError() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString())).thenReturn(new EndpointResult(AUTH_ERROR, "", ""));
    }

    private void endpointServerError() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString())).thenReturn(new EndpointResult(GENERAL_ERROR, "", ""));
    }

    private void endpointNetworkError() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString())).thenThrow(new NetworkErrorException());
    }

    private void userInCache() {
        when(usersCacheMock.getUser(anyString())).thenReturn(new User(USER_ID, USERNAME));
    }

    private void userNotInCache() {
        when(usersCacheMock.getUser(anyString())).thenReturn(null);
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}