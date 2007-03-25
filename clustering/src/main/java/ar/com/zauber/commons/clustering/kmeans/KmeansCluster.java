/*
 * Copyright (c) 2006, 2007 Zauber  -- All rights reserved
 */
package ar.com.zauber.commons.clustering.kmeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ar.com.zauber.commons.clustering.Clusterable;


public class KmeansCluster<T extends Clusterable> implements Iterable<T> {

    private Clusterable centroid;
    private List<T> elements;

    public Iterator<T> iterator() {
        return elements.iterator();
    }

    /**
     * Crea el/la Cluster.
     *
     * @param elements
     */
    public KmeansCluster(T firstElement) {
        super();
        this.elements = new ArrayList<T>();
        this.elements.add(firstElement);
        recalculateCentroid();
    }
    
    /**
     * Crea el/la Cluster.
     *
     */
    private KmeansCluster() {
        throw new UnsupportedOperationException();
    }

    public double calculateObjectiveFunctionResult(Map<Clusterable, Map<T, Double>> distanceCache) {
        double result = 0;
        
        if(this.elements.size()==0) {
            return result;
        }
        
        Double distance = null;
        
        Map<T, Double> distanceMap = distanceCache.get(centroid);
        
        for(T element : elements) {
       		distance = distanceMap.get(element);
        	if(distance == null) {
        		distance = centroid.distance(element).doubleValue();
        		distanceMap.put(element, distance);
        	}
            result = result + distance;
            distance = null;
        }
        
        return result;
    } 
    
    public void addElement(T clusterable) {
        this.elements.add(clusterable);
    }

    public void removeElement(T clusterable) {
    	this.elements.remove(clusterable);
    }

	public void recalculateCentroid() {
		Clusterable newCentroid = null;
		for(T element : elements) {
			newCentroid = element.addToCentroid(newCentroid, elements.size());
        }
		if(newCentroid != null) {
			this.centroid = newCentroid;
		}
	}

	public Clusterable getCentroid() {
		return centroid;
	}

	public void removeElements() {
		elements.clear();	
	}

}