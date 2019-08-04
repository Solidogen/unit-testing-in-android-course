package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.*;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
public class UpdateUsernameUseCaseSyncTest {

    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    UsersCache usersCacheMock;
    EventBusPoster eventBusPosterMock;

    UpdateUsernameUseCaseSync SUT;
    private String USER_ID = "USER_ID";
    private String USERNAME = "USERNAME";

    @Before
    public void setUp() throws Exception {
        updateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusPosterMock);
        success(); // default, will override this with various ***error() methods
    }

    @Test
    public void updateUsername_success_returnSuccess() throws Exception {
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsername_authError_returnFailure() throws Exception {
        authError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_serverError_returnFailure() throws Exception {
        serverError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_generalError_returnFailure() throws Exception {
        generalError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    // network error return network error (RACZEJ nie musze tego testowac, bo tam ma leciec wyjatek, ale tbh chuj wie, narazie to testuje)
    // TODO nie wiem czy jest ten jest dobrze napisany

    @Test
    public void updateUsername_networkError_returnFailure() throws Exception {
        networkError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_success_argumentsPassedToEndpoint() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(updateUsernameHttpEndpointSyncMock, times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USERNAME));
    }

    // success user cached

    @Test
    public void updateUsername_success_userCached() {
        SUT.updateUsernameSync(USER_ID, USERNAME);
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        verify(usersCacheMock, times(1)).cacheUser(ac.capture());
        User capturedUser = ac.getValue();
        assertThat(capturedUser.getUserId(), is(USER_ID));
        assertThat(capturedUser.getUsername(), is(USERNAME));
    }

    // auth error user not cached

    @Test
    public void updateUsername_authError_userCacheNotInteractedWith() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    // server error user not cached

    @Test
    public void updateUsername_serverError_userCacheNotInteractedWith() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    // general error user not cached

    @Test
    public void updateUsername_generalError_userCacheNotInteractedWith() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    // network error user not cached (RACZEJ nie musze tego testowac, bo tam ma leciec wyjatek, ale tbh chuj wie, narazie to testuje)

    @Test
    public void updateUsername_networkError_userCacheNotInteractedWith() throws Exception {
        networkError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    // success event posted

    @Test
    public void updateUsername_success_eventBusPostedSuccessEvent() {
        SUT.updateUsernameSync(USER_ID, USERNAME);
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        verify(eventBusPosterMock, times(1)).postEvent(ac.capture());
        Object postedEvent = ac.getValue();
        assertThat(postedEvent, instanceOf(UserDetailsChangedEvent.class));
    }

    // auth error event not posted

    @Test
    public void updateUsername_authError_eventBusNotInteractedWith() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    // server error event not posted

    @Test
    public void updateUsername_serverError_eventBusNotInteractedWith() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    // general error event not posted

    @Test
    public void updateUsername_generalError_eventBusNotInteractedWith() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    // network error event not posted (RACZEJ nie musze tego testowac, bo tam ma leciec wyjatek, ale tbh chuj wie, narazie to testuje)

    @Test
    public void updateUsername_networkError_eventBusNotInteractedWith() throws Exception {
        networkError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    // ------------------------------------------------------------

    private void success() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void authError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void generalError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void networkError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenThrow(new NetworkErrorException());
    }
}