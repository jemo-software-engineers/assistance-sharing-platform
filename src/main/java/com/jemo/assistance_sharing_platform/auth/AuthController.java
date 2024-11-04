package com.jemo.assistance_sharing_platform.auth;

import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserRepository;
import com.jemo.assistance_sharing_platform.user.UserRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;



    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if(userRepository.findByUsername(registerRequest.username()) != null) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if(userRepository.findByEmail(registerRequest.email()) != null) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User createUser = User.builder()
                .role(UserRole.CUSTOMER)
                .email(registerRequest.email().toLowerCase())
                .username(registerRequest.username().toLowerCase())
                .password(passwordEncoder.encode(registerRequest.password()))
                .name(registerRequest.name().toLowerCase())
                .phone(registerRequest.phone())
                .address(registerRequest.address())
                .isAvailable(true)
                .pointScore(0L)
                .build();

        User newUser = userRepository.save(createUser);
        if(newUser.getId() != null) {
            return new ResponseEntity<>("User created successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User could not be created", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username().toLowerCase(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateToken(loginRequest.username());

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

}

