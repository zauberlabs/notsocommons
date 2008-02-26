/*
 * Copyright (c) 2007 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.commons.facebook.utils.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.facebook.utils.CallbackRequestValidator;

import com.facebook.api.FacebookSignatureUtil;

/**
 * Facebook en los requests manda una serie de parametros. Uno de esos
 * parametros es un secreto que solo la aplicaci�n y facebook deber�an conocer.
 * Todos los parametros que manda (prefijados en el espacio de nombres fb_sig_* )
 * tienen un chequeo md5.
 *  
 * http://wiki.developers.facebook.com/index.php/Your_callback_page_and_you
 *
 *
 * @author Juan F. Codagnone
 * @since Dec 24, 2007
 */
public class DefaultCallbackRequestValidator implements CallbackRequestValidator {
    private final List<String> secrets;
    
    /**
     * Creates the DefaultCallbackRequestValidator.
     *
     * @param secrets API secrets
     */
    public DefaultCallbackRequestValidator(final List<String> secrets) {
        Validate.noNullElements(secrets);
        
        this.secrets = secrets;
        
    }
    
    /** @see CallbackRequestValidator#validate(HttpServletRequest) */
    public final boolean validate(final HttpServletRequest request) {
        boolean valid = false;
        
        for(final String secret : secrets) {
            valid |= FacebookSignatureUtil.verifySignature(
                    extractFacebookParams(request), secret);
        }
        return valid;
    }

    /**
     * @param request
     * @return
     */
    public static Map<String, CharSequence> extractFacebookParams(
            final HttpServletRequest request) {
        final Map<String, CharSequence> params = new HashMap<String, CharSequence>();
        final Map tmp = FacebookSignatureUtil.extractFacebookNamespaceParams(
                request.getParameterMap()
                );
        Set<Entry<String, Object>> s = tmp.entrySet();
        // el jetty pasa los parametros como arreglos. y facebook no se la banca
        for(final Entry<String, Object> entry : s) {
            if(entry.getValue().getClass().isArray()) {
                params.put(entry.getKey(), ((String[]) entry.getValue())[0]);
            } else {
                params.put(entry.getKey(), entry.getKey());
            }
        }
        return params;
    }

}
