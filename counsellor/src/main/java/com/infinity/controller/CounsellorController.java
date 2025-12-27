package com.infinity.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infinity.repository.CounsellorRepository;
import com.infinity.service.CounsellorService;
import com.infinity.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/auth")
public class CounsellorController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CounsellorRepository counsellorRepo;


    @Autowired
    private CounsellorService counsellorService;

    @PostMapping("/login")
    public Map<String,Object> loginCounsellor(@RequestBody Map<String,Object> payload) {
          Map<String, Object> response = new HashMap<>();
        String email = payload.get("email").toString();
        String password = payload.get("password").toString();

         try {
            // 1️⃣ Authenticate email & password
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            // 2️⃣ Get counsellor details
            Map<String, Object> counsellor =
                    counsellorRepo.findUserByEmail(email);
                    if(counsellor == null && counsellor.isEmpty()) {
                        throw new Exception("Invalid email or password");

                    }
                           

           int counsellorId = ((Number) counsellor.get("id")).intValue();

            // 3️⃣ Create UserDetails
            UserDetails userDetails =
                    new org.springframework.security.core.userdetails.User(
                            email,
                            counsellor.get("password").toString(),
                            new java.util.ArrayList<>()
                    );

            // 4️⃣ Generate JWT token
            String token = jwtUtil.generateToken(userDetails, counsellorId);

            // 5️⃣ Response
            response.put("status", "SUCCESS");
            response.put("token", token);
           // response.put("counsellorId", counsellorId);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Invalid email or password");
        }

        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerCounsellor(@RequestBody Map<String,Object> payload) {
       Map<String, Object> result = counsellorService.registerCounsellor(payload);
       return ResponseEntity.ok(result);
    }

    @PostMapping("/addEnquiry")
    public ResponseEntity<Object> addEnquiry(HttpServletRequest request, @RequestBody Map<String,Object> payload) {
        Map<String, Object> result = counsellorService.addEnquiry(request, payload);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getDropdownsData")
    public ResponseEntity<Object> getDropdownsData() {
        Map<String, Object> result = counsellorService.getDropdownsData();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getDashBoardData")
    public ResponseEntity<Object> getDashBoardData(HttpServletRequest request) {
        Map<String, Object> result = counsellorService.getDashBoardData(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/getEnquiries")
    public ResponseEntity<Object> getEnquiries(HttpServletRequest request, @RequestBody Map<String,Object> filters) {
        Map<String, Object> result = counsellorService.getEnquiries(request, filters);
        return ResponseEntity.ok(result);
    }
}
