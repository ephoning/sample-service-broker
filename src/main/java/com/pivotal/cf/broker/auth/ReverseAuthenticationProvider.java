package com.pivotal.cf.broker.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.List;

/**
 * a simple self-contained authentication provider that verifies credentials based on whether
 * the username and password are mirror images
 * Created by honine on 3/20/14.
 */
public class ReverseAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(ReverseAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        logger.info("ReverseAuthenticationProvider: authenticate: " + username + " / " + password);

        if((username.equals("admin") && password.equals("admin")) ||
            username.equals(reverse(password))) {
            List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            logger.info("ReverseAuthenticationProvider: authentication SUCCESS");
            return new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
        }
        else {
            logger.info("ReverseAuthenticationProvider: authentication FAILURE");
            throw new BadCredentialsException("Invalid username / password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    // reverse the passed-in string
    private String reverse(String s) {
        return new StringBuffer(s).reverse().toString();
    }
}
