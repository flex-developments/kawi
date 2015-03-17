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

package flex.kawi.exception;

import flex.kawi.i18n.I18n;

/**
 * KawiException
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class KawiException extends Exception {
    public static String ERROR_GENERAL_PREFIX = "Error!... ";
    public static String ERROR_KAWI_ADDDATA_ID_NULL = I18n.get(I18n.M_ERROR_KAWI_ADDDATA_ID_NULL);
    public static String ERROR_KAWI_ADDDATA_DATA_NULL = I18n.get(I18n.M_ERROR_KAWI_ADDDATA_DATA_NULL);
    
    public KawiException(String message) {
        super(message);
    }
    
    public KawiException(Throwable ex) {
        super(ex);
    }
    
    public KawiException(String message, Throwable ex) {
        super(message, ex);
    }
}
