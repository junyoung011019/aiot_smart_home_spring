package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.AddActionRequest;
import nsuaiot.service.DTO.GroupNameRequest;
import nsuaiot.service.Service.GroupActionService;
import nsuaiot.service.Service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/group/action")
public class GroupActionController {

    private final GroupActionService groupActionService;

    @PostMapping("/add")
    public ResponseEntity<String> addAction(@RequestBody AddActionRequest addActionRequest){
        return groupActionService.addAction(addActionRequest.getGroupId(),addActionRequest.getDevices());
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
