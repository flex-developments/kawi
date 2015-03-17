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

/**
 * KawiDataQueueNode
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class KawiDataQueueNode {
    //Estaticas para los tratamientos que se les da a los nodos en la cola
    final public static int KAWI_QUEUE_DATA_NODE_TREATMENT_STRING = 0;
    final public static int KAWI_QUEUE_DATA_NODE_TREATMENT_LOCAL_FILE = 1;
    final public static int KAWI_QUEUE_DATA_NODE_TREATMENT_REMOTE_FILE = 2;
    final public static int KAWI_QUEUE_DATA_NODE_TREATMENT_REMOTE_PDF = 3;
    
    //Atributos del nodo
    private String data = null;
    private int treatment = 0;

    public KawiDataQueueNode(String data, int treatment) throws KawiException {
        if(data == null)
            throw new KawiException(KawiException.ERROR_KAWI_ADDDATA_DATA_NULL);
        
        this.data = data;
        this.treatment = treatment;
    }

    public String getData() {
        return data;
    }

    public int getTreatment() {
        return treatment;
    }
}
