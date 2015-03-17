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

package flex.kawi.resources;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * I18n
 * Clase estatica para controlar la internacionalizacion de los mensajes.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @version 1.0
 */
public class KawiPropertiesBundle {
    final private static String BUNDLE_PATH = "flex/kawi/resources/KawiConfig";
    
    //List Resource Keys........................................................
    final public static String KAWI_TITLE = "KAWI_TITLE";
    final public static String KAWI_TITLE_SIZE = "KAWI_TITLE_SIZE";
    final public static String KAWI_PACKAGE_HEADERS = "KAWI_PACKAGE_HEADERS";
    final public static String KAWI_PACKAGE_ENCRYPT = "KAWI_PACKAGE_ENCRYPT";
    final public static String KAWI_SIGN_STANDARD = "KAWI_SIGN_STANDARD";
    final public static String KAWI_SIGN_ALG = "KAWI_SIGN_ALG";
    final public static String KAWI_DATE_SOURCE = "KAWI_DATE_SOURCE";
    final public static String KAWI_DRIVER_VERIFY_INTEGRITY = "KAWI_DRIVER_VERIFY_INTEGRITY";
    final public static String KAWI_NTP_SERVERS = "KAWI_NTP_SERVERS";
    final public static String KAWI_NTP_SYNC_INTERVAL = "KAWI_NTP_SYNC_INTERVAL";
    final public static String KAWI_VERIFY_CERTIFICATE_INTERVAL = "KAWI_VERIFY_CERTIFICATE_INTERVAL";
    final public static String KAWI_VERIFY_CERTIFICATE_TRY_DOWNLOAD_LCR = "KAWI_VERIFY_CERTIFICATE_TRY_DOWNLOAD_LCR";
    final public static String KAWI_VERIFY_CERTIFICATE_WITH_HOST = "KAWI_VERIFY_CERTIFICATE_WITH_HOST";
    final public static String KAWI_VERIFY_CERTIFICATE_WITH_OCSP = "KAWI_VERIFY_CERTIFICATE_WITH_OCSP";
    final public static String KAWI_CERTIFICATE_INVALID_ON_NTP_FAIL = "KAWI_CERTIFICATE_INVALID_ON_NTP_FAIL";
    final public static String KAWI_CERTIFICATE_INVALID_ON_OCSP_FAIL = "KAWI_CERTIFICATE_INVALID_ON_OCSP_FAIL";
    final public static String KAWI_CERTIFICATE_INVALID_ON_LCR_FAIL = "KAWI_CERTIFICATE_INVALID_ON_LCR_FAIL";
    final public static String KAWI_CERTIFICATE_INVALID_ON_BOTH_FAIL = "KAWI_CERTIFICATE_INVALID_ON_BOTH_FAIL";
    final public static String KAWI_CERTIFICATE_VERIFY_VERBOSE = "KAWI_CERTIFICATE_VERIFY_VERBOSE";
    //--------------------------------------------------------------------------
    
    /**
     * Obtener String internacionalizado.
     * 
     * @param key Clave del string dentro del bundle.
     * 
     * @return valor de la clave dentro del bundle.
     */
    public static String get(String key) {
        return ResourceBundle.getBundle(BUNDLE_PATH).getString(key);
    }
    
    /**
     * Obtener String internacionalizado con formato.
     * 
     * @param key Clave del string dentro del bundle.
     * @param arguments Argumentos para el formato.
     * 
     * @return valor de la clave dentro del bundle con formato procesado.
     */
    public static String get(String key, Object ... arguments) {
        MessageFormat temp = new MessageFormat(get(key));
        return temp.format(arguments);
    }

    public static String getLangPath() {
        return BUNDLE_PATH;
    }
}
