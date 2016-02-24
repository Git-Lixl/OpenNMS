package org.opennms.features.topology.plugins.topo.odl;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.opennms.core.criteria.Criteria;
import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.browsers.ContentType;
import org.opennms.features.topology.api.support.VertexHopGraphProvider.DefaultVertexHopCriteria;
import org.opennms.features.topology.api.topo.AbstractSearchProvider;
import org.opennms.features.topology.api.topo.DefaultVertexRef;
import org.opennms.features.topology.api.topo.SearchProvider;
import org.opennms.features.topology.api.topo.SearchQuery;
import org.opennms.features.topology.api.topo.SearchResult;
import org.opennms.features.topology.api.topo.SimpleLeafVertex;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.integrations.odl.NamingUtils;
import org.opennms.netmgt.dao.api.NodeDao;
import org.opennms.netmgt.model.OnmsNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class OpendaylightSearchProvider extends AbstractSearchProvider implements SearchProvider {
    private static final Logger LOG = LoggerFactory.getLogger(OpendaylightSearchProvider.class);

    public static final String TOPOLOGY_NAMESPACE = "odl";

    private NodeDao m_nodeDao;

    @Override
    public String getSearchProviderNamespace() {
        return TOPOLOGY_NAMESPACE;
    }

    @Override
    public boolean contributesTo(String namespace) {
        return Sets.newHashSet(ContentType.Alarm, ContentType.Node).contains(namespace);
    }

    @Override
    public List<SearchResult> query(SearchQuery searchQuery, GraphContainer graphContainer) {
        List<SearchResult> results = Lists.newArrayList();
        CriteriaBuilder bldr = new CriteriaBuilder(OnmsNode.class);
        bldr.orderBy("label", true);
        bldr.limit(10);
        Criteria dbQueryCriteria = bldr.toCriteria();
        
        for (OnmsNode node : m_nodeDao.findMatching(dbQueryCriteria)) {
            try {
                String nodeId = NamingUtils.getNodeIdFromForeignId(node.getForeignId());
                
                final SimpleLeafVertex vertex = new SimpleLeafVertex(TOPOLOGY_NAMESPACE, nodeId, 50, 50);
                vertex.setLabel(node.getLabel());
                vertex.setNodeID(node.getId());

                SearchResult searchResult = new SearchResult(vertex);
                searchResult.setCollapsed(false);
                searchResult.setCollapsible(true);
                results.add(searchResult);
            } catch (Throwable t) {
                continue;
            }
        }
        
        LOG.info("OpendaylightSearchProvider->query: found {} results: {}", results.size(), results);
        return results;
    }

    @Override
    public boolean supportsPrefix(String searchPrefix) {
        return supportsPrefix("odl=", searchPrefix);
    }

    @Override
    public Set<VertexRef> getVertexRefsBy(SearchResult searchResult, GraphContainer graphContainer) {
        VertexRef vertexToFocus = new DefaultVertexRef(searchResult.getNamespace(), searchResult.getId(), searchResult.getLabel());
        return Sets.newHashSet(vertexToFocus);
    }

    @Override
    public void addVertexHopCriteria(SearchResult searchResult, GraphContainer container) {
        DefaultVertexHopCriteria criterion = new DefaultVertexHopCriteria(new DefaultVertexRef(searchResult.getNamespace(), searchResult.getId(), searchResult.getLabel()));
        container.addCriteria(criterion);
    }

    @Override
    public void removeVertexHopCriteria(SearchResult searchResult, GraphContainer container) {
        DefaultVertexHopCriteria criterion = new DefaultVertexHopCriteria(new DefaultVertexRef(searchResult.getNamespace(), searchResult.getId(), searchResult.getLabel()));
        container.removeCriteria(criterion);
    }

    public void setNodeDao(NodeDao nodeDao) {
        m_nodeDao = Objects.requireNonNull(nodeDao);
    }
}
