/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.microprofile.jwtauth.tck;

import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * This archive processor adds the files <code>payara-mp-jwt.properties</code>
 * and <code>web.xml</code> to each archive being created by the MP-JWT TCK.
 * 
 * <p>
 * <code>payara-mp-jwt.properties</code> configures the valid issuer, while
 * <code>web.xml</code> contains a fix for a TCK bug, and a fix for a Payara bug
 * (see inside that file for more details).
 * 
 * @author Arjan Tijms
 *
 */
public class ArquillianArchiveProcessor implements ApplicationArchiveProcessor {
    
    private static final Logger LOGGER = Logger.getLogger(ArquillianArchiveProcessor.class.getName());

    @Override
    public void process(Archive<?> archive, TestClass testClass) {
        if (!(archive instanceof WebArchive)) {
            return;
        }
        
        WebArchive webArchive = WebArchive.class.cast(archive);
        Node publicKeyNode = webArchive.get("/WEB-INF/classes/publicKey.pem");
        if (publicKeyNode == null) {
            return;
        }

        Node microprofileConfig = webArchive.get("/META-INF/microprofile-config.properties");
        if (microprofileConfig == null) {
            webArchive.addAsResource("payara-mp-jwt.properties");
        }
        webArchive.addAsWebInfResource("web.xml")
                .addAsWebInfResource("glassfish-web.xml");

        LOGGER.log(INFO, "Augmenting virtual web archive: {0}", archive);
        LOGGER.log(INFO, "Virtually augmented web archive: \n{0}", webArchive.toString(true));
    }
}