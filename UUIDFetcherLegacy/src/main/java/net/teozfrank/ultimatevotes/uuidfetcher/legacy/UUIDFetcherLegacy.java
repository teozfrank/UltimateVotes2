package net.teozfrank.ultimatevotes.uuidfetcher.legacy;

import net.teozfrank.ultimatevotes.api.UUIDFetcher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;

public class UUIDFetcherLegacy implements UUIDFetcher {

    private static final double PROFILES_PER_REQUEST = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private final JSONParser jsonParser = new JSONParser();
    private List<String> usernames;
    private boolean rateLimited;


    @Override
    public Map<String, UUID> call() {
        Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        int requests = (int) Math.ceil(usernames.size() / PROFILES_PER_REQUEST);
        for (int i = 0; i < requests; i++) {
            HttpURLConnection connection = createConnection();
            String body = JSONArray.toJSONString(usernames.subList(i * 100, Math.min((i + 1) * 100, usernames.size())));
            writeBody(connection, body);
            JSONArray array = new JSONArray();
            try {
                array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                System.out.println("IO Exception reader in call: " + e.getMessage());
            } catch(ParseException e) {
                System.out.println("Error parsing inputstream reader in call: " + e.getMessage());
            }

            for (Object profile : array) {
                JSONObject jsonProfile = (JSONObject) profile;
                String id = (String) jsonProfile.get("id");
                String name = (String) jsonProfile.get("name");
                UUID uuid = getUUID(id);
                uuidMap.put(name, uuid);
            }
            if (rateLimited && i != requests - 1) {
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
            System.out.println("Malformed Exception creating connection to Mojang: " + e.getMessage());
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
        return null;
    }

    @Override
    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    @Override
    public void setRateLimited(boolean rateLimited) {
        this.rateLimited = rateLimited;
    }
}
