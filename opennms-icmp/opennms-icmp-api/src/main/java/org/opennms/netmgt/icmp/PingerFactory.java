/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.icmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>PingerFactory class.</p>
 *
 * @author <A HREF="mailto:seth@opennms.org">Seth Leger</A>
 * @author <A HREF="mailto:brozow@opennms.org">Matt Brozowski</A>
 */
public abstract class PingerFactory {
    private static final int MAX_DSCP = (1 << 16) - 1;

    private static final Logger LOG = LoggerFactory.getLogger(PingerFactory.class);

    private static Pinger[] m_pingers = new Pinger[MAX_DSCP];
    //private static Map<Integer, Pinger> m_pingers = new ConcurrentHashMap<>();
    //private static Pinger m_pinger;

    /**
     * @deprecated Use {@link #getInstance(int)} instead.
     */
    public static Pinger getInstance() {
        return PingerFactory.getInstance(0);
    }

    /**
     * Returns an implementation of the default {@link Pinger} class
     *
     * @return a {@link Pinger} object.
     */
    public static Pinger getInstance(final int tc) {
        if (m_pingers[tc] == null) {
            final String pingerClassName = System.getProperty("org.opennms.netmgt.icmp.pingerClass", "org.opennms.netmgt.icmp.jni6.Jni6Pinger");
            Class<? extends Pinger> clazz = null;

            try {
                if (m_pingers[0] != null) {
                    // If the default (0) DSCP pinger has already been initialized, use the
                    // same class in case it's been manually overridden (ie, in the Remote Poller)
                    clazz = m_pingers[0].getClass();
                } else {
                    clazz = Class.forName(pingerClassName).asSubclass(Pinger.class);
                }
                final Pinger pinger = clazz.newInstance();
                pinger.setTrafficClass(tc);
                m_pingers[tc] = pinger;
            } catch (final ClassNotFoundException e) {
                IllegalArgumentException ex = new IllegalArgumentException("Unable to find class named " + pingerClassName, e);
                LOG.error(ex.getLocalizedMessage(), ex);
                throw ex;
            } catch (final InstantiationException e) {
                IllegalArgumentException ex = new IllegalArgumentException("Error trying to create pinger of type " + clazz, e);
                LOG.error(ex.getLocalizedMessage(), ex);
                throw ex;
            } catch (final IllegalAccessException e) {
                IllegalArgumentException ex = new IllegalArgumentException("Unable to create pinger of type " + clazz + ".  It does not appear to have a public constructor", e);
                LOG.error(ex.getLocalizedMessage(), ex);
                throw ex;
            } catch (final Throwable e) {
                IllegalArgumentException ex = new IllegalArgumentException("Unexpected exception thrown while trying to create pinger of type " + clazz, e);
                LOG.error(ex.getLocalizedMessage(), ex);
                throw ex;
            }
        }
        return m_pingers[tc];
    }

    /**
     * @deprecated Use {@link #setInstance(int, Pinger)} instead.

     */
    public static void setInstance(final Pinger pinger) {
        m_pingers[0] = pinger;
    }

    public static void setInstance(final int tc, final Pinger pinger) {
        m_pingers[tc] = pinger;
    }

    /**
     * @deprecated Use {@link #reset(int)} instead.
     */
    protected static void reset() {
        for (int i=0; i < Integer.MAX_VALUE; i++) {
            m_pingers[i] = null;
        }
    }
    
    protected static void reset(final int tc) {
        m_pingers[tc] = null;
    }
}
