package com.honda.aem.core.curdOpertion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeVsResource {

    final Logger logger = LoggerFactory.getLogger(getClass());

    public String createNode(Session session) {

        try {
            Node node = session.getNode("/content/honda/us/en");
            Node demoNode = node.addNode("demoNode");
            demoNode.setProperty("name", "jagan");
            demoNode.setProperty("age", "22");
            session.save();
            logger.info(demoNode.getPath());
            return demoNode.getPath();
        } catch (Exception e) {

        }

        return "";

    }

    public void readNodePropetices(Session session, String path) throws Exception {

        Node currentNode = session.getNode(path);
        String title = currentNode.getProperty("jcr:title").toString();
        String description = currentNode.getProperty("jcr:description").toString();
        logger.info(title + "" + description);
        PropertyIterator propertyIterator = currentNode.getProperties();
        NodeIterator nodeIterator = currentNode.getNodes();
        while (propertyIterator.hasNext()) {
            Property property = propertyIterator.nextProperty();
            property.getName();
            property.getValue();
            if (property.isMultiple()) {
                Value[] values = property.getValues();
                for (Value v : values) {
                    logger.info(v.toString());

                }
            }

        }
        while (nodeIterator.hasNext()) {
            logger.info(nodeIterator.nextNode().getPath());

        }

    }

    @SuppressWarnings("unlikely-arg-type")
    public void updateNode(String path, Session session) throws PathNotFoundException, RepositoryException {
        Node currentNode = session.getNode(path);
        currentNode.setProperty("jcr:title", "New Title");
        PropertyIterator pi = currentNode.getProperties();
        while (pi.hasNext()) {
            Property currentProperty = pi.nextProperty();
            if (currentProperty.getName().equals("jcr:title")) {
                currentNode.setProperty(currentProperty.getName(), "New Value");
            }
        }
        session.save();

    }

    public void removeNodeAndProperty(Session session, String path) throws Exception {

        Node currentNode = session.getNode(path);
        currentNode.getProperty("jcr:title").remove();
        if (currentNode.hasNode("demoNode")) {
            currentNode.getNode("demoNode").remove();

        }
        session.save();
    }

    // Resource Api.

    public void crudResourceApi(String path, ResourceResolver reslover) throws PersistenceException {
        // create resouce
        Resource resouce = reslover.getResource(path);

        Map<String, Object> param = new HashMap<>();
        param.put("name", "jagan");
        param.put("age", "20");
        reslover.create(resouce, path, param);
        reslover.commit();

        // read
        ValueMap vm = resouce.getValueMap();
        String title = vm.get("jcr:title", "");
        logger.info(title);
        Iterator<Resource> listResource = resouce.listChildren();
        resouce.getChildren();
        while (listResource.hasNext()) {
            Resource child = listResource.next();
            ValueMap cvm = child.getValueMap();
            cvm.get("jcr:title", "");
        }
        for (Map.Entry<String, Object> entry : vm.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
        }

    }

}
