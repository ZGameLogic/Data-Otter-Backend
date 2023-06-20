package com.zgamelogic.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
public class MinecraftMonitor extends Monitor {

    private String url;
    private int port;

    private int max;
    private int online;
    private List<String> onlinePlayers;
    private String version;
    private String motd;

    public MinecraftMonitor(String url, int port, String name) {
        this.url = url;
        this.port = port;
        setName(name);
        setType("minecraft");
    }

    public void update(JSONObject json){
        if(json == null){
            setStatus(false);
            return;
        }
        setStatus(true);
        max = json.getJSONObject("players").getInt("max");
        online = json.getJSONObject("players").getInt("online");
        if(online > 0) {
            JSONArray players = json.getJSONObject("players").getJSONArray("sample");
            onlinePlayers = new LinkedList<>();
            for (int i = 0; i < players.length(); i++) {
                onlinePlayers.add(players.getJSONObject(i).getString("name"));
            }
        }
        version = json.getJSONObject("version").getString("name");
        motd = json.getJSONObject("description").getString("text");
    }
}
