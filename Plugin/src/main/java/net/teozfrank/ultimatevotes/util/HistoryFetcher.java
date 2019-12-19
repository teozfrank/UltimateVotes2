package net.teozfrank.ultimatevotes.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * modified example of UUID fetcher to retrieve the history
 * of a uuid profile.
 * https://gist.github.com/evilmidget38/26d70114b834f71fb3b4
 * The above used with this and HistoryFetcher class can be
 * used to get the old username of a player based on the UUID
 * It currently only adds players to the map if there username
 * has been changed, but this can be modified accordingly.
 */
public class HistoryFetcher implements Callable<Map<UUID, String>> {

    private static final String PROFILE_URL = "https://api.mojang.com/user/profiles/";
    private final JsonParser jsonParser = new JsonParser();
    private final List<UUID> uuids;

    public HistoryFetcher(List<UUID> uuids) {
        this.uuids = ImmutableList.copyOf(uuids);
    }

    public Map<UUID, String> call() {

        Map<UUID, List<HistoryResult>> uuidMap = new HashMap<UUID, List<HistoryResult>>();
        List<HistoryResult> historyResults = new ArrayList<HistoryResult>();
        Map<UUID, String> uuidToOrigionalName = new HashMap<UUID, String>();

            for(UUID uuid: uuids) {
                String uuidString = uuid.toString();
                JsonArray array = null;
                uuidString = uuidString.replaceAll("-", "");
                HttpURLConnection connection;
                try {
                    connection = createConnection(uuidString);
                    array = (JsonArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Object profile : array) {
                    JsonObject jsonProfile = (JsonObject) profile;
                    String name = jsonProfile.get("name").getAsString();
                    Long lastChanged = jsonProfile.get("lastChanged").getAsLong();
                    HistoryResult historyResult = new HistoryResult(name, lastChanged);
                    historyResults.add(historyResult);
                }
                if(historyResults.size() > 1) { //if the history result shows that a player has changed there name
                    uuidMap.put(uuid, historyResults);
                    List<HistoryResult> historyResults1 = uuidMap.get(uuid);
                    for(HistoryResult result: historyResults1) {
                        SendConsoleMessage.debug("name: " + result.getName());
                        SendConsoleMessage.debug("last changed: " + result.getChangedToAt());
                        if(result.getChangedToAt() == null) {
                            uuidToOrigionalName.put(uuid, result.getName());
                        }
                    }
                }
                historyResults.clear();
            }
        return uuidToOrigionalName;
    }

    private static HttpURLConnection createConnection(String uuidString) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(PROFILE_URL + uuidString + "/names");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

}
