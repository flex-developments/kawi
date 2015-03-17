/*
 * kawi
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com | flopez@suscerte.gob.ve
 * Ing. Yessica De Ascencao - yessicadeascencao@gmail.com | ydeascencao@suscerte.gob.ve
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
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * KawiPackHeaders
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class KawiPackHeaders extends AbstractKawiPackSecction {
    //Estaticas para la generación y carga del paquete en XML
    final private static String XML_HEADERS_SECCTION = "Headers";
    final private static String XML_HEADER_CHILDS_ID = "Header";
    final private static String XML_HEADER_ID = "idHeader";
    final private static String XML_HEADER_VALUE = "Value";
    
    //Estaticas de cabeceras aceptadas
    final public static String KAWI_PACK_HEADER_SIGN_ALG = "algoritmoFirma";
    final public static String KAWI_PACK_HEADER_DATE = "fecha";
    final public static String KAWI_PACK_HEADER_CERTIFICATE = "certificado";
    final public static String KAWI_PACK_HEADER_CONFIGURATION = "configuracion";
    
    //Mapa de cabeceras
    private final HashMap<String, String> headers = new HashMap<>();
    
    public void add(String id, String valor) throws KawiPackException {
        if(id == null)
            throw new KawiPackException(KawiPackException.ERROR_KAWI_PACK_HEADER_ID_NULL);
        if(id.isEmpty())
            throw new KawiPackException(KawiPackException.ERROR_KAWI_PACK_HEADER_ID_EMPTY);
        
        headers.put(id, valor);
    }
    
    public void remove(String id) {
        headers.remove(id);
    }
    
    public List<String> getIds() {
        List<String> result = new ArrayList<>();
        for (String id : headers.keySet())
            result.add(id);
        return result;
    }

    protected List<String> getValues() {
        List<String> result = new ArrayList<>();
        for (String values : headers.values())
            result.add(values);
        return result;
    }
    
    public String getHeader(String id) throws KawiPackException {
        String result = headers.get(id);
        if (result == null)
            throw new KawiPackException(KawiPackException.ERROR_KAWI_PACK_HEADER_ID_UNKNOW);
        
        return result;
    }
    
    //------------------------------- Abstractos -------------------------------
    @Override
    public void clear() {
        headers.clear();
    }
    
    @Override
    public int size() {
        return headers.size();
    }
    
    //---------------------------- Cargar desde XML ----------------------------
    @Override
    public void loadFromXML(String xml) throws KawiPackException {
        clear();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource archivo = new InputSource();
            archivo.setCharacterStream(new StringReader(xml)); 
            Document document = db.parse(archivo);
            document.getDocumentElement().normalize();
            
            //Se lee item 0 porque sólo se procesa un paquete a la vez
            NodeList headerNode = document.getElementsByTagName(XML_HEADER_CHILDS_ID);
            for (int s = 0; s < headerNode.getLength(); s++) {
                Node nodoMaster = headerNode.item(s);
                String header;
                String value;

                if (nodoMaster.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoMaster = (Element) nodoMaster;
                    
                    header = loadFromXMLNodeElement(elementoMaster, XML_HEADER_ID);
                    value = loadFromXMLNodeElement(elementoMaster, XML_HEADER_VALUE);
                    
                    add(header,value);
                }
            }
            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new KawiPackException(e);
        }
    }
    
    @Override
    public String toXML() {
        String xml = "\t<" + XML_HEADERS_SECCTION + ">\n";
        for (Map.Entry e : headers.entrySet()) {
            xml =  xml +
                    "\t\t<" + XML_HEADER_CHILDS_ID + ">\n" +
                    "\t\t\t<" + XML_HEADER_ID + ">" + e.getKey() + "</" + XML_HEADER_ID + ">\n" +
                    "\t\t\t<" + XML_HEADER_VALUE + ">" + e.getValue() + "</"+ XML_HEADER_VALUE + ">\n" +
                    "\t\t</" + XML_HEADER_CHILDS_ID + ">\n";
        }
        return xml + "\t</" + XML_HEADERS_SECCTION + ">\n";
    }
}
