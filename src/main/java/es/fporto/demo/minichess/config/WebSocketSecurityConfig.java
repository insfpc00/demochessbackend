package es.fporto.demo.minichess.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated() 
                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
                .simpSubscribeDestMatchers("/user/**", "/topic/**").authenticated()
                .simpDestMatchers("/app/**").authenticated() 
                .anyMessage().denyAll(); 

    }
    
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
