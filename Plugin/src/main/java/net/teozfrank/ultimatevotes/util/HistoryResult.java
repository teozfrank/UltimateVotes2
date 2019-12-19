package net.teozfrank.ultimatevotes.util;

/**
 * Created by Frank on 06/02/2015.
 * Do what you like with this code!
 */
public class HistoryResult {

    private String name;
    private Long changedToAt;

    public HistoryResult(String name, Long changedToAt) {
        this.name = name;
        this.changedToAt = changedToAt;
    }

    public HistoryResult(String name) {
        this.name = name;
        this.changedToAt = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getChangedToAt() {
        return changedToAt;
    }

    public void setChangedToAt(Long changedToAt) {
        this.changedToAt = changedToAt;
    }
}
