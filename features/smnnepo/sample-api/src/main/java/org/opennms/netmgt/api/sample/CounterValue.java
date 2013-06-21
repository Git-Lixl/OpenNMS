package org.opennms.netmgt.api.sample;

import java.math.BigInteger;

public class CounterValue extends SampleValue<BigInteger> {
	private static final long	serialVersionUID	= 1L;
	private static final BigInteger	MAX32			= BigInteger.valueOf(2L).pow(32).subtract(BigInteger.ONE);
	private static final BigInteger	MAX64			= BigInteger.valueOf(2L).pow(64).subtract(BigInteger.ONE);


	CounterValue(long value) {
		this(BigInteger.valueOf(value));
	}

	public CounterValue(BigInteger value) {
		super(value);
	}

	@Override
	public CounterValue delta(Number other) {
		final BigInteger diff = m_value.subtract(getBigInt(other));

		if (diff.compareTo(BigInteger.ZERO) < 0) {
			final BigInteger diff32 = diff.add(MAX32).add(BigInteger.ONE);

			if (diff32.compareTo(BigInteger.ZERO) < 0) {
				return new CounterValue(diff.add(MAX64).add(BigInteger.ONE));
			}
			else {
				return new CounterValue(diff32);
			}
		}
		else {
			return new CounterValue(diff);
		}
	}

	@Override
	public CounterValue add(Number other) {
		return new CounterValue(m_value.add(getBigInt(other)));
	}

	@Override
	public SampleValue<?> subtract(Number other) {
		return new CounterValue(m_value.subtract(getBigInt(other)));
	}

	@Override
	public SampleValue<?> multiply(Number other) {
		return new CounterValue(m_value.multiply(getBigInt(other)));
	}

	@Override
	public SampleValue<?> divide(Number object) {
		return new CounterValue(m_value.divide(getBigInt(object)));
	}

	@Override
	public int intValue() {
		return m_value.intValue();
	}

	@Override
	public long longValue() {
		return m_value.longValue();
	}

	@Override
	public float floatValue() {
		return m_value.floatValue();
	}

	@Override
	public double doubleValue() {
		return m_value.doubleValue();
	}

	@Override
	public MetricType getType() {
		return MetricType.COUNTER;
	}

	@Override
	public int compareTo(Number o) {
		return m_value.compareTo(getBigInt(o));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CounterValue) {
			return m_value.equals(((CounterValue)o).getValue());
		}
		else if (o instanceof BigInteger) {
			return m_value.equals(o);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return m_value.hashCode();
	}

	@Override
	public String toString() {
		return m_value.toString();
	}

	/*
	 * Avoids creation of a new BigIntger instance if the supplied Number is an
	 * instance of CounterValue (typical).
	 */
	private BigInteger getBigInt(Number num) {
		if (num instanceof CounterValue) {
			return ((CounterValue) num).getValue();
		} else {
			return BigInteger.valueOf(num.longValue());
		}
	}
}
