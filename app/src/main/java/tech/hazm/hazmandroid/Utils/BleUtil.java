package tech.hazm.hazmandroid.Utils;

import com.clj.fastble.data.BleDevice;
import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Constant.Constant;

public class BleUtil {

//    public static String uuid = "291027EB-7EF5-4551-A475-35D1FFD768B3";
//    public static Beacon beacon = Beacon.newBuilder()
//            .setUUID(uuid)
//            .build();

    public static int findBeaconPattern(BleDevice bleDevice){
        byte[] scanRecord = bleDevice.getScanRecord();
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound){
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //UUID detection
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

            byte  txpw = scanRecord[26];
            // major
            final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            // minor
            final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

            if (uuid.equalsIgnoreCase(Common.uuid)) /*beacon.getUUID().toString())*/{
                Common.strMajor = String.valueOf(major);
                Common.strMinor = String.valueOf(minor);
                Common.strTxPower = String.valueOf((int)txpw);
                if (major == Constant.MAX_TRIGER_1){
                    return Constant.BEACON_CORRUPTED;
                }
                return Constant.BEACON_FOUNDED;
            } else {
                return Constant.UNKNOWN_DEVICE_FOUNDED;
            }
        }

        return Constant.UNKNOWN_DEVICE_FOUNDED;
    }


    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
