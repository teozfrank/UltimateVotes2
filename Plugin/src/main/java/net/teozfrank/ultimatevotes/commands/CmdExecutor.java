package net.teozfrank.ultimatevotes.commands;

import net.teozfrank.ultimatevotes.main.UltimateVotes;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright teozfrank / FJFreelance 2014 All rights reserved.
 */
public class CmdExecutor {

    public UltimateVotes plugin;
    public ConcurrentHashMap<String, SubCmd> commands;
    public CmdExecutor(UltimateVotes plugin) {
        this.plugin = plugin;
        this.commands = new ConcurrentHashMap<String, SubCmd>();
    }
    public void addCmd(String name, SubCmd executor) {
        this.commands.put(name, executor);
    }
    public void addCmd(String name, SubCmd executor, String[] aliases) {
        commands.put(name, executor);
        executor.addAlias(name, name);
        for (String alias : aliases) {
            commands.put(alias, executor);
            executor.addAlias(alias, name);
        }
    }
    public SubCmd getCmd(String command) {
        return commands.get(command);
    }
    public void removeCmd(String name) {
        commands.remove(name);
    }
    public String[] makeParams(String[] baseParams, int offset) {
        int pLength = baseParams.length - offset;
        if (pLength < 1)
            return new String[0];
        String[] newParams = new String[pLength];
        for (int i = 0; i < pLength; i++) {
            newParams[i] = baseParams[i + offset];
        }
        return newParams;
    }
}