package com.member.memberservice.Controller;

import com.member.memberservice.Model.Member;
import com.member.memberservice.Service.MemberService;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {


    private final MemberService memberService;
    private RestClient restClient;

    private final String keycloakUrl = "http://localhost:8080/realms/SpringSecurityKeycloak/protocol/openid-connect/token";
    private final String clientId = "my-app-client";
    private final String clientSecret = "n6wF5HcPkyzeWL2If8ADNa1hxK3TD6M1";
    private final String keycloakAdminUrl = "http://localhost:8080/admin/realms/SpringSecurityKeycloak/users";
    private final String adminClientId = "admin-cli";
    private final String adminUsername = "admin";
    private final String adminPassword = "admin";

    private MemberController memberController;

    public AuthController(RestClient restClient, MemberController memberController, MemberService memberService) {
        this.memberController = memberController;
        this.restClient = restClient;
        this.memberService = memberService;
    }

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<Map> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        String keycloakBody = String.format(
                "client_id=%s&client_secret=%s&username=%s&password=%s&grant_type=password",
                clientId, clientSecret, email, password
        );
        try {
            Map response = restClient.post()
                    .uri(keycloakUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(keycloakBody)
                    .retrieve()
                    .body(Map.class);
            Member user = memberService.findMemberByEmail(email);
            return ResponseEntity.ok(Map.of("access_token", response.get("access_token"), "userid", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    @CrossOrigin
    public ResponseEntity<Map> register(@RequestBody Map<String, String> userDetails) {
        try {
            String tokenBody = String.format(
                    "client_id=%s&username=%s&password=%s&grant_type=password",
                    adminClientId, adminUsername, adminPassword
            );
            Map response = restClient.post()
                    .uri("http://localhost:8080/realms/master/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(tokenBody)
                    .retrieve()
                    .body(Map.class);
            Map<String, Object> userPayload = Map.of(
                    "username", userDetails.get("username"),
                    "email", userDetails.get("email"),
                    "firstName", userDetails.get("firstname"),
                    "lastName", userDetails.get("lastname"),
                    "enabled", true,
                    "credentials", new Object[]{Map.of(
                            "type", "password",
                            "value", userDetails.get("password"),
                            "temporary", false
                    )}
            );
            String adminToken = response.get("access_token").toString();
            restClient.post()
                    .uri(keycloakAdminUrl)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userPayload)
                    .retrieve()
                    .toBodilessEntity();

            Member newMember = new Member();
            newMember.setEmail(userDetails.get("email"));
            newMember.setPassword(userDetails.get("password"));
            newMember.setUsername(userDetails.get("username"));
            newMember.setFirstName(userDetails.get("firstname"));
            newMember.setLastName(userDetails.get("lastname"));
            memberController.createMemberVoid(newMember);
            return ResponseEntity.ok(Map.of("message", "Utilisateur enregistré avec succès"));
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement : " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de l'enregistrement"));
        }
    }
}
