package com.web.tip.project;

import com.web.tip.BasicResponse;
import com.web.tip.project.request.ProjectRequest;
import com.web.tip.project.response.ProjectResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiResponses(value = {@ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class)})

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/project")
public class ProjectController {

    ProjectService projectService;
    private static final String SUCCESS = "success";

    // 프로젝트 목록 반환
    @GetMapping("/getList")
    @ApiOperation(value = "프로젝트 목록 반환")
    public Object getProject(@RequestParam String memberId, @RequestParam boolean isDone) {
        log.info((isDone ? "완료된 " : "진행 중인 ") + "프로젝트 목록 반환");

        List<ProjectResponse> projectList = projectService.getProjectList(memberId, isDone);
        final BasicResponse result = new BasicResponse();

        if (projectList != null) {

            result.status = true;
            result.data = SUCCESS;
            result.object = projectList;

            return new ResponseEntity<>(result, HttpStatus.OK);

        } else {

            result.status = false;
            result.data = "fail";
            result.object = null;

            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);

        }
    }

    // 프로젝트 추가
    @PostMapping("/addProject/{memberId}")
    @ApiOperation(value = "프로젝트 추가")
    public Object addProject(@RequestBody ProjectDto projectDto, @PathVariable(name = "memberId") String memberId) {
        log.info("프로젝트 추가");
        System.out.println(projectDto);
        final BasicResponse result = new BasicResponse();

        if (projectService.addProject(projectDto, memberId)) {

            result.status = true;
            result.data = SUCCESS;
            result.object = null;

            return new ResponseEntity<>(result, HttpStatus.OK);

        } else {

            result.status = false;
            result.data = "fail";
            result.object = null;

            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);

        }
    }

    // 프로젝트 상태변경
    @PutMapping("/changeInfo")
    @ApiOperation(value = "프로젝트 정보 수정")
    public Object changeInfoProject(@RequestBody ProjectRequest projectRequest) {
        log.info("프로젝트 정보 수정");
        final BasicResponse result = new BasicResponse();

        if (projectService.modifyProject(projectRequest)) {

            result.status = true;
            result.data = SUCCESS;
            result.object = null;

            return new ResponseEntity<>(result, HttpStatus.OK);

        } else {

            result.status = false;
            result.data = "fail";
            result.object = null;

            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);

        }
    }

    // 프로젝트 상태변경
    @PutMapping("/changeDone")
    @ApiOperation(value = "프로젝트 진행 / 완료")
    public Object changeDoneProject(@RequestBody String projectId) {
        log.info("프로젝트 진행 / 완료");

        final BasicResponse result = new BasicResponse();

        if (projectService.finishProject(projectId)) {

            result.status = true;
            result.data = SUCCESS;
            result.object = null;

            return new ResponseEntity<>(result, HttpStatus.OK);

        } else {

            result.status = false;
            result.data = "fail";
            result.object = null;

            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);

        }
    }
}
