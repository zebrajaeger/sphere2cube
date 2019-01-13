package de.zebrajaeger.sphere2cube.psdimage;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

/**
 * Utils to make live with jaxp easier
 *
 * @author lars
 */
public class XmlUtils {

    /**
     * Find all attribute in a node with specific prefix.
     *
     * @param node         the note that contains the attributes
     * @param prefix       the prefix to search for
     * @param removePrefix true: removes the prefix from attribute name (name is the key of resulting map)
     * @return the values or a empty map
     */
    public static HashMap<String, String> getAttributes(Node node, String prefix, boolean removePrefix) {
        final HashMap<String, String> result = new HashMap<>();

        final NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            final Node att = attributes.item(i);
            String name = att.getNodeName();
            if (name.startsWith(prefix)) {
                if (removePrefix) {
                    name = name.substring(prefix.length());
                }
                result.put(name, att.getNodeValue());
            }
        }
        return result;
    }

    /**
     * Get all attributes as a Map from a node
     *
     * @param node the node taht contains the attributes to read
     * @return the values or a empty map
     */
    public static HashMap<String, String> getAttributes(Node node) {
        final HashMap<String, String> result = new HashMap<>();

        final NamedNodeMap atts = node.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            final Node att = atts.item(i);
            result.put(att.getNodeName(), att.getNodeValue());
        }
        return result;
    }

    /**
     * Get the first node with a given name and returns it
     *
     * @param root the node that contains the children to search within
     * @param name the name of the node
     * @return a node with given or null if not found
     */
    public static Node find(Node root, String name) {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node subnode = list.item(i);
            if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                if (subnode.getNodeName().equals(name)) {
                    return subnode;
                }
            }
        }
        return null;
    }

    public static Node findWithAttrubute(Node root, String name, String attributeName) {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node subnode = list.item(i);
            if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                if (subnode.getNodeName().equals(name)) {
                    if (subnode.getAttributes().getNamedItem(attributeName) != null) {
                        return subnode;
                    }
                }
            }
        }
        return null;
    }
}
