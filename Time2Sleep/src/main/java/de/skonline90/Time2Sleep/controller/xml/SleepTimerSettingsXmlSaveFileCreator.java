package de.skonline90.Time2Sleep.controller.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates the settings.xml file. It saves
 * the settings on the GUI.
 * 
 * @author skonline90
 * @version 28.07.18
 */
public class SleepTimerSettingsXmlSaveFileCreator
{
    public static final String XML_ROOT_TAG_NAME = "Time2Sleep-Settings";
    public static final String XML_ACTION_SETTING_TAG_NAME = "Action-Setting";
    public static final String XML_INCREMENT_TIME = "IncrementTime";
    public static final String XML_COUNTDOWN_TIME_SETTING = "CountdownTime";
    public static final String XML_SELECTED_AUDIO = "Selected-Audio";

    private Document doc;
    private Element root, actionSetting, incrementTime, countdownTime,
            selectedAudio;

    /**
     * Constructor.
     * 
     * @throws ParserConfigurationException If the document builder can not 
     * be created.
     */
    public SleepTimerSettingsXmlSaveFileCreator(String actionSetting,
            String formattedIncrementTime, String formattedCountdownTime,
            String audioSelected) throws ParserConfigurationException
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory
            .newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();

        this.actionSetting = doc.createElement(XML_ACTION_SETTING_TAG_NAME);
        this.incrementTime = doc.createElement(XML_INCREMENT_TIME);
        this.countdownTime = doc.createElement(XML_COUNTDOWN_TIME_SETTING);
        this.selectedAudio = doc.createElement(XML_SELECTED_AUDIO);

        this.actionSetting.appendChild(doc.createTextNode(actionSetting));
        this.incrementTime
            .appendChild(doc.createTextNode(formattedIncrementTime));
        this.countdownTime
            .appendChild(doc.createTextNode(formattedCountdownTime));
        this.selectedAudio.appendChild(doc.createTextNode(audioSelected));

        root = createRootElement(XML_ROOT_TAG_NAME, null, null);
        root.appendChild(this.actionSetting);
        root.appendChild(this.incrementTime);
        root.appendChild(this.countdownTime);
        root.appendChild(this.selectedAudio);
    }

    /**
     * Creates the root element.
     */
    private Element createRootElement(String elementName,
            String elementAttributeName, String elementAttributeValue)
    {
        if (!doc.hasChildNodes())
        {
            Element rootElement = doc.createElement(elementName);
            if (elementAttributeName != null && elementAttributeValue != null)
            {
                Attr rootElementAttribute = doc
                    .createAttribute(elementAttributeName);
                rootElementAttribute.setValue(elementAttributeValue);
                rootElement.setAttributeNode(rootElementAttribute);
            }
            doc.appendChild(rootElement);
            return rootElement;
        }
        System.out.println(
                "Could not create root document, because a root element already exists.");
        return null;
    }

    /**
     * Exports the current tree into a XML file.
     * 
     * @param directoryPath The full path of the file.
     */
    public void exportToXmlFile(String filePath)
            throws TransformerException, IOException
    {
        TransformerFactory transformerFactory = TransformerFactory
            .newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        File file = new File(filePath);
        file.createNewFile();
        StreamResult result = new StreamResult(new File(filePath));

        transformer.transform(source, result);
    }
}
