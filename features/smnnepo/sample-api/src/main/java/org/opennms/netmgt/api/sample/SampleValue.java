package org.opennms.netmgt.api.sample;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public abstract class SampleValue<T extends Number> extends Number implements Comparable<Number> {

	private static final long	serialVersionUID	= 1L;

	public static final byte	COUNTER				= 0x01;
	public static final byte	DERIVE				= 0x02;
	public static final byte	ABSOLUTE			= 0x03;
	public static final byte	GAUGE				= 0x04;

	protected final T m_value;


	public SampleValue(T value) {
		m_value = value;
	}

	protected T getValue() {
		return m_value;
	}

	public static SampleValue<?> compose(ByteBuffer data) {
		byte dataType = data.get();

		switch (dataType) {
			case COUNTER:
				byte[] value = new byte[data.remaining()];
				data.get(value, 0, data.remaining());
				return new CounterValue(new BigInteger(value));
			case GAUGE:
				return new GaugeValue(data.getDouble());

			// TODO: DERIVE / ABSOLUTE are stubbed
			case DERIVE:
				return new DeriveValue(data.getLong());
			case ABSOLUTE:
				return new AbsoluteValue(data.getLong());
			default:
				throw new IllegalArgumentException(String.format("parsed unknown type descriptor from buffer (0x%x)", dataType));
		}
	}

	public static ByteBuffer decompose(SampleValue<?> value) {
		switch (value.getType()) {
			case COUNTER:
				byte[] bytes = ((BigInteger)value.getValue()).toByteArray();
				ByteBuffer bb = ByteBuffer.allocate(bytes.length + 1).put(COUNTER).put(bytes);
				bb.rewind();
				return bb;

			// TODO: GAUGE/ DERIVE / ABSOLUTE are stubbed
			case GAUGE:
				return ByteBuffer.allocate(9).put(GAUGE).putDouble((Double)value.getValue());
			case DERIVE:
				return ByteBuffer.allocate(9).put(DERIVE).putLong((Long)value.getValue());
			case ABSOLUTE:
				return ByteBuffer.allocate(9).put(ABSOLUTE).putLong((Long)value.getValue());
			default:
				throw new IllegalArgumentException(String.format("value does not correspond to a known type"));
		}
	}

	public abstract SampleValue<?> delta(Number other);

	public abstract SampleValue<?> subtract(Number other);

	public abstract SampleValue<?> add(Number other);

	public abstract SampleValue<?> multiply(Number other);

	public abstract SampleValue<?> divide(Number object);

	public abstract MetricType getType();
}
