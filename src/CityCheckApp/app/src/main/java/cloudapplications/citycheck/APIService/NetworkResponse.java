package cloudapplications.citycheck.APIService;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
Created by T.Surkis on 25-Aug-17.
A wrapper class around the response to handle the data extraction and retrieval to the requesting listener.
*/

class NetworkResponse<ResponseType> implements Callback<ResponseType> {
    private WeakReference<NetworkResponseListener<ResponseType>> listener;

    NetworkResponse(NetworkResponseListener<ResponseType> listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    public void onResponse(@NonNull Call<ResponseType> call, @NonNull Response<ResponseType> response) {
        Log.d("Retrofit", "Retrofit successfull response: " + response.toString());
        if (listener.get() != null && listener != null) {
            listener.get().onResponseReceived(response.body());
        }
    }

    @Override
    public void onFailure(@NonNull Call<ResponseType> call, @NonNull Throwable t) {
        Log.d("Retrofit", "Retrofit error response: " + t.getMessage());
        if (listener.get() != null && listener != null) {
            listener.get().onError();
        }
    }
}
