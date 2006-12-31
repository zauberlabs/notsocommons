/*
 * Copyright (c) 2006 Zauber  -- All rights reserved
 */
package ar.com.zauber.commons.syndication.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.zauber.commons.syndication.FeedReader;
import ar.com.zauber.commons.syndication.SyndicationManager;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;


/**
 * Implementaci�n de {@link SyndicationManager} que periodicamente
 * se fija en una archivo atom las noticias de otro sitio (blog?).  
 * 
 * @author Juan F. Codagnone
 * @since Jul 2, 2006
 */
public class PeriodicRomeSyndicationManager implements SyndicationManager {
    /** logger */
    private static final Log LOGGER = LogFactory
            .getLog(PeriodicRomeSyndicationManager.class);
    /** se ocupa de periodicamente actualizar el feed */
    private final ScheduledThreadPoolExecutor executor = 
        new ScheduledThreadPoolExecutor(1);
    
    /** parsea el feed */
    private final SyndFeedInput input = new SyndFeedInput();
    /** lista de entradas actuales. cada tanto se recarga */
    private List<SyndEntry> entries = Collections
            .unmodifiableList(new ArrayList<SyndEntry>());
    /** obtiene el feed */
    private FeedReader feedReader;
    
    /**
     * Creates the PeriodicRomeSyndicationManager.
     *
     * @param feedReader fuente del feed
     * @param periodInSeconds cantidad de segundos cada cuanto 
     *                        se intenta actualizar
     */
    public PeriodicRomeSyndicationManager(final long periodInSeconds, 
            final FeedReader reader) {
        Validate.isTrue(periodInSeconds > 0);
        Validate.notNull(reader);
        
        this.feedReader = reader;
        
        loadEntries();
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                loadEntries();
            }
        }, periodInSeconds, periodInSeconds, TimeUnit.SECONDS);
    }
    
    /** @see SyndicationManager#getEntries() */
    public final List<SyndEntry> getEntries() {
        List<SyndEntry> ret = null;
        
        synchronized(this) {
            ret = entries;
        }
        
        return ret;
    }

    private void loadEntries() {
        try {
            LOGGER.info("actualizando feed");
            final Reader reader = feedReader.getReader();
            Validate.notNull(reader, "feed reader no debe retornar null");
            final SyndFeed feed = input.build(reader);
            final List<SyndEntry> feedEntries = feed.getEntries();
            
            synchronized(this) {
                entries = Collections.unmodifiableList(feedEntries);
            }
        } catch(Exception e) {
            LOGGER.error("obteniendo feed", e);
        }
    }
}