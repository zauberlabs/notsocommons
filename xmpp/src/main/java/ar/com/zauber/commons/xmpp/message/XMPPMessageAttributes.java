/**
 * Copyright (c) 2005-2009 Zauber S.A. <http://www.zauber.com.ar/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ar.com.zauber.commons.xmpp.message;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Message.Type;

/**
 * Cosas configurables en un {@link XMPPMessage}.
 * 
 * 
 * @author Juan F. Codagnone
 * @since Jun 20, 2009
 */
public class XMPPMessageAttributes {
    /** message type @see Type */
    private Type messageType = Type.normal;
    
    public final Type getMessageType() {
        return messageType;
    }

    /** @see #messageType */
    public final void setMessageType(final Type messageType) {
        Validate.notNull(messageType);
        this.messageType = messageType;
    }

    /** 
     * Los mensajes opcionalmente soportan I18N.
     * Este mapa contiene el mensaje traducido para cada lenguaje.
     * Si hay algun body ac� cuyo lenguaje es "en", pisa al {@link #defaultContent}.
     */
    private Map<Locale, String> langBodies = new TreeMap<Locale, String>();
    
    public final Map<Locale, String> getLangBodies() {
        return langBodies;
    }
    
    /** 
     * @see #langBodies
     */
    public final void setLangBodies(final Map<Locale, String> langBodies) {
        Validate.notNull(langBodies);
        for(final Entry<Locale, String> entry : langBodies.entrySet()) {
            Validate.notNull(entry);
            Validate.notNull(entry.getKey());
            Validate.notNull(entry.getValue());
        }
        
        this.langBodies = langBodies;
    }
    
    /** 
     * conexion al servidor. es opcional. si est� seteada y se tienen
     * bodies html, se utiliza para verificar las capacidades del destinatario.
     */
    private XMPPConnection connection;
    
    public final void setConnection(final XMPPConnection connection) {
        this.connection = connection;
    }
    
    public final XMPPConnection getConnection() {
        return connection;
    }
    
    /**
     * Si el destinatario lo soporta, se pueden enviar mensajes con html.
     * Esta variable contiene dicho mensaje.
     * Se DEBE seguir 
     *    http://xmpp.org/extensions/xep-0071.html
     * Para que esto se env�e el destinatario tiene que soportar html y 
     * se debe tener seteado la variable {@link #connection}.  
     */
    private String htmlMessage;

    public final String getHtmlMessage() {
        return htmlMessage;
    }

    public final void setHtmlMessage(final String htmlMessage) {
        this.htmlMessage = htmlMessage;
    }
    
    /** 
     * Extensiones de XMPP (tradiosanalmente especificadas en jep) para agregar
     * al mensaje. Por ejemplo formularios. 
     */
    private List<PacketExtension> extensions = new LinkedList<PacketExtension>();
    
    public final List<PacketExtension> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }
    
    /** @see #extensions */
    public final void setExtensions(final List<PacketExtension> extensions) {
        Validate.noNullElements(extensions);
        this.extensions = extensions;
    }
}