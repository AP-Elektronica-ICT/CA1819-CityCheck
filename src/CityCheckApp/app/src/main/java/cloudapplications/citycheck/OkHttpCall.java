package cloudapplications.citycheck;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpCall {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    public String responseStr = "";

    public enum RequestStatus {
        Successful,
        Unsuccessful,
        Undefined
    }

    public RequestStatus status = RequestStatus.Undefined;

    private Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call delete(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public void post(String databaseIP, String route, String jsonBody) {
        OkHttpCall call = new OkHttpCall();
        Call postCall = call.post("http://" + databaseIP + "/api/citycheck/" + route, jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                status = RequestStatus.Unsuccessful;
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    responseStr = response.body().string();
                    Log.d("OkHttpCall", "Successful POST response: " + responseStr);
                    status = RequestStatus.Successful;
                } else {
                    // Als er een fout is bij de request
                    Log.d("OkHttpCall", "Error POST response: " + response.message());
                    status = RequestStatus.Unsuccessful;
                }
            }
        });
    }

    public void get(String databaseIP, String route) {
        OkHttpCall call = new OkHttpCall();
        Call getCall = call.get("http://" + databaseIP + "/api/citycheck/" + route, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                status = RequestStatus.Unsuccessful;
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    responseStr = response.body().string();
                    Log.d("OkHttpCall", "Successful GET response: " + responseStr);
                    status = RequestStatus.Successful;
                } else {
                    // Als er een fout is bij de request
                    Log.d("OkHttpCall", "Error GET response: " + response.message());
                    status = RequestStatus.Unsuccessful;
                }
            }
        });
    }

    public void delete(String databaseIP, String route) {
        OkHttpCall call = new OkHttpCall();
        Call deleteCall = call.delete("http://" + databaseIP + "/api/citycheck/" + route, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                status = RequestStatus.Unsuccessful;
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    responseStr = response.body().string();
                    Log.d("OkHttpCall", "Successful DELETE response: " + responseStr);
                    status = RequestStatus.Successful;
                } else {
                    // Als er een fout is bij de request
                    Log.d("OkHttpCall", "Error DELETE response: " + response.message());
                    status = RequestStatus.Unsuccessful;
                }
            }
        });
    }
}
