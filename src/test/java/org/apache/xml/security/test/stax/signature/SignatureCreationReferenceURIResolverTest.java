/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.test.stax.signature;

import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.impl.resourceResolvers.ResolverHttp;
import org.apache.xml.security.test.stax.utils.HttpRequestRedirectorProxy;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.apache.xml.security.utils.resolver.implementations.ResolverDirectHTTP;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SignatureCreationReferenceURIResolverTest extends AbstractSignatureCreationTest {

    @Test
    public void testSignatureCreationWithExternalFilesystemXMLReference() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions =
                new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);

        // Set the key up
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
                this.getClass().getClassLoader().getResource("transmitter.jks").openStream(),
                "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        properties.setSignatureKey(key);
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");
        properties.setSignatureCerts(new X509Certificate[]{cert});

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addSignaturePart(securePart);

        //todo remove null-c14n when external transformation is implemented
        securePart = new SecurePart("file://" + BASEDIR + "/src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml", null, XMLSecurityConstants.NS_XMLDSIG_SHA1);
        properties.addSignaturePart(securePart);

        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");

        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);

        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();

        Document document =
                documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, cert, properties.getSignatureSecureParts());
    }

    @Test
    public void testSignatureCreationWithExternalFilesystemBinaryReference() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions =
                new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);

        // Set the key up
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
                this.getClass().getClassLoader().getResource("transmitter.jks").openStream(),
                "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        properties.setSignatureKey(key);
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");
        properties.setSignatureCerts(new X509Certificate[]{cert});

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addSignaturePart(securePart);

        securePart = new SecurePart("file://" + BASEDIR + "/target/test-classes/org/apache/xml/security/test/stax/signature/SignatureVerificationReferenceURIResolverTest.class", null, XMLSecurityConstants.NS_XMLDSIG_SHA1);
        properties.addSignaturePart(securePart);

        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");

        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);

        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();

        Document document =
                documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, cert, properties.getSignatureSecureParts());
    }

    @Test
    public void testSignatureCreationWithExternalHttpReference() throws Exception {

        Proxy proxy = HttpRequestRedirectorProxy.startHttpEngine();

        try {
            ResolverHttp.setProxy(proxy);

            // Set up the Configuration
            XMLSecurityProperties properties = new XMLSecurityProperties();
            XMLSecurityConstants.Action[] actions =
                    new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
            properties.setOutAction(actions);

            // Set the key up
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(
                    this.getClass().getClassLoader().getResource("transmitter.jks").openStream(),
                    "default".toCharArray()
            );
            Key key = keyStore.getKey("transmitter", "default".toCharArray());
            properties.setSignatureKey(key);
            X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");
            properties.setSignatureCerts(new X509Certificate[]{cert});

            SecurePart securePart =
                    new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
            properties.addSignaturePart(securePart);

            securePart = new SecurePart("http://www.w3.org/Signature/2002/04/xml-stylesheet.b64", null, XMLSecurityConstants.NS_XMLDSIG_SHA1);
            properties.addSignaturePart(securePart);

            OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");

            InputStream sourceDocument =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);

            XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
            xmlStreamWriter.close();

            Document document =
                    documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));

            ResolverDirectHTTP resolverDirectHTTP = new ResolverDirectHTTP();
            resolverDirectHTTP.engineSetProperty("http.proxy.host", ((InetSocketAddress)proxy.address()).getAddress().getHostAddress());
            resolverDirectHTTP.engineSetProperty("http.proxy.port", "" + ((InetSocketAddress)proxy.address()).getPort());

            // Verify using DOM
            verifyUsingDOM(document, cert, properties.getSignatureSecureParts(), resolverDirectHTTP);
        } finally {
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    @Test
    public void testSignatureCreationWithSameDocumentXPointerIdApostropheReference() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions =
                new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);

        // Set the key up
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
                this.getClass().getClassLoader().getResource("transmitter.jks").openStream(),
                "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        properties.setSignatureKey(key);
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");
        properties.setSignatureCerts(new X509Certificate[]{cert});

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PaymentInfo"), true, SecurePart.Modifier.Element);
        properties.addSignaturePart(securePart);

        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");

        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);

        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();

        Document document =
                documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));

        NodeList nodeList = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
        Assert.assertEquals(1, nodeList.getLength());

        String uri = ((Element) nodeList.item(0)).getAttribute("URI");
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.startsWith("#xpointer"));

        // Verify using DOM
        verifyUsingDOM(document, cert, properties.getSignatureSecureParts());
    }
}
