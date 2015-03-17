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

package flex.kawi.components.queue;

import flex.kawi.exception.KawiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * KawiPDFQueue
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao en gmail
 * @version 1.0
 */
public class KawiPDFQueue {
    //Mapa de elementos
    private final HashMap<String, KawiPDFQueueNode> nodes = new HashMap<>();
    
    public void addPDF(
        String id, 
        String pdfInPath, 
        String pdfOutPath, 
        String readPass, 
        String writePass, 
        String reason, 
        String location, 
        String contact, 
        boolean noModify, 
        boolean visible, 
        int page,
        String imgPath, 
        float imgP1X, 
        float imgP1Y, 
        float imgP2X, 
        float imgP2Y, 
        int imgRotation
    ) throws KawiException {
        if(id == null)
            throw new KawiException(KawiException.ERROR_KAWI_ADDDATA_ID_NULL);
        
        nodes.put(
            id, 
            new KawiPDFQueueNode(
                pdfInPath, 
                pdfOutPath, 
                readPass, 
                writePass, 
                reason, 
                location, 
                contact,  
                noModify, 
                visible, 
                page,
                imgPath, 
                imgP1X, 
                imgP1Y, 
                imgP2X, 
                imgP2Y, 
                imgRotation
            )
        );
    }
    
    public void removePDF(String id) {
        nodes.remove(id);
    }
    
    public void clearPDF() {
        nodes.clear();
    }
    
    public int size() {
        return nodes.size();
    }
    
    public KawiPDFQueueNode getNode(String id) {
        return nodes.get(id);
    }
    
    public List<String> getIds() {
        List<String> result = new ArrayList<>();
        for (String id : nodes.keySet())
            result.add(id);
        return result;
    }
    
    public List<KawiPDFQueueNode> getNodes() {
        List<KawiPDFQueueNode> result = new ArrayList<>();
        for (KawiPDFQueueNode values : nodes.values())
            result.add(values);
        return result;
    }
}
