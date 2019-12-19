package net.teozfrank.ultimatevotes.api;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UUIDFetcher {

    Map<String, UUID> call();

    void writeBody(HttpURLConnection connection, String body);

    HttpURLConnection createConnection();

    UUID getUUID(String id);

    byte[] toBytes(UUID uuid);

    UUID fromBytes(byte[] array);

    UUID getUUIDOf(String name);

    void setUsernames(List<String> usernames);

    void setRateLimited(boolean rateLimited);

}
