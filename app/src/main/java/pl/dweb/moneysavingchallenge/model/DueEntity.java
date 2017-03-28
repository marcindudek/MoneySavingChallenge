package pl.dweb.moneysavingchallenge.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by md on 3/28/17.
 */

@DatabaseTable(tableName = "dues")
public class DueEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName="challenge_id")
    private ChallengeEntity challenge;

    @DatabaseField(columnName = "due")
    private Integer due;

    @DatabaseField(columnName = "due_number")
    private Integer dueNumber;

    @DatabaseField(columnName = "timestamp")
    private Date timestamp;

    public DueEntity() {
    }

    public DueEntity(ChallengeEntity challenge, Integer due, Integer dueNumber, Date timestamp) {
        this.challenge = challenge;
        this.due = due;
        this.dueNumber = dueNumber;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public ChallengeEntity getChallenge() {
        return challenge;
    }

    public Integer getDue() {
        return due;
    }

    public Integer getDueNumber() {
        return dueNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
