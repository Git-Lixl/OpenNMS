package org.opennms.netmgt.api.sample;


public interface SampleRepository {
	
	/**
	 * Returns measurements matching a set of metrics, for a given resource, and
	 * time range.
	 * 
	 * <p>
	 * Passing a null value for {@code start} or {@code end} serves to make that
	 * end of the temporal range unbounded. For example, a null start will
	 * return all matching measurements up to the end {@link Timestamp}. Passing
	 * both a null {@code start} and {@code end} will return all measurements
	 * for the matching {@code metrics} (so <i>be careful</i>).
	 * </p>
	 * 
	 * @param processor
	 * 	a processor that processes the data as it is read
	 * 
	 * @param start
	 *            the start time of this search.
	 * @param end
	 *            the end time of this search.
	 * @param resource
	 *            the resource to query.
	 * @param metrics
	 *            a sequence of metrics to filter the results against.
	 * @return a {@link Results} instance containing the results of the search.
	 */
	public Results find(SampleProcessorBuilder builder, Timestamp start, Timestamp end, Resource resource, Metric... metrics);
	
	public void save(SampleSet samples);
}
