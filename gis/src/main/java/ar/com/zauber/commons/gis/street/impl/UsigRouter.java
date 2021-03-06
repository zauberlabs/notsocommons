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
package ar.com.zauber.commons.gis.street.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.dao.exception.NoSuchEntityException;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Encuentra recorridos usando la informacion de ruteo de los datos USIG
 * 
 * 
 * @author Juan F. Codagnone
 * @since Sep 22, 2007
 */
public class UsigRouter {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_NEDGE = 
        "select gid,source, target, distance(the_geom, GeomFromText(?,4326)) "
      + "from streets order by distance limit 1";
    private final WKTReader reader = new WKTReader();
    
    /** constructor */
    public UsigRouter(final JdbcTemplate jdbcTemplate) {
        Validate.notNull(jdbcTemplate);
        
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 
     * encuentra un recorrido entre las coordenadas from hasta to, dejandolo
     * en la clasura ret
     */
    public final void route(final Point from, final Point to, 
            final Closure<LineString> ret) {
        Validate.notNull(from);
        Validate.notNull(to);
        Validate.notNull(ret);
        
        int vertexFrom = sourceOrTarget(from, findNearestEdge(from));
        int vertexTo = sourceOrTarget(to, findNearestEdge(to));
        
        final String s = "SELECT AsText(LineMerge(Collect(the_geom))) FROM streets "
              + "WHERE gid in (SELECT edge_id  FROM shortest_path('SELECT gid "
              + "as id, source, target, cost   FROM streets', ?,  ?, false, false))";
        jdbcTemplate.query(s, 
                new Object[]{vertexFrom, vertexTo}, 
                new ResultSetExtractor() {
            public Object extractData(final ResultSet rset) throws SQLException,
                    DataAccessException {
                while(rset.next()) {
                    try {
                        final String s = rset.getString(1);
                        if(s != null) {
                            ret.execute((LineString) reader.read(s));
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    
                }
        
                return null;
            }
        });
    }
    
    /** dada una cordena, encuentra el eje del grafo de ruteo m�s cercano */
    private RoutingEdge findNearestEdge(final Point geom) {
        final List<RoutingEdge> ret = new ArrayList<RoutingEdge>();
        
        jdbcTemplate.query(SQL_NEDGE, new Object[]{geom.toString()},
                new ResultSetExtractor() {
                    public Object extractData(final ResultSet rs)
                            throws SQLException, DataAccessException {
                        
                        while(rs.next()) {
                            final RoutingEdge edge = new RoutingEdge();
                            edge.gid = rs.getLong("gid");
                            edge.source = rs.getLong("source");
                            edge.target = rs.getLong("target");
                            edge.distance = rs.getLong("distance");
                            
                            ret.add(edge);
                        }
                        return null;
                    }
            }
        );
        
        if(ret.size() == 0) {
            throw new NoSuchEntityException(geom);
        }
        if(ret.size() > 1) {
            throw new IllegalStateException("bla");
        }
        
        return ret.get(0);
    }
    
    /** determina si para un eje del grafo el punto est� m�s cerca
     * de ser target o source */
    private int sourceOrTarget(final Point geom, 
            final RoutingEdge edge) {
        String s = "select distance(line_interpolate_point("
                 + "LineMerge(the_geom),0), GeomFromText(?, 4326)), "
                 + "distance(line_interpolate_point(LineMerge(the_geom),1), "
                 + "GeomFromText(?, 4326)),source, target from streets where gid=?";
        final List<Integer> ret = new ArrayList<Integer>();    
        jdbcTemplate.query(s, new Object[]{geom.toString(), geom.toString(), 
                edge.gid},
                new ResultSetExtractor() {
                    public Object extractData(final ResultSet rs)
                            throws SQLException, DataAccessException {
                        
                        while(rs.next()) {
                            int r;
                            if(rs.getDouble(1) > rs.getDouble(2)) {
                                r = rs.getInt(4);
                            } else {
                                r = rs.getInt(3);
                            }
                            ret.add(r);
                        }
                        
                        return null;
                    }
            
        });
        
        if(ret.size() == 0) {
            throw new NoSuchEntityException(geom);
        }
        if(ret.size() > 1) {
            throw new IllegalStateException("bla");
        }
        
        
        return ret.get(0);
    }
}

//CHECKSTYLE:ALL:OFF
class RoutingEdge {
    long gid;
    long source;
    long target;
    double distance;
}
// CHECKSTYLE:ALL:ON