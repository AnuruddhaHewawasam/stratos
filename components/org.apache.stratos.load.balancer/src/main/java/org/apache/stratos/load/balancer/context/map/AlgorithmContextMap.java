/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.stratos.load.balancer.context.map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratos.load.balancer.context.LoadBalancerContext;
import org.wso2.carbon.caching.impl.DistributedMapProvider;
import org.wso2.carbon.caching.impl.MapEntryListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Algorithm context map is a singleton class for managing load balancing algorithm context
 * of each service cluster.
 */
public class AlgorithmContextMap {
    @SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AlgorithmContextMap.class);
    private static final String LOAD_BALANCER_ALGORITHM_CONTEXT_MAP = "LOAD_BALANCER_ALGORITHM_CONTEXT_MAP";
    private static AlgorithmContextMap instance;

    private final Map<String, Integer> clusterMemberIndexMap;

    private AlgorithmContextMap() {
        DistributedMapProvider distributedMapProvider = LoadBalancerContext.getInstance().getDistributedMapProvider();
        if(distributedMapProvider != null) {
            clusterMemberIndexMap = distributedMapProvider.getMap(LOAD_BALANCER_ALGORITHM_CONTEXT_MAP,
                    new MapEntryListener() {
                        @Override
                        public <X> void entryAdded(X x) {
                        }

                        @Override
                        public <X> void entryRemoved(X x) {
                        }

                        @Override
                        public <X> void entryUpdated(X x) {
                        }
                    });
            if(clusterMemberIndexMap != null) {
                if(log.isInfoEnabled()) {
                    log.info("Load balancer context map initiated in distributed mode");
                }
            } else {
                log.error("Could not initialize algorithm context map from distributed map provider");
            }
        } else {
            clusterMemberIndexMap = new HashMap<String, Integer>();
            if(log.isInfoEnabled()) {
                log.info("Load balancer context map initiated locally");
            }
        }
    }

    public static AlgorithmContextMap getInstance() {
        if (instance == null) {
            synchronized (AlgorithmContextMap.class) {
                if (instance == null) {
                    instance = new AlgorithmContextMap();
                }
            }
        }
        return instance;
    }

    private String constructKey(String serviceName, String clusterId) {
        return String.format("%s-%s", serviceName, clusterId);
    }

    public void putCurrentMemberIndex(String serviceName, String clusterId, int currentMemberIndex) {
        String key = constructKey(serviceName, clusterId);
        clusterMemberIndexMap.put(key, currentMemberIndex);
    }

    public void removeCluster(String serviceName, String clusterId) {
        String key = constructKey(serviceName, clusterId);
        clusterMemberIndexMap.remove(key);
    }

    public int getCurrentMemberIndex(String serviceName, String clusterId) {
        String key = constructKey(serviceName, clusterId);
        return clusterMemberIndexMap.get(key);
    }
}
