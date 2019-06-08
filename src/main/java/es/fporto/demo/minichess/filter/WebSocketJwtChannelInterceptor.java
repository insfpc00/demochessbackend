package es.fporto.demo.minichess.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

@Component
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private TokenProvider jwtTokenUtil;

	@SuppressWarnings("unchecked")
	@Override
	public Message<?> preSend(Message<?> message, final MessageChannel channel) {

		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			Object nativeHeaders = accessor.getHeader("nativeHeaders");
			if (nativeHeaders != null && nativeHeaders instanceof LinkedMultiValueMap) {

				LinkedMultiValueMap<String, String> headers = (LinkedMultiValueMap<String, String>) nativeHeaders;
				List<String> headerUsername = headers.get("login");
				List<String> headerJwt = headers.get("jwt");

				if (headerUsername != null && headerJwt != null && !headerUsername.isEmpty() && !headerJwt.isEmpty()) {

					String username = headerUsername.get(0);
					String authToken = headerJwt.get(0);

					if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
						UserDetails userDetails = userDetailsService.loadUserByUsername(username);
						if (jwtTokenUtil.validateToken(authToken, userDetails)) {
							UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(
									authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);

							accessor.setUser(authentication);

						}
					}
				}

			}
		}
		return message;
	}

}
