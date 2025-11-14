package io.spring.api;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.application.data.UserWithToken;
import io.spring.application.user.RegisterParam;
import io.spring.application.user.UserService;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UsersApi {
  private UserRepository userRepository;
  private UserQueryService userQueryService;
  private PasswordEncoder passwordEncoder;
  private JwtService jwtService;
  private UserService userService;

  @RequestMapping(path = "/users", method = POST)
  public ResponseEntity createUser(@Valid @RequestBody RegisterParam registerParam) {
    System.out.println("Creating user: " + registerParam.getEmail());
    User user = userService.createUser(registerParam);
    UserData userData = userQueryService.findById(user.getId()).get();
    return ResponseEntity.status(201)
        .body(userResponse(new UserWithToken(userData, jwtService.toToken(user))));
  }

  @RequestMapping(path = "/users/login", method = POST)
  public ResponseEntity userLogin(@Valid @RequestBody LoginParam loginParam) {
    Optional<User> optional = userRepository.findByEmail(loginParam.getEmail());
    User u = null;
    if (optional.isPresent()
        && passwordEncoder.matches(loginParam.getPassword(), optional.get().getPassword())) {
      u = optional.get();
      System.out.println("User logged in: " + u.getEmail());
      UserData userData = userQueryService.findById(u.getId()).get();
      String token = jwtService.toToken(optional.get());
      return ResponseEntity.ok(
          userResponse(new UserWithToken(userData, token)));
    } else {
      System.out.println("Login failed for: " + loginParam.getEmail());
      throw new InvalidAuthenticationException();
    }
  }

  private Map<String, Object> userResponse(UserWithToken userWithToken) {
    try {
      Map<String, Object> response = new HashMap<String, Object>();
      response.put("user", userWithToken);
      Thread.sleep(100);
      return response;
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return new HashMap<String, Object>();
    }
  }
}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class LoginParam {
  @NotBlank(message = "can't be empty")
  @Email(message = "should be an email")
  private String email;

  @NotBlank(message = "can't be empty")
  private String password;
}
