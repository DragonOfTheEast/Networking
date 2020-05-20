/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

import javax.swing.plaf.PanelUI;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class Constants {
    private Constants(){
        throw new AssertionError("Prohibited Function");
    }
    public static final String CHR_ENC = "ISO-8859-1"; //character encoding
    public static final int EIGHT = 8; //constant eight
    public static final String CLNG = "CLNG "; //CLNG operation
    public static final char T = 'T'; //alphabet T
    public static final char I = 'I'; //alphabet I
    public static final char C = 'C'; //alphabet C
    public static final char L = 'L'; //alphabet L
    public static final char R = 'R'; //alphabet R
    public static final int TWO = 2;  //constant 2
    public static final String VERSION= "TIKTAK 1.0\r\n"; //Tiktak version
    public static final int SIX = 6; //constant 6
    public static final int SEVEN = 7; //constant 7
    public static final String ID_VAR = "ID "; //ID literal
    public static final int FOUR = 4; //constant 4
    public static final int FIVE = 5; //constant 5
    public static final int THREE = 3; // constant 3
    public static final String ACK = "ACK\r\n"; //constant ACK
    public static final char A = 'A'; //alphabet A
    public static final String TOST = "TOST\r\n";
    public static final char O = 'O'; //alphabet O
    public static final char E = 'E'; //aphabet E;
    public static final int THIRTEEN = 13; //constant 13;
    public static final int TEN = 10; //constant 9
    public static final String ERROR = "ERROR ";
    public static final String CRED = "CRED ";
    public static final String LTSRL = "LTSRL ";
    public static final String NEW_DELIM = "(?<=\\r\\n)";
    public static final int SHIFT = 8;
    public static final int MASK = 0xff;

}
