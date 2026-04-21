package com.livestream.livestream_api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyTest {

    static String BASE_URL = "http://localhost:8080";

    static String TOKEN_DAWOOD = "";
    static String TOKEN_HAMZA  = "";
    static String TOKEN_SHAHID = "";

    static AtomicInteger likeSuccess   = new AtomicInteger(0);
    static AtomicInteger likeFail      = new AtomicInteger(0);
    static AtomicInteger giftSuccess   = new AtomicInteger(0);
    static AtomicInteger giftFail      = new AtomicInteger(0);
    static AtomicInteger viewerSuccess = new AtomicInteger(0);
    static AtomicInteger viewerFail    = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {

        System.out.println("Logging in users...");
        TOKEN_DAWOOD = getToken("Dawood@livestream.com", "Dawood123");
        TOKEN_HAMZA  = getToken("Hamza@livestream.com",  "Hamza123");
        TOKEN_SHAHID = getToken("Shahid@livestream.com", "Shahid123");
        System.out.println("All users logged in.");
        System.out.println("Starting concurrency tests...\n");

        testLikes();
        testGifts();
        testViewers();

        System.out.println("\nAll concurrency tests completed.");
    }

    static void testLikes() throws Exception {
        int taskCount   = 500000;
        int poolSize    = 100;
        String[] tokens = { TOKEN_DAWOOD, TOKEN_HAMZA, TOKEN_SHAHID };

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        long start = System.currentTimeMillis();

        for (int i = 0; i < taskCount; i++) {
            final String token = tokens[i % tokens.length];
            executor.submit(() -> {
                int status = sendLike(token, 7L);
                if (status == 200) likeSuccess.incrementAndGet();
                else               likeFail.incrementAndGet();
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("starting like tst: " + taskCount + " tasks -- pool=" + poolSize
                + " -- Success: " + likeSuccess.get()
                + " -- Failed: "  + likeFail.get()
                + " -- Time: "    + (end - start) + "ms");
    }

    static void testGifts() throws Exception {
        int taskCount   = 500000;
        int poolSize    = 100;
        String[] tokens = { TOKEN_DAWOOD, TOKEN_HAMZA, TOKEN_SHAHID };

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        long start = System.currentTimeMillis();

        for (int i = 0; i < taskCount; i++) {
            final String token = tokens[i % tokens.length];
            executor.submit(() -> {
                int status = sendGift(token, 7L, 1L);
                if (status == 201) giftSuccess.incrementAndGet();
                else               giftFail.incrementAndGet();
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("GIFT TEST: " + taskCount + " tasks -- pool=" + poolSize
                + " -- Success: " + giftSuccess.get()
                + " -- Failed: "  + giftFail.get()
                + " -- Time: "    + (end - start) + "ms");
    }

    static void testViewers() throws Exception {
        int taskCount = 500000;
        int poolSize  = 100;

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        long start = System.currentTimeMillis();

        for (int i = 0; i < taskCount; i++) {
            final int guestNum = i + 1;
            executor.submit(() -> {
                int status = joinAsGuest(7L, "Guest" + guestNum);
                if (status == 200) viewerSuccess.incrementAndGet();
                else               viewerFail.incrementAndGet();
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("startig viewer test " + taskCount + " tasks -- pool=" + poolSize
                + " -- Success: " + viewerSuccess.get()
                + " -- Failed: "  + viewerFail.get()
                + " -- Time: "    + (end - start) + "ms");
    }

    static String getToken(String email, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return response.split("\"accessToken\":\"")[1].split("\"")[0];
        } catch (Exception e) {
            return "";
        }
    }

    static int sendLike(String token, Long streamId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/streams/" + streamId + "/likes"))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    static int sendGift(String token, Long streamId, Long giftId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"giftId\": " + giftId + "}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/streams/" + streamId + "/gifts"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    static int joinAsGuest(Long streamId, String guestName) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/streams/" + streamId
                            + "/join/guest?guestName=" + guestName))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) {
            return 500;
        }
    }
}