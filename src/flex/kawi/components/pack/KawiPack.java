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

/**
 * KawiPack
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao en gmail
 * @version 1.0
 */
public final class KawiPack {
    final private static String XML_ROOT_KAWI_PACK = "KawiPack";
    private KawiPackHeaders headers = new KawiPackHeaders();
    private KawiPackSignatures signatures = new KawiPackSignatures();
    
    //Constructoras
    public KawiPack() {}
    
    public KawiPack(byte[] bytesPack) throws KawiPackException {
        loadKawiPackFromXML(new String(bytesPack));
    }
    
    public KawiPack(String stringPack) throws KawiPackException {
        loadKawiPackFromXML(stringPack);
    }
    
    //Operaciones Cabecera
    public KawiPackHeaders getHeaders() {    
        return headers;
    }
    
    public void setHeaders(KawiPackHeaders headers) {
        this.headers = headers;
    }
    
    //Operaciones Firmas
    public KawiPackSignatures getSignatures() {
        return signatures;
    }

    public void setSignatures(KawiPackSignatures signatures) {
        this.signatures = signatures;
    }
    
    public void clear() {
        headers.clear();
        signatures.clear();
    }
    
    public void loadKawiPackFromXML(String stringPack) throws KawiPackException {
        clear();
        headers.loadFromXML(stringPack);
        signatures.loadFromXML(stringPack);
    }
    
    /**
     * 
     * @return Paquete Kawi como XML
     */
    @Override
    public String toString() {
        String xml = "<?xml version=\"1.0\"?>\n" +
                     "<" + XML_ROOT_KAWI_PACK + ">\n" +
                        headers.toXML() + 
                        signatures.toXML() +
                     "</" + XML_ROOT_KAWI_PACK + ">";
        return xml;
    }
}
