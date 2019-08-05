package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.Status;
import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    // region constants
    // endregion constants

    // region helper fields

    @Mock
    private GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;

    // endregion helper fields

    FetchReputationUseCaseSync SUT;

    private static final int SUCCESS_REPUTATION = 13;
    private static final int FAILURE_REPUTATION = 0;

    @Before
    public void setup() throws Exception {
        SUT = new FetchReputationUseCaseSyncImpl(getReputationHttpEndpointSyncMock);
    }

    @Test
    public void fetchReputationSync_endpointSuccess_successReturned_() throws Exception {
        // Arrange
        endpointSuccess();
        // Act
        UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    @Test
    public void fetchReputationSync_endpointSuccess_reputationReturned() throws Exception {
        // Arrange
        endpointSuccess();
        // Act
        UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result.getReputation(), is(SUCCESS_REPUTATION));
    }

    @Test
    public void fetchReputationSync_generalError_failureReturned() throws Exception {
        // Arrange
        endpointGeneralError();
        // Act
        UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchReputation_generalError_zeroReputationReturned() throws Exception {
        // Arrange
        endpointGeneralError();
        // Act
        UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result.getReputation(), is(FAILURE_REPUTATION));
    }

    @Test
    public void fetchReputationSync_networkError_networkErrorReturned() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchReputation_networkError_zeroReputationReturned() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result.getReputation(), is(FAILURE_REPUTATION));
    }

    // region helper methods
    //

    private void endpointSuccess() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new EndpointResult(EndpointStatus.SUCCESS, SUCCESS_REPUTATION));
    }
    private void endpointGeneralError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, FAILURE_REPUTATION));
    }

    private void endpointNetworkError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new EndpointResult(EndpointStatus.NETWORK_ERROR, FAILURE_REPUTATION));
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}