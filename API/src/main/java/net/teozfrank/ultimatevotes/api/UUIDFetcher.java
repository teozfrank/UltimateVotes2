package net.teozfrank.ultimatevotes.api;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UUIDFetcher {

    public Map<String, UUID> call();

    public void writeBody(HttpURLConnection connection, String body);

    public HttpURLConnection createConnection();

    public UUID getUUID(String id);

    public byte[] toBytes(UUID uuid);

    public UUID fromBytes(byte[] array);

    public UUID getUUIDOf(String name);

}
