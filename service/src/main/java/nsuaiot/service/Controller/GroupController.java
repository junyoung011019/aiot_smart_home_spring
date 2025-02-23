package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.AddActionRequest;
import nsuaiot.service.DTO.GroupNameRequest;
import nsuaiot.service.Service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/check/list")
    public ResponseEntity<?> groupCheck(){
        return groupService.groupCheck();
    }

    @PostMapping("/create")
    public ResponseEntity<String> groupCreate(@RequestBody GroupNameRequest groupNameRequest){
        return groupService.groupCreate(groupNameRequest.getGroupName());
    }
    


}
