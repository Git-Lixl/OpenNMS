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

package org.opennms.netmgt.provision.detector.client.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.opennms.core.rpc.xml.AbstractXmlRpcModule;
import org.opennms.netmgt.provision.AsyncServiceDetector;
import org.opennms.netmgt.provision.DetectFuture;
import org.opennms.netmgt.provision.DetectFutureListener;
import org.opennms.netmgt.provision.DetectRequest;
import org.opennms.netmgt.provision.ServiceDetector;
import org.opennms.netmgt.provision.SyncServiceDetector;
import org.opennms.netmgt.provision.detector.registry.api.ServiceDetectorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Executes detectors.
 *
 * @author jwhite
 */
public class DetectorClientRpcModule extends AbstractXmlRpcModule<DetectorRequestDTO, DetectorResponseDTO> {

    @Autowired
    private ServiceDetectorRegistry serviceDetectorRegistry;

    @Autowired
    @Qualifier("scanExecutor")
    private Executor executor;

    public DetectorClientRpcModule() {
        super(DetectorRequestDTO.class, DetectorResponseDTO.class);
    }

    @Override
    public CompletableFuture<DetectorResponseDTO> execute(DetectorRequestDTO request) {
        String className = request.getClassName();
        Map<String, String> attributes = request.getAttributeMap();
        ServiceDetector detector = serviceDetectorRegistry.getDetectorByClassName(className, attributes);
        if (detector == null) {
            throw new IllegalArgumentException("No detector found with class name '" + className + "'.");
        }
        return detectService(detector, request);
    }

    private CompletableFuture<DetectorResponseDTO> detectService(ServiceDetector detector, DetectRequest detectRequest) {
        detector.init();
        if (detector instanceof SyncServiceDetector) {
            final SyncServiceDetector syncDetector = (SyncServiceDetector) detector;
            return CompletableFuture.supplyAsync(new Supplier<DetectorResponseDTO>() {
                @Override
                public DetectorResponseDTO get() {
                    try {
                        return new DetectorResponseDTO(syncDetector.detect(detectRequest));
                    } catch (Throwable t) {
                        return new DetectorResponseDTO(t);
                    } finally {
                        syncDetector.dispose();
                    }
                }
            }, executor);
        } else if (detector instanceof AsyncServiceDetector) {
            final CompletableFuture<DetectorResponseDTO> future = new CompletableFuture<>();
            final AsyncServiceDetector asyncDetector = (AsyncServiceDetector) detector;
            try {
                DetectFuture detectFuture = asyncDetector.detect(detectRequest);
                detectFuture.addListener(new DetectFutureListener<DetectFuture>() {
                    @Override
                    public void operationComplete(DetectFuture detectFuture) {
                        if (detectFuture.getException() != null) {
                            future.complete(new DetectorResponseDTO(detectFuture.getException()));
                        } else {
                            future.complete(new DetectorResponseDTO(detectFuture));
                        }
                    }
                });
            } catch (Throwable t) {
                DetectorResponseDTO responseDTO = new DetectorResponseDTO();
                responseDTO.setDetected(false);
                responseDTO.setFailureMesage(t.getMessage());
                future.complete(responseDTO);
            }
            return future;
        } else {
            throw new IllegalArgumentException("Unsupported detector type.");
        }
    }

    @Override
    public String getId() {
        return "Detect";
    }

    public void setServiceDetectorRegistry(ServiceDetectorRegistry serviceDetectorRegistry) {
        this.serviceDetectorRegistry = serviceDetectorRegistry;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
