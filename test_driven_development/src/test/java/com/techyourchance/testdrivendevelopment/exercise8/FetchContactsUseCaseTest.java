package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    // region constants
    public static final String FILTER = "filter";

    // endregion constants

    // region helper fields

    @Mock
    GetContactsHttpEndpoint getContactsHttpEndpointMock;

    @Mock
    Callback listenerMock1;

    @Mock
    Callback listenerMock2;

    // endregion helper fields

    FetchContactsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchContactsUseCase(getContactsHttpEndpointMock);
        endpointSuccess();
    }

    @Test
    public void fetchContacts_dataPassedToEndpoint() throws Exception {
        // Arrange
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchContacts(FILTER);
        // Assert
        verify(getContactsHttpEndpointMock).getContacts(argumentCaptor.capture(), any(Callback.class));
        assertThat(argumentCaptor.getValue(), is(FILTER));
    }

    @Test
    public void fetchContacts_endpointSuccess_listenersNotifiedWithData() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContacts(FILTER);
        // Assert
        verify(listenerMock1).onGetContactsSucceeded(getContacts());
        verify(listenerMock2).onGetContactsSucceeded(getContacts());
    }

    @Test
    public void fetchContacts_endpointGeneralError_listenersNotifiedWithError() throws Exception {
        // Arrange
        endpointGeneralError();
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContacts(FILTER);
        // Assert
        verify(listenerMock1).onGetContactsFailed(FailReason.GENERAL_ERROR);
        verify(listenerMock2).onGetContactsFailed(FailReason.GENERAL_ERROR);
    }

    @Test
    public void fetchContacts_endpointNetworkError_listenersNotifiedWithError() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContacts(FILTER);
        // Assert
        verify(listenerMock1).onGetContactsFailed(FailReason.NETWORK_ERROR);
        verify(listenerMock2).onGetContactsFailed(FailReason.NETWORK_ERROR);
    }

    @Test
    public void fetchContacts_endpointSuccess_unregisteredListenerNotInteractedWith() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.unregisterListener(listenerMock2);
        SUT.fetchContacts(FILTER);
        // Assert
        verify(listenerMock1).onGetContactsSucceeded(getContacts());
        verifyZeroInteractions(listenerMock2);
    }

    // region helper methods

    @NotNull
    private List<ContactSchema> getContacts() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema("asd", "asd", "asd", "asd", 20));
        return contactSchemas;
    }

    private void endpointSuccess() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContacts());
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void endpointGeneralError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void endpointNetworkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}