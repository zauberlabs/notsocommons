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
package ar.com.zauber.commons.moderation;

import java.util.Date;

import ar.com.zauber.commons.repository.Reference;

/**
 * Crea {@link ModerationEntry}s.
 * 
 * @author Pablo Grigolatto
 * @since Oct 9, 2009
 */
public interface ModerationEntryFactory {

    /** @see ModerationEntry */
    ModerationEntry create(
            Date moderatedAt,
            String moderatedBy,
            ModerationState initialState,
            ModerationState finalState,
            Reference<Moderateable> entityReference);

    /** @return la clase concreta que crea */
    Class getClazz();
    
}
