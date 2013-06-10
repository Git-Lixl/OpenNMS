
-- NOTES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- All values are of type double.  Supporting arbitrary types would be
-- require making measurements.value a blob.  Native values would need
-- to be serialized to bytes for writing, and byte[]s marshalled to
-- native types on read, in the application.
--
-- This schema, as of yet, lacks the means to keep rows from growing
-- unbounded.  One of sharding (by row key, table, etc), or column TTLs,
-- or both, will need to be added.
--
-- This schema does not yet support storage of rollups. 
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

DROP KEYSPACE seriesdb;

CREATE KEYSPACE seriesdb
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE seriesdb;

CREATE TABLE metrics (
    group      text,
    metric     text,
    type       text,
    PRIMARY KEY(group, metric)
);

CREATE TABLE samples (
    resource      text,
    collected_at  timestamp,
    metric        text,
    value         double,
    PRIMARY KEY(resource, collected_at, metric)
);

CREATE TABLE samples_step3600 (
    resource      text,
    interval      timestamp,
    metric        text,
    value         map<text, double>,
    PRIMARY KEY(resource, interval, metric)
) WITH CLUSTERING ORDER BY (interval DESC);

CREATE TABLE samples_step86400 (
    resource      text,
    interval      timestamp,
    metric        text,
    value         map<text, double>,
    PRIMARY KEY(resource, interval, metric)
) WITH CLUSTERING ORDER BY (interval DESC);

-- Name/value pairs associated w/ resource
CREATE TABLE resources (
    resource   text,
    attribute  text,
    value      text,
    PRIMARY KEY(resource, attribute)
);

-- Metrics indexed by resource
CREATE TABLE metrics_x_resource (
    resource   text,
    group      text,
    metric     text,
    PRIMARY KEY(resource, group, metric)
);

-- Resources indexed by endpoint
CREATE TABLE resources_x_endpoint (
    endpoint   text,
    resource   text,
    PRIMARY KEY(endpoint, resource)
);
