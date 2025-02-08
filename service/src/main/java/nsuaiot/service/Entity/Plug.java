package nsuaiot.service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Plug {
    @Id
    public Integer no;
    public String owner;
    public String plugId;
    public String plugName;
    public String actualDevice;
}
