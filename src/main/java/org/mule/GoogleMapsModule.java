/**
 * Mule Development Kit
 * Copyright 2010-2011 (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.mule;

import org.apache.commons.io.IOUtils;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.net.URL;

/**
 * Simple Mule extension to demonstrate the use of the Mule DevKit.
 *
 * @author Federico Recio
 */
@Module(name="googlemaps", schemaVersion="1.0")
public class GoogleMapsModule
{
    private static final String PROTOTYPE_URL =
            "http://maps.googleapis.com/maps/api/distancematrix/xml?origins=%s&destinations=%s&units=%s&sensor=false";
    private static final String DISTANCE_XPATH = "//DistanceMatrixResponse/row/element/distance/text/text()";
    /**
     * Configurable
     */
    @Configurable
    private DistanceUnit distanceUnit;

    /**
     * Calls Google Maps API to get the distance between the two given locations. The distance unit to
     * use depends on {@link GoogleMapsModule#distanceUnit}
     *
     * {@sample.xml ../../../doc/GoogleMaps-connector.xml.sample googlemaps:getDistance}
     *
     * @param origin the origin
     * @param destination the destination
     * @return the distance between origin and destination
     */
    @Processor
    public String getDistance(@Optional @Default("San+Francisco") String origin,
                              String destination) throws Exception
    {
        String effectiveUrl = String.format(PROTOTYPE_URL, origin, destination, distanceUnit);
        InputStream responseStream = new URL(effectiveUrl).openConnection().getInputStream();
        try
        {
            return getXPath(responseStream, DISTANCE_XPATH);
        }
        finally
        {
            IOUtils.closeQuietly(responseStream);
        }
    }

    /**
     * Set property
     *
     * @param distanceUnit My property
     */
    public void setDistanceUnit(DistanceUnit distanceUnit)
    {
        this.distanceUnit = distanceUnit;
    }

    private String getXPath(InputStream inputStream, String xpath) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(inputStream);
        XPathFactory factory = XPathFactory.newInstance();
        XPathExpression expr = factory.newXPath().compile(xpath);
        return (String) expr.evaluate(doc, XPathConstants.STRING);
    }
}
