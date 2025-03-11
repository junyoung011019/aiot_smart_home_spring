package nsuaiot.service.Controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.EditActionRequest;
import nsuaiot.service.Service.GroupActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/group/action")
public class GroupActionController {

    private final GroupActionService groupActionService;

    @PostMapping("/edit")
    @Transactional
    public ResponseEntity<String> editAction(@RequestBody EditActionRequest editActionRequest){
        return groupActionService.editAction(editActionRequest.getGroupId(),editActionRequest.getDevices());
    }

    @GetMapping("/check/{groupId}")
    public ResponseEntity<String> groupActionCheck(@PathVariable Long groupId){
        return groupActionService.checkAction(groupId);
    }

    @GetMapping("/run/{groupId}")
    public ResponseEntity<String> groupActionRun(@PathVariable Long groupId){
        return groupActionService.runAction(groupId);
    }
}
