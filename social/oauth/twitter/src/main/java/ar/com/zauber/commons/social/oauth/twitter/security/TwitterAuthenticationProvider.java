/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.commons.social.oauth.twitter.security;

import org.apache.commons.lang.Validate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import ar.com.zauber.commons.social.oauth.OAuthAccessException;
import ar.com.zauber.commons.social.oauth.OAuthAccessManager;
import ar.com.zauber.commons.social.oauth.OAuthAccessToken;

/**
 * {@link AuthenticationProvider} para OAuth de Twitter
 * 
 * @author Mariano Cortesi
 * @since Feb 3, 2010
 */
public class TwitterAuthenticationProvider implements AuthenticationProvider {

    private final TwitterUserDetailsService userDetailsService;
    private final OAuthAccessManager twitterAccessManager;

    /**
     * Creates the TwitterAuthenticationProvider.
     */
    public TwitterAuthenticationProvider(
            final TwitterUserDetailsService userDetailsService,
            final OAuthAccessManager twitterAccessManager) {
        Validate.notNull(userDetailsService);
        Validate.notNull(twitterAccessManager);
        this.userDetailsService = userDetailsService;
        this.twitterAccessManager = twitterAccessManager;
    }

    /** @see AuthenticationProvider#authenticate(Authentication) */
    public final Authentication authenticate(final Authentication authentication)
            throws AuthenticationException {
        Validate.notNull(authentication);
        TwitterAuthenticationToken authenticationToken = 
            (TwitterAuthenticationToken) authentication;

        try {
            Object accessToken = authenticationToken.getCredentials();

            if (accessToken == null) {
                final String oAuthToken = authenticationToken.getOAuthToken();
                final String oAuthVerifier = authenticationToken.getOAuthVerifier();
                accessToken = twitterAccessManager.getAccessToken(oAuthToken,
                        oAuthVerifier);
            }

            UserDetails userDetails = this.userDetailsService
                    .loadUserByTwitterAccessToken((OAuthAccessToken) accessToken);
            return new TwitterAuthenticationToken(
                    (OAuthAccessToken) accessToken, userDetails, userDetails
                            .getAuthorities());
        } catch (OAuthAccessException e) {
            throw new AuthenticationServiceException("twitter error", e);
        }
    }

    /** @see AuthenticationProvider#supports(java.lang.Class) */
    public final boolean supports(final Class<? extends Object> authentication) {
        return (TwitterAuthenticationToken.class
                .isAssignableFrom(authentication));
    }

}
