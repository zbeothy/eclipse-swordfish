/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.policy.exploration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.regex.Pattern;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyReader;
import org.apache.ws.policy.util.StAXPolicyWriter;
import org.eclipse.swordfish.policy.util.PolicyProcessor;
import org.eclipse.swordfish.policy.util.UnexpectedPolicyProcessingException;

/**
 * The Class PolicySampleMatcher.
 */
public class PolicySampleMatcher {

    /** The Constant directories. */
    public static final String[] DIRECTORIES =
            {"D:/workspace/dev/policy-operation/src/test/data/compression",
                    "D:/workspace/dev/policy-operation/src/test/data/correlation",
                    "D:/workspace/dev/policy-operation/src/test/data/sdxvalidation",
                    "D:/workspace/dev/policy-operation/src/test/data/priority",
                    "D:/workspace/dev/policy-operation/src/test/data/signature",
                    "D:/workspace/dev/policy-operation/src/test/data/encryption",
                    "D:/workspace/dev/policy-operation/src/test/data/conversationalBinding",
                    "D:/workspace/dev/policy-operation/src/test/data/subscription",
                    "D:/workspace/dev/policy-operation/src/test/data/customvalidation",
                    "D:/workspace/dev/policy-operation/src/test/data/authentication",
                    "D:/workspace/dev/policy-operation/src/test/data/authorization",
                    "D:/workspace/dev/policy-operation/src/test/data/extension",
                    "D:/workspace/dev/policy-operation/src/test/data/trackinglevel",
                    "D:/workspace/dev/policy-operation/src/test/data/maxresponse",
                    "D:/workspace/dev/policy-operation/src/test/data/transformation",
                    "D:/workspace/dev/policy-operation/src/test/data/transport"};

    /** The Constant pattern. */
    public static final Pattern CONSUMER_PATTERN = Pattern.compile("consumer_.*\\.xml");

    /** The processor. */
    private static PolicyProcessor processor = new PolicyProcessor();

    /**
     * The main method.
     * 
     * @param args
     *        the args
     */
    public static void main(final String[] args) {
        PolicySampleMatcher matcher = new PolicySampleMatcher();
        for (int i = 0; i < DIRECTORIES.length; i++) {
            matcher.createSamples(DIRECTORIES[i]);
        }
    }

    /** The reader. */
    private PolicyReader reader = PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);

    /** The writer. */
    private StAXPolicyWriter writer = new StAXPolicyWriter();

    /**
     * Creates the samples.
     * 
     * @param dirname
     *        the dirname
     */
    public void createSamples(final String dirname) {
        System.out.println("Processing " + dirname);
        File dir = new File(dirname);
        File[] files = dir.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return CONSUMER_PATTERN.matcher(pathname.getName()).matches();
            }

        });
        for (int i = 0; i < files.length; i++) {
            File consumer = files[i];
            String name = consumer.getName();
            int start = name.indexOf('_') + 1;
            int end = name.indexOf('.');
            String providerPath = dirname + "/provider_" + name.subSequence(start, end) + ".xml";
            String resultPath = dirname + "/result_" + name.subSequence(start, end) + ".xml";
            File provider = new File(providerPath);
            File result = new File(resultPath);
            try {
                FileInputStream cis = new FileInputStream(consumer);
                Policy consumerPolicy = this.reader.readPolicy(cis);
                FileInputStream pis = new FileInputStream(provider);
                Policy providerPolicy = this.reader.readPolicy(pis);
                Policy resultPolicy = processor.match(consumerPolicy, providerPolicy);
                FileOutputStream ros = new FileOutputStream(result);
                this.writer.writePolicy(resultPolicy, ros);
                System.out.println("  Wrote " + resultPath);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            } catch (UnexpectedPolicyProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
