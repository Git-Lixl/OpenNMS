/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.bsm.service.model.functions.reduce;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.opennms.netmgt.bsm.service.model.Status;
import org.opennms.netmgt.bsm.service.model.StatusWithIndex;

import com.google.common.collect.Lists;

public class HighestSeverityAboveTest {

    public static List<StatusWithIndex> toListWithIndices(List<Status> statuses) {
        final List<StatusWithIndex> indexedStatuses = new ArrayList<>();
        for (int i = 0; i < statuses.size(); i++) {
            indexedStatuses.add(new StatusWithIndex(statuses.get(i), i));
        }
        return indexedStatuses;
    }

    @Test
    public void testReduce() {
        HighestSeverityAbove reduceFunction = new HighestSeverityAbove();
        reduceFunction.setThreshold(Status.MAJOR);

        assertEquals(Optional.empty(), reduceFunction.reduce(Lists.newArrayList()));
        assertEquals(Optional.empty(), reduceFunction.reduce(toListWithIndices(Lists.newArrayList(
                Status.MINOR, Status.MAJOR, Status.WARNING))));

        assertEquals(Status.CRITICAL, reduceFunction.reduce(toListWithIndices(Lists.newArrayList(
                Status.MINOR, Status.MAJOR, Status.WARNING, Status.CRITICAL))).get().getStatus());
    }
}
