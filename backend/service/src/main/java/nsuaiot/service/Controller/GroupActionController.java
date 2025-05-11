package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.EditActionRequest;
import nsuaiot.service.Service.GroupActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/group/action")
public class GroupActionController {

    private final GroupActionService groupActionService;

    //그룹 액션 추가/수정/삭제
    @Transactional(readOnly = false)
    @PutMapping("/edit")
    public ResponseEntity<?> editAction(@RequestBody EditActionRequest editActionRequest, @RequestAttribute("userId") String userId){
        return groupActionService.editAction(editActionRequest.getGroupId(),editActionRequest.getDevices(),userId);
    }

    //그룹 액션 조회
    @Transactional(readOnly = true)
    @GetMapping("/check/{groupId}")
    public ResponseEntity<?> groupActionCheck(@PathVariable Long groupId, @RequestAttribute("userId") String userId){
        return groupActionService.checkAction(groupId,userId);
    }

    //그룹 액션 실행
    @Transactional(readOnly = true)
    @GetMapping("/run/{groupId}")
    public ResponseEntity<?> groupActionRun(@PathVariable Long groupId, @RequestAttribute("userId") String userId){
        return groupActionService.runAction(groupId,userId);
    }
}
