/**
 * Copyright (c) 2005-2011 Zauber S.A. <http://www.zaubersoftware.com/>
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
package ar.com.zauber.commons.social.twitter.impl.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.BasicAuthorization;
import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.dao.Transformer;
import ar.com.zauber.commons.social.twitter.api.streaming.filter.BoundingBox;
import ar.com.zauber.commons.social.twitter.api.streaming.filter.StreamingFilter;
import ar.com.zauber.commons.validate.Validate;

/**
 * Models a connection to the Twitter Streaming API.
 * 
 * 
 * @author Francisco J. Gonz�lez Costanz�
 * @since Sep 20, 2010
 */
public class TweetFetcher<T> {

    private final Logger logger = LoggerFactory
            .getLogger(TweetFetcher.class);

    private final TwitterStream stream;
    private final Transformer<Status, T> transformer;
    private final Closure<T> closure;
    
    private StreamingFilter filter;
    private boolean streamStarted = false;

    /** Creates the TweetFetcher. */
    public TweetFetcher(final String user, final String password,
            final StreamingFilter filter,
            final Transformer<Status, T> transformer,
            final Closure<T> closure) {
        Validate.notBlank(user, password);
        Validate.notNull(filter, transformer, closure);
        this.stream = createStream(user, password);
        this.filter = filter;
        this.closure = closure;
        this.transformer = transformer;
    }

    /**
     * Creates the {@link TwitterStream}
     * 
     * @param user
     * @param password
     * @return
     */
    private TwitterStream createStream(final String user, final String password) {
        final TwitterStream stream = new TwitterStreamFactory().getInstance(new BasicAuthorization(user,
                password));

        stream.addListener(new StatusListener() {
            @Override
            public void onStatus(final Status status) {
                try {
                    T t = transformer.transform(status);
                    closure.execute(t);
                } catch (Throwable ex) {
                    logger.error("Exception en onStatus", ex);
                }

            }

            @Override
            public void onException(final Exception e) {
                logger.error("Exception on TwitterStream", e);
            }

            @Override
            public void onTrackLimitationNotice(
                    final int numberOfLimitedStatuses) {
                logger.warn("onTrackLimitationNotice: Number of limited "
                        + "statuses: {}", numberOfLimitedStatuses);
            }

            @Override
            public void onDeletionNotice(
                    final StatusDeletionNotice statusDeletionNotice) {
                logger.warn("statusDeletionNotice: {}", statusDeletionNotice);
            }
            
            @Override
            public void onScrubGeo(final long userId, final long upToStatusId) {
                logger.warn("scrubGeo: {} {}", userId, upToStatusId);
            }
        });
        
        stream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {
            @Override
            public void onDisconnect() {
                logger.warn("Disconnected from Twitter!");
            }
            
            @Override
            public void onConnect() {
            }
            
            @Override
            public void onCleanUp() {
            }
        });
        
        return stream;
    }

    /**
     * Opens the Twitter Stream.
     */
    public final void openStream() {
        if (!streamStarted) {
            logger.info("Opening Twitter Stream!");
            stream.filter(toFilterQuery(filter));
            streamStarted = true;
        } else {
            logger.warn("Stream is already opened!");
        }
    }

    /**
     * Transforms a {@link TwitterFilter} to a {@link FilterQuery}
     * 
     * @param filter2
     *            the {@link TwitterFilter}
     * @return the {@link FilterQuery}
     */
    private FilterQuery toFilterQuery(final StreamingFilter filter2) {
        final int count = filter2.getPreviousStatuses();
        final long[] follow = filter2.getUserIds();
        final String[] track = filter2.getKeywords();

        double[][] locations;
        final BoundingBox[] boxes = filter2.getBoxes();
        if (boxes != null) {
            locations = new double[boxes.length * 2][2];
            for (int i = 0; i < boxes.length; i += 2) {
                BoundingBox box = boxes[i];
                locations[i][0] = box.getSwLong();
                locations[i][1] = box.getSwLat();
                locations[i + 1][0] = box.getNeLong();
                locations[i + 1][1] = box.getNeLat();
            }
        } else {
            locations = new double[][] {};
        }

        return new FilterQuery(count, follow, track, locations);
    }

    /**
     * Closes the stream.
     */
    public final void closeStream() {
        if (streamStarted) {
            logger.info("Closing Twitter Stream.");
            stream.cleanUp();
            streamStarted = false;
        } else {
            logger.warn("Stream is already closed!");
        }
    }
    
    /**
     * Updates the stream filter. This implies closing and reopening the stream.
     * 
     * @param filter
     */
    public final void updateFilter(final StreamingFilter filter) {
        this.closeStream();
        this.filter = filter;
        this.openStream();
    }

}
