import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class DullEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "INT UNSIGNED")
    private long id;

    public long getId() {
        return id;
    }

    @Version
    @Column(columnDefinition = "MEDIUMINT UNSIGNED")
    @NotNull
    private int version;

    @NotEmpty
    @Size(max = 255)
    private String shortName;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}