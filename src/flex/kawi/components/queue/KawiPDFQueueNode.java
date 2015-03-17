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
 * KawiPDFQueueNode
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class KawiPDFQueueNode {
    private final String pdfInPath;
    private final String pdfOutPath;
    private final String readPass;
    private final String writePass;
    private final String reason;
    private final String location;
    private final String contact;
    private final boolean noModify;
    private final boolean visible;
    private final int page;
    private final String imgPath;
    private final float imgP1X;
    private final float imgP1Y;
    private final float imgP2X;
    private final float imgP2Y;
    private final int imgRotation;

    public KawiPDFQueueNode(
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
        this.pdfInPath = pdfInPath;
        this.pdfOutPath = pdfOutPath;
        this.readPass = readPass;
        this.writePass = writePass;
        this.reason = reason;
        this.location = location;
        this.contact = contact;
        this.noModify = noModify;
        this.visible = visible;
        this.page = page;
        this.imgPath = imgPath;
        this.imgP1X = imgP1X;
        this.imgP1Y = imgP1Y;
        this.imgP2X = imgP2X;
        this.imgP2Y = imgP2Y;
        this.imgRotation = imgRotation;
    }

    public String getPdfInPath() {
        return pdfInPath;
    }

    public String getPdfOutPath() {
        return pdfOutPath;
    }

    public String getReadPass() {
        return readPass;
    }

    public String getWritePass() {
        return writePass;
    }

    public String getReason() {
        return reason;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public boolean isNoModify() {
        return noModify;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getPage() {
        return page;
    }

    public String getImgPath() {
        return imgPath;
    }

    public float getImgP1X() {
        return imgP1X;
    }

    public float getImgP1Y() {
        return imgP1Y;
    }

    public float getImgP2X() {
        return imgP2X;
    }

    public float getImgP2Y() {
        return imgP2Y;
    }

    public int getImgRotation() {
        return imgRotation;
    }
}
