package com.app.mschooling.utils;

public interface IBarcode {

    /**
     * CODABAR 1D format.
     */
    int CODABAR = 8;

    /**
     * Code 39 1D format.
     */
    int CODE39 = 2;

    /**
     * Code 93 1D format.
     */
    int CODE93 = 4;

    /**
     * Code 128 1D format.
     */
    int CODE128 = 1;

    /**
     * EAN-8 1D format.
     */
    int EAN8 = 64;

    /**
     * EAN-13 1D format.
     */
    int EAN13 = 32;

    /**
     * ITF (Interleaved Two of Five) 1D format.
     */
    int ITF = 128;

    /**
     * UPC-A 1D format.
     */
    int UPCA = 512;

    /**
     * UPC-E 1D format.
     */
    int UPCE = 1024;

    /**
     * QR Code 2D barcode format.
     */
    int QR_CODE = 256;

    /**
     * Data Matrix 2D barcode format.
     */
    int DATA_MATRIX = 16;

    /**
     * PDF417 format.
     */
    int PDF417 = 2048;

    /**
     * Aztec 2D barcode format.
     */
    int AZTEC = 4096;


    int CONTACT_INFO = 1;
    int EMAIL = 2;
    int ISBN = 3;
    int PHONE = 4;
    int PRODUCT = 5;
    int SMS = 6;
    int TEXT = 7;
    int URL = 8;
    int WIFI = 9;
    int GEO = 10;
    int CALENDAR_EVENT = 11;
    int DRIVER_LICENSE = 12;


    public static final int ALL_FORMATS = 0;
    int CODE11 = 1;
    int CODE25 = 4;
    int INTERLEAVED25 = 5;
    int IDENTCODE = 6;
    int LEITCODE = 7;
    int ONECODE = 8;
    int PLANET = 9;
    int POSTNET = 10;
    int RM4SCC = 11;
    int EAN128 = 13;
    int SSCC18 = 14;
    int MSI = 16;
    int ISSN = 22;
    int UOM_PIXEL = 0;
    int UOM_INCH = 1;
    int UOM_CM = 2;
    int ROTATE_0 = 0;
    int ROTATE_90 = 1;
    int ROTATE_180 = 2;
    int ROTATE_270 = 3;
    int FNC1_NONE = 0;
    int FNC1_ENABLE = 1;


}
