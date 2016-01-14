/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.app.internal.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.opennms.features.topology.api.topo.Criteria;
import org.opennms.features.topology.api.topo.Edge;
import org.opennms.features.topology.api.topo.EdgeProvider;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.api.topo.Vertex;
import org.opennms.features.topology.api.topo.VertexProvider;
import org.opennms.features.topology.api.topo.WrappedEdge;
import org.opennms.features.topology.api.topo.WrappedGraph;
import org.opennms.features.topology.api.topo.WrappedLeafVertex;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class DefaultTopologyService implements TopologyService {

    private final Map<String, VertexProvider> vertexProviders = new HashMap<String, VertexProvider>();
    private final Map<String, EdgeProvider> edgeProviders = new HashMap<String, EdgeProvider>();
    private final Map<String, GraphProvider> graphProviders = new HashMap<>();

    @Override
    public WrappedGraph getGraph(String namespace) {
        List<Vertex> displayVertices = getVertices(namespace, null);
        List<Edge> displayEdges = getEdges(namespace, null);

        return new WrappedGraph(
                namespace,
                new ArrayList<>(Collections2.transform(displayVertices, new Function<Vertex, WrappedLeafVertex>() {
                    @Override
                    public WrappedLeafVertex apply(Vertex input) {
                        return new WrappedLeafVertex(input);
                    }
                })),
                new ArrayList<>(Collections2.transform(displayEdges, new Function<Edge, WrappedEdge>() {

                    @Override
                    public WrappedEdge apply(Edge input) {
                        return new WrappedEdge(input, new WrappedLeafVertex((Vertex) input.getSource()), new WrappedLeafVertex((Vertex) input.getTarget()));
                    }
                })));
    }

    @Override
    public Set<String> getNamespaces() {
        return graphProviders.keySet();
    }

    public void onGraphProviderBind(GraphProvider graphProvider, final Map<String,String> properties) {
        graphProviders.put(graphProvider.getVertexNamespace(), graphProvider);
    }

    public void onGraphProviderUnbind(GraphProvider graphProvider, final Map<String,String> properties) {
        graphProviders.remove(graphProvider);
    }

    public void onVertexProviderBind(VertexProvider vertexProvider, final Map<String,String> properties) {
        vertexProviders.put(vertexProvider.getVertexNamespace(), vertexProvider);
    }

    public void onVertexProviderUnbind(VertexProvider vertexProvider, final Map<String,String> properties) {
        vertexProviders.remove(vertexProvider.getVertexNamespace());
    }

    public void onEdgeProviderBind(EdgeProvider edgeProvider, final Map<String,String> properties) {
        edgeProviders.put(edgeProvider.getEdgeNamespace(), edgeProvider);
    }

    public void onEdgeProviderUnbind(EdgeProvider edgeProvider, final Map<String,String> properties) {
        edgeProviders.remove(edgeProvider.getEdgeNamespace());
    }

    public List<Vertex> getVertices(String namespace, Criteria... criteria) {
        final GraphProvider graphProvider = getGraphProvider(namespace);
        List<Vertex> vertices = new ArrayList<Vertex>(filteredVertices(graphProvider, criteria, true));

        for(VertexProvider vertexProvider : vertexProviders.values()) {
            if (vertexProvider.contributesTo(namespace)) {
                vertices.addAll(filteredVertices(vertexProvider, criteria, false));
            }
        }

        return vertices;
    }

    private GraphProvider getGraphProvider(String namespace) {
        return Objects.requireNonNull(graphProviders.get(namespace));
    }

    private Collection<? extends Vertex> filteredVertices(VertexProvider vertexProvider, Criteria[] criteria, boolean returnAllOnNullFilter) {
        if (criteria == null) {
            criteria = new Criteria[0];
        }
        return vertexProvider.getVertices(criteria);
    }

    public List<Edge> getEdges(String namespace, Criteria... criteria) {
        final GraphProvider graphProvider = getGraphProvider(namespace);
        List<Edge> edges = new ArrayList<Edge>(filteredEdges(graphProvider, criteria, true));

        for(EdgeProvider edgeProvider : edgeProviders.values()) {
            if (edgeProvider.contributesTo(namespace)) {
                edges.addAll(filteredEdges(edgeProvider, criteria, false));
            }
        }

        return edges;
    }

    private Collection<? extends Edge> filteredEdges(EdgeProvider edgeProvider, Criteria[] criteria, boolean returnAllOnNullFilter) {
        if (criteria == null) {
            criteria = new Criteria[0];
        }
        return edgeProvider.getEdges(criteria);
    }

}
