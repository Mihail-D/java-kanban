package server;

import exceptions.KVTaskClientLoadException;
import exceptions.KVTaskClientPutException;
import exceptions.KVTaskClientRegisterException;
import exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String url;
    private final String apiToken;

    public KVTaskClient(String url) {
        this.url = url;
        this.apiToken = register();
    }

    public void put(String key, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Запрос не может быть обработан.");
            }
        } catch (IOException | InterruptedException e) {
            throw new KVTaskClientPutException("Запрос не может быть обработан.");
        }
    }

    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Запрос не может быть обработан. " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new KVTaskClientLoadException("Запрос не может быть обработан.");
        }
    }

    private String register() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Запрос не может быть обработан.");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new KVTaskClientRegisterException("Запрос не может быть обработан.");
        }
    }
}
