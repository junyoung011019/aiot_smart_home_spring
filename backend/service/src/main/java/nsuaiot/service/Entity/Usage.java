package nsuaiot.service.Entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Document(collection = "heyHomeUsage")
public class Usage {

    @Id
    private String id;

    @Setter
    private String plugId;

    @Setter
    private Date usageDate;

    @Setter
    private double usageValue;

    public Usage(String plugId, Date usageDate) {
        this.plugId = plugId;
        this.usageDate = usageDate;
    }

}
