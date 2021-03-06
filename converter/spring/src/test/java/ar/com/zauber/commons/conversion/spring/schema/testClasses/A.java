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
package ar.com.zauber.commons.conversion.spring.schema.testClasses;

/**
 * 
 * Source class of the conversion.
 * 
 * 
 * @author Juan Edi
 * @since Nov 18, 2009
 */
public class A {
    private final Integer integerField;
    
    /**
     * Creates the A.
     *
     * @param integerField
     */
    public A(final Integer integerField) {
        super();
        this.integerField = integerField;
    }

    /**
     * Returns the integerField.
     * 
     * @return <code>Integer</code> with the integerField.
     */
    public final Integer getIntegerField() {
        return integerField;
    }
    
    
}