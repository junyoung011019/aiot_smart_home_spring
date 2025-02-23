package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Entity.GroupList;
import nsuaiot.service.Entity.GroupPlugManagement;
import nsuaiot.service.Repository.GroupListRepository;
import nsuaiot.service.Repository.GroupPlugManagementRepository;
import nsuaiot.service.Repository.PlugRepository;
import org.json.JSONArray;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupListRepository groupListRepository;
    private final GroupPlugManagementRepository groupPlugManagementRepository;
    private final PlugRepository plugRepository;

    public ResponseEntity<?> groupCheck(){
        List<GroupList> groupList = groupListRepository.findAll(Sort.by("creationTime"));
        if(groupList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(groupList);
    }

    public ResponseEntity<String> groupCreate(String groupName){
        if(groupListRepository.existsByGroupName(groupName)){
            return ResponseEntity.status(400).body("이미 있는 그룹명입니다.");
        }
        GroupList group = new GroupList(groupName);
        Long generatedId;
        do{
            generatedId=ThreadLocalRandom.current().nextLong(100000L, 199999L);;
        }while(groupListRepository.existsById(generatedId));
        group.setGroupId(generatedId);

        groupListRepository.save(group);
        return ResponseEntity.ok().body("그룹 생성 완료");
    }


}
