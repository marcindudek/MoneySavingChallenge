package pl.dweb.moneysavingchallenge.model;

import android.support.v4.app.INotificationSideChannel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by md on 3/28/17.
 */

@DatabaseTable(tableName = "challenges")
public class ChallengeEntity {


    @DatabaseField(generatedId = true, columnName = "id")
    private Integer id;

    @DatabaseField(columnName = "amount")
    private Integer amount;

    @DatabaseField(columnName = "dues")
    private Integer dues;

    @DatabaseField(columnName = "purpose")
    private String purpose;

    @DatabaseField(columnName = "currency")
    private String currency;

    @DatabaseField(columnName = "start")
    private Date startTimestamp;

    @DatabaseField(columnName = "finish")
    private Date finishTimestamp;

    @DatabaseField(columnName = "canceled", defaultValue = "false")
    private Boolean isCanceled;

    public ChallengeEntity() {
    }

    public ChallengeEntity(Integer amount, Integer dues, String purpose, String currency) {
        this.amount = amount;
        this.dues = dues;
        this.purpose = purpose;
        this.currency = currency;
        this.startTimestamp = new Date();
    }

    public Integer getId() {
        return id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getDues() {
        return dues;
    }

    public void setDues(Integer dues) {
        this.dues = dues;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public Date getFinishTimestamp() {
        return finishTimestamp;
    }

    public void setFinishTimestamp(Date finishTimestamp) {
        this.finishTimestamp = finishTimestamp;
    }


    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }
}
