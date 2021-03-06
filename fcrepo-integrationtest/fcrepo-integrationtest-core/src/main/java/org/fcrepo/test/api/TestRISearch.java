/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://fedora-commons.org/license/).
 */
package org.fcrepo.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.fcrepo.client.FedoraClient;

import org.fcrepo.common.Constants;
import org.fcrepo.common.Models;
import org.fcrepo.common.PID;

import org.fcrepo.server.resourceIndex.UvaStdImgTripleGenerator_1;

import org.fcrepo.test.DemoObjectTestSetup;
import org.fcrepo.test.FedoraServerTestCase;

import static org.fcrepo.test.api.RISearchUtil.checkSPOCount;


/**
 * Tests risearch functionality when the resource index is enabled.
 *
 * @author Chris Wilper
 */
public class TestRISearch
extends FedoraServerTestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("TestRISearch TestSuite");
        suite.addTestSuite(TestRISearch.class);
        return new DemoObjectTestSetup(suite);
    }

    /**
     * Implicit relationship to Fedora object CModel
     * @throws Exception
     */
    public void testRISearchBasicCModel() throws Exception {
        FedoraClient client = getFedoraClient();
        for (String pid : new String[] { "demo:SmileyPens",
                "demo:SmileyPens_M",
                                         "demo:SmileyGreetingCard" }) {
            String query = "<" + PID.toURI(pid) + ">"
                        + " <" + Constants.MODEL.HAS_MODEL.uri + ">"
                        + " <" + Models.FEDORA_OBJECT_CURRENT.uri + ">";
            checkSPOCount(client, query, 1);
        }
    }

    /**
     * Explicit RELS-EXT relation to collection object
     * @throws Exception
     */
    public void testRISearchRelsExtCollection() throws Exception {
        FedoraClient client = getFedoraClient();
        String collectionPid = "demo:SmileyStuff";
        for (String pid : new String[] { "demo:SmileyPens",
                "demo:SmileyPens_M",
                                         "demo:SmileyGreetingCard" }) {
            String query = "<" + PID.toURI(pid) + ">"
                        + " <" + Constants.RELS_EXT.IS_MEMBER_OF.uri + ">"
                        + " <" + PID.toURI(collectionPid) + ">";
            checkSPOCount(client, query, 1);
        }
    }

    /**
     * RELS-INT relationships specifying image size for jpeg datastreams
     * @throws Exception
     */
    public void testRISearchRelsInt() throws Exception {
        FedoraClient client = getFedoraClient();
        for (String pid : new String[] { "demo:SmileyPens" ,
                "demo:SmileyPens_M",
                                         "demo:SmileyGreetingCard" }) {
            String query = "<" + PID.toURI(pid) + "/MEDIUM_SIZE" + ">"
                        + " <" + "http://ns.adobe.com/exif/1.0/PixelXDimension" + ">"
                        + " \"320\"";
            checkSPOCount(client, query, 1);
        }
    }
    
    /**
     * Test that Spring-configured triple generators are working
     */
    public void testSpringTripleGenerators() throws Exception {
        FedoraClient client = getFedoraClient();
        String query = "<info:fedora/demo:5>"
        + " <" + UvaStdImgTripleGenerator_1.TEST_PREDICATE + ">"
        + " \"true\"";
        checkSPOCount(client, query, 1);
    }
}
