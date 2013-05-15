package org.opennms.netmgt.storage.sample.cassandra.internal;

import static com.datastax.driver.core.querybuilder.QueryBuilder.batch;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.SampleRepository;
import org.opennms.netmgt.api.sample.SampleSet;
import org.opennms.netmgt.api.sample.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.Select;

// TODO: Sharding by time period; Rows cannot grow unbounded
// TODO: Applying consistency levels

public class CassandraSampleRepository extends CassandraStorage implements SampleRepository {
	private static final Logger	LOG	= LoggerFactory.getLogger(CassandraSampleRepository.class);

	public CassandraSampleRepository(String cassandraHost, int cassandraPort, String cassandraKeyspace) {
		super(cassandraHost, cassandraPort, cassandraKeyspace);
	}

	/** {@inheritDoc} */
	@Override
	public Results find(Timestamp start, Timestamp end, Resource resource, Metric... metrics) {
		LOG.debug(
				"Finding samples of {} metrics between {} and {} for resource {}",
				metrics.length,
				(start != null) ? start.asDate() : "-INF",
				(end != null) ? end.asDate() : "+INF",
				resource);

		// resource is a required argument
		if (resource == null) throw new IllegalArgumentException("null resource argument");

		Results results = new Results(resource, metrics);
		resource.getAttributes().putAll(getResourceAttributes(resource));

		Select cqlQuery = select(F_COLLECTED_AT, F_METRIC, F_VALUE).from(T_SAMPLES);
		cqlQuery.where(eq(F_RESOURCE, resource.getIdentifier()));

		if (start != null) cqlQuery.where(gte(F_COLLECTED_AT, start.asDate()));
		if (end != null)   cqlQuery.where(lte(F_COLLECTED_AT, end.asDate()));

		for (com.datastax.driver.core.Row row : executeQuery(cqlQuery)) {
			Date collectedAt = row.getDate(F_COLLECTED_AT);
			String metricName = row.getString(F_METRIC);
			double metricValue = row.getDouble(F_VALUE);    // value can be null, but this driver will return 0.0d

			for (Metric metric : metrics) {
				if (metricName.equals(metric.getName())) {
					Timestamp t = new Timestamp(collectedAt.getTime(), TimeUnit.MILLISECONDS);
					Sample m = new Sample(resource, metric, t, metricValue);
					results.addSample(m);
				}
			}
		}

		return results;
	}

	/** {@inheritDoc} */
	@Override
	public void save(SampleSet measurements) {
		if (measurements.getSamples().size() < 1) {
			LOG.debug("a save was invoked with an empty sample set; aborting save");
			return;
		}

		Batch batchInsert = batch();

		for (Sample measurement : measurements.getSamples()) {
			// Samples
			batchInsert.add(
					insertInto(T_SAMPLES)
							.value(F_RESOURCE, measurement.getResource().getIdentifier())
							.value(F_COLLECTED_AT, measurement.getTimestamp().asMillis())
							.value(F_METRIC, measurement.getMetric().getName())
							.value(F_VALUE, measurement.getValue())
			);

			// One-to-many: resource -> metrics
			batchInsert.add(
					insertInto(T_METRICS_IDX)
							.value(F_RESOURCE, measurement.getResource().getIdentifier())
							.value(F_GROUP, measurement.getMetric().getGroup())
							.value(F_METRIC, measurement.getMetric().getName())
			);

			// One-to-many: endpoint -> resources
			batchInsert.add(
					insertInto(T_RESOURCES_IDX)
							.value(F_ENDPOINT, measurement.getResource().getAgent().getName())
							.value(F_RESOURCE, measurement.getResource().getIdentifier())
			);

			// Resource attributes
			for (Map.Entry<String, String> kv : measurement.getResource().getAttributes().entrySet()) {
				batchInsert.add(
						insertInto(T_RESOURCES)
								.value(F_RESOURCE, measurement.getResource().getIdentifier())
								.value(F_ATTRIBUTE, kv.getKey())
								.value(F_VALUE, kv.getValue())
				);
			}
		}

		executeQuery(batchInsert);
	}

	/* Retrieve resource attributes from the database */
	private Map<String, String> getResourceAttributes(Resource resource) {
		Map<String, String> attrs = new HashMap<String, String>();
		Statement query = select(F_ATTRIBUTE, F_VALUE).from(T_RESOURCES).where(eq(F_RESOURCE, resource.getIdentifier()));

		for (com.datastax.driver.core.Row row : executeQuery(query)) {
			attrs.put(row.getString(F_ATTRIBUTE), row.getString(F_VALUE));
		}

		return attrs;
	}
}
