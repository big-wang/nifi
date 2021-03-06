/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.util;

import java.io.File;
import org.apache.nifi.components.state.StateManager;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.controller.ControllerServiceInitializationContext;
import org.apache.nifi.controller.ControllerServiceLookup;
import org.apache.nifi.controller.NodeTypeProvider;
import org.apache.nifi.kerberos.KerberosContext;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.state.MockStateManager;

public class MockControllerServiceInitializationContext extends MockControllerServiceLookup implements ControllerServiceInitializationContext, ControllerServiceLookup, NodeTypeProvider {

    private final String identifier;
    private final ComponentLog logger;
    private final StateManager stateManager;
    private volatile boolean isClustered;
    private volatile boolean isPrimaryNode;
    private final KerberosContext kerberosContext;

    public MockControllerServiceInitializationContext(final ControllerService controllerService, final String identifier) {
        this(controllerService, identifier, new MockStateManager(controllerService));
    }

    public MockControllerServiceInitializationContext(final ControllerService controllerService, final String identifier, final StateManager stateManager) {
        this(controllerService, identifier, new MockComponentLog(identifier, controllerService), stateManager);
    }

    public MockControllerServiceInitializationContext(final ControllerService controllerService, final String identifier, final ComponentLog logger, final StateManager stateManager) {
        this(controllerService, identifier, logger, stateManager, null);
    }

    public MockControllerServiceInitializationContext(final ControllerService controllerService, final String identifier, final ComponentLog logger, final StateManager stateManager,
                                                      final KerberosContext kerberosContext) {
        this.identifier = identifier;
        this.logger = logger;
        this.stateManager = stateManager;
        this.kerberosContext = kerberosContext;
        addControllerService(controllerService, identifier);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getControllerServiceName(final String serviceIdentifier) {
        return null;
    }

    @Override
    public ControllerServiceLookup getControllerServiceLookup() {
        return this;
    }

    @Override
    public ComponentLog getLogger() {
        return logger;
    }

    @Override
    public StateManager getStateManager() {
        return stateManager;
    }

    @Override
    public NodeTypeProvider getNodeTypeProvider() {
        return this;
    }

    @Override
    public String getKerberosServicePrincipal() {
        return kerberosContext != null ? kerberosContext.getKerberosServicePrincipal() : null;
    }

    @Override
    public File getKerberosServiceKeytab() {
        return kerberosContext != null ? kerberosContext.getKerberosServiceKeytab() : null;
    }

    @Override
    public File getKerberosConfigurationFile() {
        return kerberosContext != null ? kerberosContext.getKerberosConfigurationFile() : null;
    }

    @Override
    public boolean isClustered() {
        return isClustered;
    }

    @Override
    public boolean isPrimary() {
        return isPrimaryNode;
    }

    public void setClustered(boolean clustered) {
        isClustered = clustered;
    }

    public void setPrimaryNode(boolean primaryNode) {
        if (!isClustered && primaryNode) {
            throw new IllegalArgumentException("Primary node is only available in cluster. Use setClustered(true) first.");
        }
        isPrimaryNode = primaryNode;
    }
}
