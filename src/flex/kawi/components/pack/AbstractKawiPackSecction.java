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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * AbstractKawiPackSecction
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public abstract class AbstractKawiPackSecction {
    
    protected String loadFromXMLNodeElement(Element elementoMaster, String elementTagName) {
        try {
            NodeList node1 = elementoMaster.getElementsByTagName(elementTagName);
            Element element1 = (Element) node1.item(0);
            NodeList dataNode = element1.getChildNodes();
            return ((Node) dataNode.item(0)).getNodeValue();
            
        } catch(NullPointerException ex) {
            return "";
        }
    }
    
    //------------------------------- Abstractos -------------------------------
    public abstract void clear();
    public abstract int size();
    public abstract void loadFromXML(String xml) throws KawiPackException;
    public abstract String toXML();
}
