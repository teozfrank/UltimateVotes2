package net.teozfrank.ultimatevotes.util;

/**
 * Created by Frank on 15/02/2015.
 */
public class TimedCmd {

    private Long startTime;
    private int duration;
    private String[] cmds;

    public TimedCmd(Long startTime, int duration, String[] cmd) {
        this.startTime = startTime;
        this.duration = duration;
        this.cmds = cmd;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String[] getCmds() {
        return cmds;
    }

    public String getCmd(int index) {
        return cmds[index];
    }

    public void setCmds(String[] cmds) {
        this.cmds = cmds;
    }
}
