package nsuaiot.service.Entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "plug")
public class Plug {
    @Id
    public Integer no;

    @Getter
    public String ownerId;

    @Getter
    @Column(name = "plug_id")
    public String plugId;

    @Getter
    @Column(name = "plug_name")
    public String plugName;

    @Column(name = "actual_device")
    public String actualDevice;

}
