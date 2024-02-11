package com.Security.SignApp.Controller;
import com.Security.SignApp.Entity.UserEntity;
import com.Security.SignApp.Model.UserRequest;
import com.Security.SignApp.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    private ResponseEntity<String> test(){
        return new ResponseEntity<>("HEY THIS STUFF WORKS!!!", HttpStatus.OK);
    }


    @PostMapping("/register")
    private ResponseEntity<UserEntity> register(@RequestBody UserRequest userRequest, HttpServletRequest request){
        return new ResponseEntity<>(userService.registerUser(userRequest,request),HttpStatus.OK);
    }

    @GetMapping("/activate")
    private ResponseEntity<String> activate(@RequestParam(name = "token") String verificationToken){
        return new ResponseEntity<>(userService.activateUser(verificationToken),HttpStatus.OK);
    }
}
