package pl.dweb.moneysavingchallenge.model;

/**
 * Created by md on 4/5/17.
 */

public class ChallengeFinisher {
    private Integer id;
    private Boolean finished;

    public ChallengeFinisher(Integer id, Boolean finished) {
        this.id = id;
        this.finished = finished;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
}
