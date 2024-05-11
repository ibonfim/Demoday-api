package com.demodayapi.controller;
import com.demodayapi.enums.DemodayStatusEnum;
import com.demodayapi.exceptions.ThereIsNotPeriodOfSubmissionException;
import com.demodayapi.models.Demoday;
import com.demodayapi.models.Project;
import com.demodayapi.models.User;
import com.demodayapi.services.DemodayService;
import com.demodayapi.services.ProjectService;
import com.demodayapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ProjectControler {

    @Autowired
    ProjectService projectService;

    @Autowired
    DemodayService demodayService;
    @Autowired
    UserService userService;

    @PostMapping("/newproject")
    public ResponseEntity<Project> postProject(@RequestBody Project newProject, HttpServletRequest request) {
        try {
            DemodayStatusEnum demodayStatus = demodayService.verifyphase1InProgress();
            if (demodayStatus != DemodayStatusEnum.PHASE1)
                throw new ThereIsNotPeriodOfSubmissionException();
            Demoday demoday = demodayService.getPhase1();
            newProject.setDemoday(demoday);
            User user = userService.getLoggedUser(request);
            newProject.setUser(user);
            Project savedProject = projectService.saveProject(newProject);
            return new ResponseEntity<>(savedProject, HttpStatus.CREATED);

        } catch (ConstraintViolationException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
        }
    }

    @GetMapping("/getproject")
    public ResponseEntity<List<Project>> getProject() throws IOException, MethodArgumentNotValidException {
        
        List<Project> project = projectService.findAll();
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

}
