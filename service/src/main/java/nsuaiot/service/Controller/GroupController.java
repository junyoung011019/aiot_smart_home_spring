package nsuaiot.service.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.GroupNameRequest;
import nsuaiot.service.Service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    //그룹 목록 조회
    @Transactional(readOnly = true)
    @GetMapping("/check/list")
    public ResponseEntity<?> groupCheck(@RequestAttribute("userId") String userId){
        return groupService.groupCheck(userId);
    }

    //그룹 생성
    @Transactional(readOnly = false)
    @PostMapping("/create")
    public ResponseEntity<?> groupCreate(@RequestBody GroupNameRequest groupNameRequest, @RequestAttribute("userId") String userId){
        return groupService.groupCreate(groupNameRequest.getGroupName(),userId);
    }

    //그룹 삭제
    @Transactional(readOnly = false)
    @DeleteMapping("/remove/{groupId}")
    public ResponseEntity<?> groupRemove(@PathVariable Long groupId, @RequestAttribute("userId") String userId){
        return groupService.groupRemove(groupId, userId);
    }

}
