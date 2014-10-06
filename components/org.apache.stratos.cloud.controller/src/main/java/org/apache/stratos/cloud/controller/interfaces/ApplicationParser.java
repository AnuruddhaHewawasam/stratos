/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.stratos.cloud.controller.interfaces;

import org.apache.stratos.cloud.controller.exception.ApplicationDefinitionException;
import org.apache.stratos.cloud.controller.pojo.ApplicationClusterContext;
import org.apache.stratos.cloud.controller.pojo.payload.MetaDataHolder;
import org.apache.stratos.messaging.domain.topology.Application;

import java.util.Set;

public interface ApplicationParser {

    /**
     * Parses the Application Definition
     *
     * @param obj Object with the Application Definition. An Object is used here since there can be
     *            significant changes between the way composite Applications are defined in different
     *            conventions
     * @return Application structure denoting the parsed Application
     * @throws ApplicationDefinitionException If the Application Definition is invalid
     */
    public Application parse (Object obj) throws ApplicationDefinitionException;

    /**
     * Returns a set of ApplicationClusterContext which will comprise of cluster related information
     * extracted from the Application definition
     *
     * @return  Set of ApplicationClusterContext objects
     * @throws ApplicationDefinitionException if any error occurs
     */
    public Set<ApplicationClusterContext> getApplicationClusterContexts() throws ApplicationDefinitionException;

    // TODO: remove
    public Set<MetaDataHolder> getPayloadData () throws ApplicationDefinitionException;
}
