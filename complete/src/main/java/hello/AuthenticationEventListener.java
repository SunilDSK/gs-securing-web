package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) //More overhead but workaround for the singleton
public class AuthenticationEventListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationEventListener.class);

    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent authenticationEvent) {
        Authentication authentication = authenticationEvent.getAuthentication();

        // These would be duplicate events in the case of successful logins
        if ((authenticationEvent instanceof InteractiveAuthenticationSuccessEvent) ||
                (authenticationEvent instanceof SessionFixationProtectionEvent)) {
            logger.debug("Authentication event [{}]", authenticationEvent.getClass());
            return;
        }

        if (authentication.isAuthenticated()) {
            logger.info("[{}] logged in successfully", authentication.getName());
        } else {
            logger.warn("[{}] failed to log in with password [{}]", authentication.getName(),
                    authentication.getCredentials().toString().replaceAll(".", "*"));

            // Simulate a stacktrace
            if (authentication.getName().equals("admin")){
                throw new RuntimeException("There is no admin here...");
            }
        }
    }

}
