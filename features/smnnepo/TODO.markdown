TODO
====

In order of importance:

 1. Enable collection of metric groups not associated with a resource type. -- *Matt*
 1. Define interfaces for:
    - Querying metrics by resource
    - Querying resources by endpoint
    - Query resource attributes
 1. Implement:
    - Querying metrics by resource (Jrrd)
    - Querying resources by endpoint (Jrrd)
    - Querying metrics by resource (Cassandra)
    - Querying resources by endpoint (Cassandra)
 1. Cassandra repository needs to calculate rate when metric type != GAUGE
 1. Lazy result rollups
 1. Caching of Cassandra indexes.
 1. Caching of resource attributes (Cassandra)
 1. Degroup metrics in `JrrdSampleRepository` (for onms-compatibility).
 1. Graph!
 1. Integrate persistence with OpenNMS.
 1. Missing system bundle imports (com.sun.\*., sun.\*.) in jrobin.
 1. Centrally published polling / collection configuration.
 1. Remote event collection.
 1. Jrrd repository hardcodes archive properties (step, CF, etc).
 1. Redo routes (somehow, magic consultant power-config)
 1. Turn api.sample.Results into an interface (iterable); Uses pluggable
    aggregation to process underlying results
 1. Need API for getting sample resources by endpoint
 1. SampleSet is not great; Externalizable? Other, more compact serialization?
 1. netmgt.api.sample: javadocs
 1. Good tests for api.sample implementations (then remove old from api.sample)
 
Completed
---------
 1. Create OSGi bundle of Cassandra driver.
 1. Get `JrrdSampleRespoitory` writing to onms-compatible paths (requires
    some refactor of `Resource`).
 1. Reorder / prioritize this list!
 1. Implement storage of resource attributes (Cassandra repository).
 1. Implement storage of resource attributes (Jrrd repository).
 1. Implement:
    - Query resource attributes (Cassandra)
    - Query resource attributes (Jrrd)
