package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.Entity.GroupList;
import nsuaiot.service.Entity.GroupPlugManagement;
import nsuaiot.service.Repository.GroupListRepository;
import nsuaiot.service.Repository.GroupPlugManagementRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupListRepository groupListRepository;
    private final GroupPlugManagementRepository groupPlugManagementRepository;

    //그룹 목록 조회
    public ResponseEntity<?> groupCheck(String userId){
        Optional<List<GroupList>> groupList = groupListRepository.findByOwnerId(userId);
        if(groupList.isEmpty()){
            return ResponseEntity.status(204).body(new ApiResponse("그룹 리스트가 없습니다."));
        }
        return ResponseEntity.ok().body(groupList);
    }

    //그룹 생성
    public ResponseEntity<?> groupCreate(String groupName, String userId){
        //해당 유저에 그룹명이 존재하는지 확인
        if(groupListRepository.existsByOwnerIdAndGroupName(userId, groupName)){
            return ResponseEntity.status(409).body(new ApiResponse("이미 존재하는 그룹명입니다."));
        }

        //그룹의 아이디가 중복이라면 다시 랜덤값 부여
        Long generatedId;
        do{
            generatedId=ThreadLocalRandom.current().nextLong(100000L, 199999L);;
        }while(groupListRepository.existsById(generatedId));
        
        //생성자로 그룹 객체(그룹명, 주인, 그룹 ID)생성 및 저장 
        GroupList group = new GroupList(userId, groupName,generatedId);
        groupListRepository.save(group);
        
        return ResponseEntity.ok().body(new ApiResponse("그룹 생성 완료"));
    }

    //그룹 삭제
    public ResponseEntity<?> groupRemove(Long groupId, String userId){
        //그룹 있는지 여부 검사
        Optional<GroupList> deleteGroup = groupListRepository.findByGroupIdAndOwnerId(groupId,userId);
        if(deleteGroup.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("그룹이 존재하지 않습니다."));
        }
        
        //그룹 내 액션 제거
        Optional <List<GroupPlugManagement>> deletePlugs = groupPlugManagementRepository.findByGroupId(groupId);
        try {
            groupPlugManagementRepository.deleteAll(deletePlugs.get());
            groupListRepository.deleteById(groupId);
            return ResponseEntity.ok().body(new ApiResponse("그룹 삭제 완료"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("서버 오류"));
        }

    }

}
