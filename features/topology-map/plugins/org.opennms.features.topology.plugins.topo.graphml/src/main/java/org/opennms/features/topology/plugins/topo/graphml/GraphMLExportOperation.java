/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.plugins.topo.graphml;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.opennms.features.graphml.model.GraphML;
import org.opennms.features.graphml.model.GraphMLEdge;
import org.opennms.features.graphml.model.GraphMLElement;
import org.opennms.features.graphml.model.GraphMLGraph;
import org.opennms.features.graphml.model.GraphMLNode;
import org.opennms.features.graphml.model.GraphMLWriter;
import org.opennms.features.topology.api.Operation;
import org.opennms.features.topology.api.OperationContext;
import org.opennms.features.topology.api.RawCriteria;
import org.opennms.features.topology.api.topo.Edge;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.api.topo.MetaTopologyProvider;
import org.opennms.features.topology.api.topo.Vertex;
import org.opennms.features.topology.api.topo.VertexRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Strings;

public class GraphMLExportOperation implements Operation {

    private static Logger LOG = LoggerFactory.getLogger(GraphMLExportOperation.class);

    @Override
    public void execute(List<VertexRef> targets, OperationContext operationContext) {
        MetaTopologyProvider metaTopologyProvider = operationContext.getGraphContainer().getMetaTopologyProvider();
        try {
            GraphML graphML = new GraphMLConverter().convert(metaTopologyProvider, null);

            // XML
            GraphMLWriter.write(graphML, Paths.get(System.getProperty("user.home"), "Desktop", "the-graph.xml").toFile());

            // JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get(System.getProperty("user.home"), "Desktop", "the-graph.json").toFile(), graphML);

//            JAXBContext jc = JAXBContext.newInstance(GraphML.class);
//            Marshaller marshaller = jc.createMarshaller();
//            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
//            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.marshal(graphML, new File("/Users/mvrueden/Desktop/the-graph.json"));

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean display(List<VertexRef> targets, OperationContext operationContext) {
        return true;
    }

    @Override
    public boolean enabled(List<VertexRef> targets, OperationContext operationContext) {
        return true;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    private static class GraphMLConverter {

        public GraphML convert(MetaTopologyProvider metaTopologyProvider, String label) throws IllegalAccessException {
            GraphML graphML = new GraphML();
            if (!Strings.isNullOrEmpty(label)) {
                graphML.setProperty(GraphMLProperties.LABEL, label);
            }
            int id = 1;
            for (GraphProvider eachGraphProvider : metaTopologyProvider.getGraphProviders()) {
                GraphMLGraph graph = new GraphMLGraph();
                graph.setId(Integer.toString(id));
                if (!Strings.isNullOrEmpty(eachGraphProvider.getTopologyProviderInfo().getName())) {
                    graph.setProperty(GraphMLProperties.LABEL, eachGraphProvider.getTopologyProviderInfo().getName());
                }
                if (!Strings.isNullOrEmpty(eachGraphProvider.getTopologyProviderInfo().getDescription())) {
                    graph.setProperty(GraphMLProperties.DESCRIPTION, eachGraphProvider.getTopologyProviderInfo().getDescription());
                }
                graph.setProperty(GraphMLProperties.SEMANTIC_ZOOM_LEVEL, eachGraphProvider.getDefaults().getSemanticZoomLevel());
                graph.setProperty(GraphMLProperties.PREFERRED_LAYOUT, eachGraphProvider.getDefaults().getPreferredLayout());
                graph.setProperty(GraphMLProperties.NAMESPACE, eachGraphProvider.getVertexNamespace());

                RawCriteria criteria = new RawCriteria();
                int vertexTotalCount = eachGraphProvider.getVertexTotalCount();
                List<Vertex> vertices = eachGraphProvider.getVertices(criteria);
                List<Edge> edges = eachGraphProvider.getEdges(criteria);
                if (vertices.size() != vertexTotalCount) {
                    throw new IllegalStateException("Something went wrong. Vertex count does not match");
                }
                for (Vertex eachVertex : vertices) {
                    GraphMLNode graphMLNode = new GraphMLNode();
                    graphMLNode.setId(eachVertex.getId());
                    applyProperties(graphMLNode, eachVertex);
                    graph.addNode(graphMLNode);
                }
                for (Edge eachEdge : edges) {
                    GraphMLEdge graphMLEdge = new GraphMLEdge();
                    graphMLEdge.setSource(graph.getNodeById(eachEdge.getSource().getVertex().getId()));
                    graphMLEdge.setTarget(graph.getNodeById(eachEdge.getTarget().getVertex().getId()));
                    graphMLEdge.setId(eachEdge.getId());
                    applyProperties(graphMLEdge, eachEdge);
                    graph.addEdge(graphMLEdge);
                }
                id++;
                graphML.addGraph(graph);
            }
            return graphML;
        }
    }

    private static void applyProperties(GraphMLElement element, Object object) throws IllegalAccessException {
        ReflectionUtils.doWithFields(object.getClass(), field -> {
            field.setAccessible(true);
            if (field.getName().equals("id") || field.getName().equals("m_id")) {
                return; // we ignore id fields
            }
            if (field.getName().equals("properties") && field.getType() == Map.class) {
                Map<String, Object> properties = (Map<String, Object>) field.get(object);
                for (Map.Entry<String, Object> eachEntry : properties.entrySet()) {
                    element.setProperty(eachEntry.getKey(), eachEntry.getValue());
                }
            } else {
                Object value = field.get(object);
                if (value != null) {
                    String name = field.getName();
                    if (name.startsWith("m_")) {
                        name = name.replaceFirst("m_", "");
                    }
                    element.setProperty(name, value);
                }
            }
        });
    }
}
