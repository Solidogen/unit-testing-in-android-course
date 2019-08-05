package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    private GetContactsHttpEndpoint endpoint;
    private List<GetContactsHttpEndpoint.Callback> mListeners = new ArrayList<>();

    public FetchContactsUseCase(GetContactsHttpEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void fetchContacts(String filter) {
        endpoint.getContacts(filter, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> cartItems) {
                for (GetContactsHttpEndpoint.Callback listener : mListeners) {
                    listener.onGetContactsSucceeded(cartItems);
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) throws Exception {
                switch (failReason) {
                    case GENERAL_ERROR:
                    case NETWORK_ERROR:
                        notifyListenersAboutFailReason(failReason);
                        break;
                    default:
                        throw new Exception("invalid fail reason: " + failReason);
                }
            }
        });
    }

    public void registerListener(GetContactsHttpEndpoint.Callback callback) {
        mListeners.add(callback);
    }

    public void unregisterListener(GetContactsHttpEndpoint.Callback callback) {
        mListeners.remove(callback);
    }

    private void notifyListenersAboutFailReason(GetContactsHttpEndpoint.FailReason failReason) throws Exception {
        for (GetContactsHttpEndpoint.Callback listener : mListeners) {
            listener.onGetContactsFailed(failReason);
        }
    }
}
