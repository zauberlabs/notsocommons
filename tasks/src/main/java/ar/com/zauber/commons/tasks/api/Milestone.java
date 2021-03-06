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
package ar.com.zauber.commons.tasks.api;

import java.util.Date;

import ar.com.zauber.commons.validate.Validate;

/**
 * ValueObject representing a milestone of a {@link Task}
 * 
 * @author Mariano A Cortesi
 * @since Dec 14, 2010
 */
public class Milestone {

    private final Date timestamp;
    private final String milestoneName;

    /** Creates the Milestone. */
    public Milestone(final Date timestamp, final String milestoneName) {
        Validate.notNull(timestamp, milestoneName);
        this.timestamp = new Date(timestamp.getTime());
        this.milestoneName = milestoneName;
    }

    public final Date getTimestamp() {
        return timestamp;
    }

    public final String getMilestoneName() {
        return milestoneName;
    }

    @Override
    public final String toString() {
        return "Milestone[" + timestamp + ":" + milestoneName + "]";
    }

}
