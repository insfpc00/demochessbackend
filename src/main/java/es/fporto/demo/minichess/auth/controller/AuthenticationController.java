package es.fporto.demo.minichess.auth.controller;

import java.util.Calendar;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.fporto.demo.minichess.filter.TokenProvider;
import es.fporto.demo.minichess.user.model.AuthToken;
import es.fporto.demo.minichess.user.model.LoginUser;
import es.fporto.demo.minichess.user.model.UserDto;
import es.fporto.demo.minichess.user.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/token")
public class AuthenticationController {

	private static final String GUEST_ROLE = "USER";

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenProvider jwtTokenUtil;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/generate-token", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody LoginUser loginUser) throws AuthenticationException {

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String token = jwtTokenUtil.generateToken(authentication);
		return ResponseEntity.ok(new AuthToken(token));
	}

	@RequestMapping(value = "/generate-guest-token", method = RequestMethod.POST)
	public ResponseEntity<?> registerAsGuest() throws AuthenticationException {
		try {
			String loginName = "guest-" + Calendar.getInstance().getTimeInMillis();

			UserDto user = new UserDto(loginName, UUID.randomUUID().toString(),"Guest","User");

			userService.save(user, GUEST_ROLE);

			final Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			final String token = jwtTokenUtil.generateToken(authentication);
			return ResponseEntity.ok(new Object[] { new AuthToken(token), loginName });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
