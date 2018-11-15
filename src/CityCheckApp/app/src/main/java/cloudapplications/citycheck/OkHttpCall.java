package cloudapplications.citycheck;


import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class OkHttpCall {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    String responseStr = "";

    enum RequestStatus {
        Successful,
        Unsuccessful,
        Undefined
    }

    RequestStatus status = RequestStatus.Undefined;

    Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    void post(String route, String jsonBody) {
        OkHttpCall call = new OkHttpCall();
        Call response = call.post("http://84.197.102.107/api/citycheck/" + route, jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    responseStr = response.body().string();
                    Log.d("OkHttpCall", "Successful response: " + responseStr);
                    status = RequestStatus.Successful;
                } else {
                    // Als er een fout is bij de request
                    Log.d("OkHttpCall", "Error response: " + response.message());
                    status = RequestStatus.Unsuccessful;
                }
            }
        });
    }
}
