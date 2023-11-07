package br.com.fiap.techchallenge.lambdaauthorizer.services;

import br.com.fiap.techchallenge.lambdaauthorizer.models.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.IOException;

public class AuthClient {

    private String baseUrl;

    private OkHttpClient client;

    private ObjectMapper responseMapper;

    public AuthClient() {
        client = new OkHttpClient();
        baseUrl = System.getenv("AUTH_URL");
        responseMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public User getUserInfo(String token) throws RuntimeException {
        var request = getRequest("/auth/info", token);

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            return parseResponseBody(response);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Request getRequest(String path, String $token) {
        return new Request.Builder()
                          .url(baseUrl + path)
                          .addHeader("Authorization", $token)
                          .addHeader("Content-Type", "application/json")
                          .build();
    }

    private User parseResponseBody(Response response) throws IOException {
        return responseMapper.readValue(response.body().byteStream(), User.class);
    }




}
