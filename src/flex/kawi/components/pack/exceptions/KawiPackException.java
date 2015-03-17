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

package flex.kawi.components.pack.exceptions;

import flex.kawi.i18n.I18n;

/**
 * KawiPackException
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class KawiPackException extends Exception {
    public static String ERROR_KAWI_PACK_HEADER_ID_NULL = I18n.get(I18n.M_ERROR_KAWI_PACK_SECCTION_ID_NULL);
    public static String ERROR_KAWI_PACK_HEADER_ID_EMPTY = I18n.get(I18n.M_ERROR_KAWI_PACK_SECCTION_ID_EMPTY);
    public static String ERROR_KAWI_PACK_HEADER_ID_UNKNOW = I18n.get(I18n.M_ERROR_KAWI_PACK_SECCTION_ID_UNKNOW);
    
    public KawiPackException(String message) {
        super(message);
    }
    
    public KawiPackException(Exception e) {
        super(e);
    }
}
