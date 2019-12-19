package net.teozfrank.ultimatevotes.uuidfetcher.latest;

import net.teozfrank.ultimatevotes.api.UUIDFetcher;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;

public class UUIDFetcherLatest implements UUIDFetcher {

    private static final double PROFILES_PER_REQUEST = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private final JsonParser jsonParser = new JsonParser();
    private final List<String> names;
    private final boolean rateLimiting;

    public UUIDFetcherLatest(List<String> names, boolean rateLimiting) {
        this.names = ImmutableList.copyOf(names);
        this.rateLimiting = rateLimiting;
    }

    public UUIDFetcherLatest(List<String> names) {
        this(names, true);
    }


    @Override
    public Map<String, UUID> call() {
        Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
        for (int i = 0; i < requests; i++) {
            HttpURLConnection connection = createConnection();

            String body = new Gson().toJson(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
            writeBody(connection, body);
            JsonArray array = new JsonArray();
            try {
                array = (JsonArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                System.out.println("Error parsing inputstream reader in call: " + e.getMessage());
            }

            for (Object profile : array) {
                JsonObject jsonProfile = (JsonObject) profile;

                String id = jsonProfile.get("id").getAsString();
                String name = jsonProfile.get("name").getAsString();
                UUID uuid = getUUID(id);
                uuidMap.put(name, uuid);
            }
            if (rateLimiting && i != requests - 1) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    System.out.println("Sleep interrupted " + e.getMessage());
                }

            }
        }
        return uuidMap;
    }

    @Override
    public void writeBody(HttpURLConnection connection, String body) {
        try {
            OutputStream stream = connection.getOutputStream();
            System.out.println("UUID body: " + body);
            stream.write(body.getBytes());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            System.out.println("Error writing UUID body: " + e.getMessage());
        }
    }

    @Override
    public HttpURLConnection createConnection() {

        try {
            URL url = new URL(PROFILE_URL);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            return connection;
        } catch (MalformedURLException e) {

        } catch (Exception e) {

        }
        return null;

    }

    @Override
    public UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" +id.substring(20, 32));
    }

    @Override
    public byte[] toBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    @Override
    public UUID fromBytes(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        long mostSignificant = byteBuffer.getLong();
        long leastSignificant = byteBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }

    @Override
    public UUID getUUIDOf(String name) {
        //return new UUIDFetcher(Arrays.asList(name)).call().get(name);
        return null;
    }
}
