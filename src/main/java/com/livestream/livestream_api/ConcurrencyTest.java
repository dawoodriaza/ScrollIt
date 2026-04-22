package com.livestream.livestream_api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConcurrencyTest {

    static String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInN1YiI6IkRhd29vZEBsaXZlc3RyZWFtLmNvbSIsImlhdCI6MTc3Njc5ODI4OSwiZXhwIjoxNzc2ODg0Njg5fQ.bVSgl9bXMqry9tQkCrYjw42wm36QagyonEpQRI15nXk";
    static String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) throws Exception {
        testLikes();
        testGifts();
        testViewers();
    }

    static void testLikes() throws Exception {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> sendLike(81L));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Likes concurrency test finished");
    }

    static void testGifts() throws Exception {
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> sendGift(81L, 81L));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Gifts concurrency test finished");
    }

    static void testViewers() throws Exception {
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> joinStream(81L));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Viewer concurrency test finished");
    }

    static int sendLike(Long streamId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/streams/" + streamId + "/likes"))
                    .header("Authorization", "Bearer " + TOKEN)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    static int sendGift(Long streamId, Long giftId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"giftId\": " + giftId + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/streams/" + streamId + "/gifts"))
                    .header("Authorization", "Bearer " + TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    static int joinStream(Long streamId) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/streams/" + streamId + "/join"))
                    .header("Authorization", "Bearer " + TOKEN)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) {
            return 500;
        }
    }
}