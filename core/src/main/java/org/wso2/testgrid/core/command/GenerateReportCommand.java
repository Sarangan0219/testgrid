/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.testgrid.core.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.Option;
import org.wso2.testgrid.common.ProductTestPlan;
import org.wso2.testgrid.common.exception.TestGridException;
import org.wso2.testgrid.common.exception.TestReportEngineException;
import org.wso2.testgrid.core.TestGridMgtService;
import org.wso2.testgrid.core.TestGridMgtServiceImpl;
import org.wso2.testgrid.reporting.TestReportEngineImpl;

/**
 * This generates a cumulative test report that consists of
 * all testplans for a given product, version and channel.
 */
public class GenerateReportCommand extends Command {

    private static final Log log = LogFactory.getLog(GenerateReportCommand.class);

    @Option(name = "--product",
            usage = "Product Name",
            aliases = { "-p" },
            required = true)
    protected String productName = "";

    @Option(name = "--version",
            usage = "product version",
            aliases = { "-v" },
            required = true)
    protected String productVersion = "";

    /**
     * Channels are how we distribute our products to customers.
     * Common channels include public branch, LTS (support) branch,
     * premium/premium+ branches.
     */
    @Option(name = "--channel",
            usage = "product channel",
            aliases = { "-c" },
            required = false)
    protected String channel = "public";

    @Option(name = "--infraRepo",
            usage = "Location of Infra plans. "
                    + "Under this location, there should be a Infrastructure/ folder."
                    + "Assume this location is the test-grid-is-resources",
            aliases = { "-ir" },
            required = true)
    protected String infraRepo = "";

    @Override
    public void execute() throws TestGridException {
        log.info("Create product test plan command");
        log.info(
                "Input Arguments: \n" +
                        "\tProduct name: " + productName + "\n" +
                        "\tProduct version: " + productVersion + "\n" +
                        "\tChannel" + channel);

        TestGridMgtService testGridMgtService = new TestGridMgtServiceImpl();
        ProductTestPlan productTestPlan = testGridMgtService.createProduct(productName, productVersion, infraRepo);

        try {
            new TestReportEngineImpl().generateReport(productTestPlan);
        } catch (TestReportEngineException e) {
            String msg = "Unable to generate test report for the ProductTests ran for product '" +
                    productTestPlan.getProductName() + "', version '" + productTestPlan.getProductVersion() + "'";
            log.error(msg, e);
            throw new TestGridException(msg, e);
        }

    }
}