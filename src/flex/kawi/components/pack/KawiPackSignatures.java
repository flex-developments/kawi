/*
 * kawi
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments en gmail
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments en gmail | flopez en suscerte gob ve
 * Ing. Yessica De Ascencao - yessicadeascencao en gmail | ydeascencao en suscerte gob ve
 *
 * Este programa es software libre; Usted puede usarlo bajo los terminos de la
 * licencia de software GPL version 2.0 de la Free Software Foundation.
 *
 * Este programa se distribuye con la esperanza de que sea util, pero SIN
 * NINGUNA GARANTIA; tampoco las implicitas garantias de MERCANTILIDAD o
 * ADECUACION A UN PROPOSITO PARTICULAR.
 * Consulte la licencia GPL para mas detalles. Usted debe recibir una copia
 * de la GPL junto con este programa; si no, escriba a la Free Software
 * Foundation Inc. 51 Franklin Street,5 Piso, Boston, MA 02110-1301, USA.
 */

package flex.kawi.components.pack;

import flex.kawi.components.pack.exceptions.KawiPackException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * KawiPackSignatures
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao en gmail
 * @version 1.0
 */
public class KawiPackSignatures extends AbstractKawiPackSecction {
    //Estaticas para la generación y carga del paquete en XML
    final private static String XML_SIGNATURES_SECCTION = "Signatures";
    final private static String XML_SIGNATURE_CHILDS_ID = "Signature";
    final private static String XML_ELEMENT_DATA = "signatureID";
    final private static String XML_ELEMENT_SIGNATURE = "signatureData";
    final private static String XML_ELEMENT_SIGNATURE_DATE = "signatureDate";
    
    //Mapa de firmas
    private final HashMap<String, SignatureNode> signatures = new HashMap<>();
    
    public void add(String id, String value) throws KawiPackException {
        add(id, value, "");
    }
    
    public void add(String id, String sign, String date) throws KawiPackException {
        if(id == null)
            throw new KawiPackException(KawiPackException.ERROR_KAWI_PACK_HEADER_ID_NULL);
        if(id.isEmpty())
            throw new KawiPackException(KawiPackException.ERROR_KAWI_PACK_HEADER_ID_EMPTY);
        
        signatures.put(id, new SignatureNode(sign, date) );
    }
    
    public List<String> getIds() {
        List<String> result = new ArrayList<>();
        for (String id : signatures.keySet())
            result.add(id);
        return result;
    }
    
    public List<SignatureNode> getSignaturesNodes() {
        List<SignatureNode> result = new ArrayList<>();
        for (SignatureNode values : signatures.values())
            result.add(values);
        return result;
    }
    
    public String getSignature(String id) throws KawiPackException {
        String result = signatures.get(id).getValue();
        if (result == null)
            throw new KawiPackException(KawiPackException.ERROR_KAWI_PACK_HEADER_ID_UNKNOW);
        
        return result;
    }
    
    public String getDate(String id) throws KawiPackException {
        String result = signatures.get(id).getDate();
        if (result == null)
            throw new KawiPackException(KawiPackException.ERROR_KAWI_PACK_HEADER_ID_UNKNOW);
        
        return result;
    }
    
    //--------------------------Abstractos--------------------------------------
    @Override
    public void clear() {
        signatures.clear();
    }

    @Override
    public int size() {
        return signatures.size();
    }
    
    //---------------------------- Cargar desde XML ----------------------------
    @Override
    public void loadFromXML(String stringPack) throws KawiPackException {
        clear();
        
        Document document = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource file = new InputSource();
            file.setCharacterStream(new StringReader(stringPack)); 
            document = db.parse(file);
            document.getDocumentElement().normalize();
            
        } catch (ParserConfigurationException | SAXException | IOException | DOMException ex) {
            throw new KawiPackException(ex);
        }
            
        //Se lee item 0 porque sólo se procesa un paquete a la vez
        NodeList headerNode = document.getElementsByTagName(XML_SIGNATURE_CHILDS_ID);
        for (int s = 0; s < headerNode.getLength(); s++) {
            Node masterNode = headerNode.item(s);
            String data;
            String sigm;
            String signDate;

            if (masterNode.getNodeType() == Node.ELEMENT_NODE) {
                Element masterElement = (Element) masterNode;
                
                data = loadFromXMLNodeElement(masterElement, XML_ELEMENT_DATA);
                sigm = loadFromXMLNodeElement(masterElement, XML_ELEMENT_SIGNATURE);
                signDate = loadFromXMLNodeElement(masterElement, XML_ELEMENT_SIGNATURE_DATE);
                
                add(data, sigm, signDate);
            }
        }
    }
    
    @Override
    public String toXML() {
        String xml = "\t<" + XML_SIGNATURES_SECCTION + ">\n";
        
        for (String key : signatures.keySet()) {
            String current = "\t\t<" + XML_SIGNATURE_CHILDS_ID + ">\n" +
                             "\t\t\t<" + XML_ELEMENT_DATA + ">default</" + XML_ELEMENT_DATA + ">\n" +
                             "\t\t\t<" + XML_ELEMENT_SIGNATURE + ">default</"+ XML_ELEMENT_SIGNATURE + ">\n" +
                             "\t\t\t<" + XML_ELEMENT_SIGNATURE_DATE + ">default</"+ XML_ELEMENT_SIGNATURE_DATE + ">\n" +
                             "\t\t</" + XML_SIGNATURE_CHILDS_ID + ">\n";
            try {
                current = "\t\t<" + XML_SIGNATURE_CHILDS_ID + ">\n" +
                          "\t\t\t<" + XML_ELEMENT_DATA + ">" + key + "</" + XML_ELEMENT_DATA + ">\n" +
                          "\t\t\t<" + XML_ELEMENT_SIGNATURE + ">" + getSignature(key) + "</"+ XML_ELEMENT_SIGNATURE + ">\n" +
                          "\t\t\t<" + XML_ELEMENT_SIGNATURE_DATE + ">" + getDate(key) + "</"+ XML_ELEMENT_SIGNATURE_DATE + ">\n" +
                          "\t\t</" + XML_SIGNATURE_CHILDS_ID + ">\n";
                
            } catch (KawiPackException ex) {
                return "";
            }
            xml =  xml + current;
        }
        return xml + "\t</" + XML_SIGNATURES_SECCTION + ">\n";
    }
}