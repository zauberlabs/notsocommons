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
package ar.com.zauber.commons.conversion.util;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.conversion.ConversionContext;
import ar.com.zauber.commons.conversion.Converter;


/**
 * Converter used in cases in which the target field is supposed to be the same
 * as the source field. <code>convert</code> method returns the source object.
 * 
 * @param <S> Type of the element to which the converter is applied..
 * 
 * @author Juan Edi
 * @since Nov 3, 2009
 */
public class IdentityConverter<S> implements Converter<S, S> {

    /** @see Converter#convert(Object, ConversionContext) */
    public final S convert(final S source, final ConversionContext ctx) {
        Validate.notNull(source);
        return source;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public final String toString() {
        return "identityConverter";
    }
}
