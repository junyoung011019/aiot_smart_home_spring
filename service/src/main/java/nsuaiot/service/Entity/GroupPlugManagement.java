package nsuaiot.service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "GroupPlugManagement")
public class GroupPlugManagement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;
    private Long groupId;
    private String plugId;

    @Setter
    private String action;

    public GroupPlugManagement(){}

    public GroupPlugManagement(Long groupId, String plugId, String action) {
        this.groupId = groupId;
        this.plugId = plugId;
        this.action = action;
    }
}
