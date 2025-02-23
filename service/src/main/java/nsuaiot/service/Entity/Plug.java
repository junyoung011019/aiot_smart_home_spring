package nsuaiot.service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plug")
public class Plug {
    @Id
    public Integer no;
    public String owner;

    @Column(name = "plug_id")
    public String plugId;

    @Column(name = "plug_name")
    public String plugName;

    @Column(name = "actual_device")
    public String actualDevice;
}
