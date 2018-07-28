package de.skonline90.Time2Sleep.controller.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.skonline90.Time2Sleep.controller.properties.ApplicationProperties;

public class XmlSettingsReader
{
    public static List<String[]> loadDefaultSettings()
            throws ParserConfigurationException, SAXException, IOException
    {
        List<String[]> result = new ArrayList<>();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory
            .newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder
            .parse(ApplicationProperties.SETTINGS_FILE_LOCATION);
        String[] tagNames = {
                SleepTimerSettingsXmlSaveFileCreator.XML_ACTION_SETTING_TAG_NAME,
                SleepTimerSettingsXmlSaveFileCreator.XML_INCREMENT_TIME,
                SleepTimerSettingsXmlSaveFileCreator.XML_COUNTDOWN_TIME_SETTING};
        NodeList nodes;
        for (int i = 0; i < tagNames.length; i++)
        {
            String[] temp = new String[2];
            nodes = doc.getElementsByTagName(tagNames[i]);
            Node node = nodes.item(0);
            temp[0] = tagNames[i];
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element value = (Element) node;
                temp[1] = value.getTextContent();
            }
            result.add(temp);
        }
        return result;
    }
}
