package org.opennms.netmgt.api.sample;

public class GaugeValue extends SampleValue<Double> {

	private static final long	serialVersionUID	= 1L;


	public GaugeValue(Double value) {
		super(value);
	}


	@Override
	public SampleValue<?> delta(Number other) {
		// TODO Auto-generated method stub
		return new GaugeValue(0.0d);
	}

	@Override
	public SampleValue<?> add(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SampleValue<?> subtract(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SampleValue<?> multiply(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SampleValue<?> divide(Number object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return m_value;
	}

	@Override
	public MetricType getType() {
		return MetricType.GAUGE;
	}


	@Override
	public int compareTo(Number o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
