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

package flex.kawi;

import flex.helpers.LoggerHelper;

/**
 * KawiLogger
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao en gmail
 * @version 1.0
 */
public class KawiLogger {
    private static final LoggerHelper log = new LoggerHelper("KawiLogger", LoggerHelper.LOG_TYPE_SINGLE_DATED);
    
    public static void writeInfoLog(String message) {
        log.writeInfoLog("\n" + message + "\n");
    }
    
    public static void writeWarningLog(String message) {
        log.writeWarningLog("\n" + message + "\n");
    }
    
    public static void writeErrorLog(String message) {
        log.writeErrorLog("\n" + message + "\n");
    }
    
    public static void writeErrorLog(Throwable ex) {
        log.writeErrorLog(ex);
    }
}
