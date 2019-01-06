package sh.mazurkiewicz.msc.model;

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
