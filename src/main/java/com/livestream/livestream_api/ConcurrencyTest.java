package com.livestream.livestream_api;

import java.net.URI;
import java.net.http.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyTest {

    static String BASE_URL = "http://localhost:8080";
    static Long   STREAM_ID = 85L;

    static String TOKEN_DAWOOD  = "";
    static String TOKEN_HAMZA   = "";
    static String TOKEN_SHAHID  = "";
    static String TOKEN_ALI     = "";
    static String TOKEN_OMAR    = "";
    static String TOKEN_YUSUF   = "";
    static String TOKEN_IBRAHIM = "";
    static String TOKEN_AHMED   = "";
    static String TOKEN_KHALID  = "";
    static String TOKEN_SAEED   = "";

    static AtomicInteger likes    = new AtomicInteger(0);
    static AtomicInteger gifts    = new AtomicInteger(0);
    static AtomicInteger viewers  = new AtomicInteger(0);
    static AtomicInteger comments = new AtomicInteger(0);
    static AtomicInteger total    = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {


        System.out.println("  testing ");
        System.out.println("  stream id: " + STREAM_ID);


        TOKEN_DAWOOD  = login("dawood@livestream.com",  "Dawood123");
        TOKEN_HAMZA   = login("hamza@livestream.com",   "Hamza123");
        TOKEN_SHAHID  = login("shahid@livestream.com",  "Shahid123");
        TOKEN_ALI     = login("ali@livestream.com",     "Ali123");
        TOKEN_OMAR    = login("omar@livestream.com",    "Omar123");
        TOKEN_YUSUF   = login("yusuf@livestream.com",   "Yusuf123");
        TOKEN_IBRAHIM = login("ibrahim@livestream.com", "Ibrahim123");
        TOKEN_AHMED   = login("ahmed@livestream.com",   "Ahmed123");
        TOKEN_KHALID  = login("khalid@livestream.com",  "Khalid123");
        TOKEN_SAEED   = login("saeed@livestream.com",   "Saeed123");

        System.out.println("All 10 users logged in.\n");
        System.out.println("Running all tests simultaneously...");
        System.out.println("Watch the stream page on your browser NOW!\n");

        long start = System.currentTimeMillis();

        String[] tokens = { TOKEN_DAWOOD, TOKEN_HAMZA, TOKEN_SHAHID, TOKEN_ALI, TOKEN_OMAR,
                TOKEN_YUSUF, TOKEN_IBRAHIM, TOKEN_AHMED, TOKEN_KHALID, TOKEN_SAEED };

        Thread t1 = new Thread(() -> runLikes(tokens));
        Thread t2 = new Thread(() -> runGifts(tokens));
        Thread t3 = new Thread(() -> runViewers());
        Thread t4 = new Thread(() -> runComments(tokens));

        t1.start(); t2.start(); t3.start(); t4.start();

        Thread progress = new Thread(() -> {
            int sec = 0;
            while (true) {
                try {
                    Thread.sleep(2000);
                    sec += 2;
                    System.out.println("[" + sec + "s] Total=" + total.get()
                            + " Likes=" + likes.get()
                            + " Gifts=" + gifts.get()
                            + " Viewers=" + viewers.get()
                            + " Comments=" + comments.get());
                } catch (InterruptedException e) { break; }
            }
        });
        progress.setDaemon(true);
        progress.start();

        t1.join(); t2.join(); t3.join(); t4.join();

        long end = System.currentTimeMillis();


        System.out.println("  finished in " + (end - start) + "in seconds");
        System.out.println("  Likes:    " + likes.get());
        System.out.println("  Gifts:    " + gifts.get() + "failedd");
        System.out.println("  Viewers:  " + viewers.get());
        System.out.println("  Comments: " + comments.get());
        System.out.println("  Total:    " + total.get() + " requests");

    }

    static void runLikes(String[] tokens) {
        ExecutorService ex = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 500; i++) {
            final String token = tokens[i % tokens.length];
            ex.submit(() -> {
                int s = post("/api/streams/" + STREAM_ID + "/likes", token, null);
                if (s == 200) likes.incrementAndGet();
                total.incrementAndGet();
            });
        }
        ex.shutdown();
        try { ex.awaitTermination(5, TimeUnit.MINUTES); } catch (Exception ignored) {}
        System.out.println(" Likes finished: " + likes.get());
    }

    static void runGifts(String[] tokens) {
        ExecutorService ex = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 500; i++) {
            final String token = tokens[i % tokens.length];
            ex.submit(() -> {
                int s = post("/api/streams/" + STREAM_ID + "/gifts", token, "{\"giftId\": 1}");
                if (s == 201) gifts.incrementAndGet();
                total.incrementAndGet();
            });
        }
        ex.shutdown();
        try { ex.awaitTermination(5, TimeUnit.MINUTES); } catch (Exception ignored) {}
        System.out.println("Gifts finished: " + gifts.get() + " sent");
    }

    static void runViewers() {
        ExecutorService ex = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 500; i++) {
            final int n = i + 1;
            ex.submit(() -> {
                int s = postGuest("/api/streams/" + STREAM_ID + "/join/guest?guestName=Guest" + n);
                if (s == 200) viewers.incrementAndGet();
                total.incrementAndGet();
            });
        }
        ex.shutdown();
        try { ex.awaitTermination(5, TimeUnit.MINUTES); } catch (Exception ignored) {}
        System.out.println("Viewers finished: " + viewers.get() + " joined");
    }

    static void runComments(String[] tokens) {
        String[] msgs = { "WOHOOOOO!", "bEST THING", "BooO", "stop it please", "  HhAHAHA  " };
        ExecutorService ex = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 500; i++) {
            final String token = tokens[i % tokens.length];
            final String msg   = msgs[i % msgs.length];
            ex.submit(() -> {
                int s = post("/api/streams/" + STREAM_ID + "/comments", token, "{\"message\": \"" + msg + "\"}");
                if (s == 200 || s == 201) comments.incrementAndGet();
                total.incrementAndGet();
            });
        }
        ex.shutdown();
        try { ex.awaitTermination(5, TimeUnit.MINUTES); } catch (Exception ignored) {}
        System.out.println(" Comments finished: " + comments.get() + " posted");
    }

    static int post(String path, String token, String body) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json");
            if (body != null) builder.POST(HttpRequest.BodyPublishers.ofString(body));
            else              builder.POST(HttpRequest.BodyPublishers.noBody());
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) { return 500; }
    }

    static int postGuest(String path) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Exception e) { return 500; }
    }

    static String login(String email, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            String res = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            String token = res.split("\"accessToken\":\"")[1].split("\"")[0];
            System.out.println("Logged in: " + email);
            return token;
        } catch (Exception e) {
            System.out.println("Login failed: " + email);
            return "";
        }
    }
}