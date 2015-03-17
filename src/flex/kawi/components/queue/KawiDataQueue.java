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

package flex.kawi.components.queue;

import flex.kawi.exception.KawiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * KawiDataQueue
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class KawiDataQueue {
    //Mapa de elementos
    private final HashMap<String, KawiDataQueueNode> nodes = new HashMap<>();
    
    public void addData(String id, String data, int treatment) throws KawiException {
        if(id == null)
            throw new KawiException(KawiException.ERROR_KAWI_ADDDATA_ID_NULL);
        
        nodes.put(id, new KawiDataQueueNode(data, treatment) );
    }
    
    public void removeData(String id) {
        nodes.remove(id);
    }
    
    public void clearData() {
        nodes.clear();
    }
    
    public int size() {
        return nodes.size();
    }
    
    public KawiDataQueueNode getNode(String id) {
        return nodes.get(id);
    }
    
    public List<String> getIds() {
        List<String> result = new ArrayList<>();
        for (String id : nodes.keySet())
            result.add(id);
        return result;
    }
    
    public List<KawiDataQueueNode> getNodes() {
        List<KawiDataQueueNode> result = new ArrayList<>();
        for (KawiDataQueueNode values : nodes.values())
            result.add(values);
        return result;
    }
}
