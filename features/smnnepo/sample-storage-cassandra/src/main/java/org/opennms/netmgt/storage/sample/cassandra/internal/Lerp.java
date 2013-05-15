package org.opennms.netmgt.storage.sample.cassandra.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.MetricType;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.Results.Row;
import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.Timestamp;

public class Lerp {
	private static final TimeUnit	STD_UNITS	= TimeUnit.MILLISECONDS;
	private static final boolean	DEBUG		= false;

	public static Iterable<Timestamp> getSteps(final Timestamp start, final Timestamp finish, final long step) {
		return new Iterable<Timestamp>() {
			@Override
			public Iterator<Timestamp> iterator() {
				return new Iterator<Timestamp>() {
					private int count = 0;
					//private Timestamp m_start = roundToStep(start, step);
					private Timestamp m_start = start.atStepBoundaryStart(step, STD_UNITS);
					//private Timestamp m_finish = roundToStep(finish, step);
					private Timestamp m_finish = finish.atStepBoundaryStart(step, STD_UNITS);

					private Timestamp getCurrent() {
						return new Timestamp(m_start.asMillis() + (count * step), STD_UNITS);
					}

					@Override
					public boolean hasNext() {
						return (getCurrent().compareTo(m_finish) >= 0) ? false : true;
					}

					@Override
					public Timestamp next() {
						try {
							return getCurrent();
						}
						finally {
							count++;
						}
					}

					@Override
					public void remove() {
						// TODO Auto-generated method stub
					}
				};
			}
		};
	}

	public static Results align(Results in, Timestamp start, Timestamp finish, long step) {
		Metric[] metrics = new Metric[in.getMetrics().size()];
		Results out = new Results(in.getResource(), in.getMetrics().toArray(metrics));

		List<Results.Row> rows = new ArrayList<Results.Row>(in.getRows());
		assert in.getRows().size() > 1;
		
		Iterator<Timestamp> steps = getSteps(start, finish, step).iterator();
		Timestamp currentStep = steps.next();
		
		Row lastL = null;

		for (int i = 0; i < (rows.size()-1); i++) {
			Row rowL = rows.get(i);
			Row rowR = rows.get(i+1);
			Timestamp xL = rowL.getTimestamp();
			Timestamp xR = rowR.getTimestamp();

			// Ignore values earlier than start
			if (xR.lessThan(start)) {
				continue;
			}
			
			// List is ordered; Break if we hit something higher than finish
			if (xL.greaterThan(finish)) {
				break;
			}

			// Back-fill
			int xff = 2, misses = 0;

			while (xL.greaterThan(currentStep)) {
				if (misses < xff) {
					if (lastL != null) {
						printStep("BACKFILL", lastL, currentStep, rowL);
						storeInterpolatedSamples(out, lastL, rowL, currentStep, metrics);
					}
					else {
						printStep("NAN (0)", rowL, currentStep, rowR);
						storeNaNSamples(out, currentStep, metrics);
					}
				}
				else {
					printStep("NAN (1)", rowL, currentStep, rowR);
					storeNaNSamples(out, currentStep, metrics);
				}
				misses = misses + 1;
				currentStep = steps.next();
			}

			// We're on
			if ((xL.lessThan(currentStep) || xL.equals(currentStep)) && (xR.greaterThan(currentStep) || xR.equals(currentStep))) {
				printStep("LERP", rowL, currentStep, rowR);
				storeInterpolatedSamples(out, rowL, rowR, currentStep, metrics);
				currentStep = steps.next();
				lastL = rowL;
			}
			else {
				printStep("SKIP", rowL, currentStep, rowR);
			}
		}

		// Back-fill any missing from the end
		while (currentStep.lessThan(finish) || currentStep.equals(finish)) {
			storeNaNSamples(out, currentStep, metrics);
			currentStep = steps.next();
		}

		return out;
	}

	private static void printStep(Object...args) {
		if (DEBUG) System.out.printf("%-8s: L=%s, C=%s, R=%s%n", args);
	}
	
	private static void storeNaNSamples(Results out, Timestamp step, Metric...metrics) {
		for (Metric m : metrics) {
			out.addSample(new Sample(out.getResource(), m, step, Double.NaN));
		}
	}

	private static void storeInterpolatedSamples(Results out, Row rowL, Row rowR, Timestamp step, Metric...metrics) {
		for (Metric m : metrics) {
			Sample yL = rowL.getSample(m);
			Sample yR = rowR.getSample(m);

			double value;

			if (yL != null && yR != null) {
				value = interpolate(step, rowL.getTimestamp(), rowR.getTimestamp(), yL, yR);
			}
			else {
				value = Double.NaN;
			}

			out.addSample(new Sample(out.getResource(), m, step, value));
		}
	}

	private static double interpolate(Timestamp x, Timestamp x0, Timestamp x1, Sample y0, Sample y1) {
		return interpolate(x.asMillis(), x0.asMillis(), x1.asMillis(), y0.getValue(), y1.getValue());
	}
	
	private static double interpolate(double x, double x0, double x1, double y0, double y1) {
		return ((x - x0) * (y1 - y0) / (x1 - x0)) + y0;
	}

	private static void printResults(Results r) {
		for (Results.Row row : r.getRows()) {
			System.out.printf(
					"%s (%d): %f%n",
					row.getTimestamp(),
					row.getTimestamp().asMillis(),
					row.getSample(new Metric("m", MetricType.COUNTER, "g")).getValue());
		}
	}

//	private static Timestamp roundToStep(Timestamp t, long stepSize) {
//		return t.roundToStep(stepSize, STD_UNITS);
//	}

	private static Timestamp ts(long secs) {
		return new Timestamp(secs, TimeUnit.SECONDS);
	}

	public static void main(String[] args) {

		Results in = null, out = null;

		in = new Results(new Resource(null, "resrc"), new Metric("m", MetricType.COUNTER, "g"));

		Timestamp s = ts(0);
		Timestamp e = new Timestamp(0 + (55 * 60 * 1000), STD_UNITS);
		long step = 300000;

		Sample[] samples = new Sample[] {
			new Sample(in.getResource(), in.getMetrics().get(0), ts(0),    1.0d),
			new Sample(in.getResource(), in.getMetrics().get(0), ts(300),  5.0d),
			new Sample(in.getResource(), in.getMetrics().get(0), ts(326),  6.0d),
			//new Sample(in.getResource(), in.getMetrics().get(0), ts(622),  7.0d),	
			//new Sample(in.getResource(), in.getMetrics().get(0), ts(899),  9.9d),	
			//new Sample(in.getResource(), in.getMetrics().get(0), ts(1200), 5.1d),	
			new Sample(in.getResource(), in.getMetrics().get(0), ts(1588), 5.9d),	
			new Sample(in.getResource(), in.getMetrics().get(0), ts(1800), 4.4d),	
			new Sample(in.getResource(), in.getMetrics().get(0), ts(2200), 2.0d),	
			new Sample(in.getResource(), in.getMetrics().get(0), ts(2477), 1.1d),	
			new Sample(in.getResource(), in.getMetrics().get(0), ts(2701), 5.1d),	
			new Sample(in.getResource(), in.getMetrics().get(0), ts(3000), 7.6d)
		};

		for (Sample sample : samples)
			in.addSample(sample);

		System.out.printf("From %s, to %s (step %d seconds)%n", s.toString(), e.toString(), (step/1000));

		out = Lerp.align(in, s, e, step);
		
		System.out.println("-------");
		printResults(in);
		System.out.println("-------");
		printResults(out);
	}
}
