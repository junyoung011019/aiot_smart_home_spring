package nsuaiot.service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Entity
@Table(name = "GroupList")
public class GroupList {

    @Id
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "creation_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationTime;

    @Column(name = "ownerId")
    private String ownerId;

    public GroupList() {}

    //시간 및 그룹 값 랜덤 부여
    public GroupList(String userId, String groupName,Long groupId){
        this.ownerId=userId;
        this.groupName=groupName;
        this.groupId=groupId;
        this.creationTime=LocalDateTime.now();
    }

}
