import java.time.format.DateTimeFormatter;

public class Util {
    public static byte[] getByteArray(float f){
        int bits = Float.floatToIntBits(f);
        byte[] bytes = new byte[] {
            (byte)(bits >> 24), 
            (byte)(bits >> 16),
            (byte)(bits >> 8),
            (byte) bits};
        
        return bytes;
    }

    public static byte[] getByteArray(String str){
        byte[] bArr = str.getBytes();
        short len = (short) bArr.length;

        byte[] res = new byte[bArr.length + 2];
        res[0] = (byte) (len>>8);
        res[1] = (byte) len;

        for(int i=0;i<bArr.length;i++){
            res[i+2] = bArr[i];
        }

        return res;
    }

    public static byte[] getLongByteArray(String str){
        byte[] bArr = str.getBytes();
        int len = bArr.length;

        byte[] res = new byte[bArr.length + 4];
        res[0] = (byte) (len>>24);
        res[1] = (byte) (len>>16);
        res[2] = (byte) (len>>8);
        res[3] = (byte) len;

        for(int i=0;i<bArr.length;i++){
            res[i+4] = bArr[i];
        }

        return res;
    }

    public static byte[] getByteArray(String str, int len){
        byte[] bArr = str.getBytes();
        byte[] res = new byte[len];
        for(int i=0;i<bArr.length;i++){
            if(i>=len) break;
            res[i] = bArr[i]; 
        }

        return res;
    }

    public static byte[] getByteArray(long l){
        byte[] res = new byte[8];
        for(int i=7;i>=0;i--){
            res[i] = (byte) l;
            l = l>>8;
        }

        return res;
    }

    public static byte[] getByteArray(int n){
        byte[] res = new byte[4];
        for(int i=3;i>=0;i--){
            res[i] = (byte) n;
            n = n>>8;
        }

        return res;
    }

    public static byte[] getByteArray(boolean b, byte option1, byte option2){
        byte[] res = new byte[1];
        res[0] = b?option1:option2;

        return res;
    }

    public static String combineStrings(String[] strs){
        String res = "";
        for(int i=0;i<strs.length;i++){
            res += strs[i];
            if(i<strs.length-1) res+=';';
        }

        return res;
    }

    public static byte[] combineByteArrays(byte[]... byteArrs){
        int len = 0;
        for(byte[] ba:byteArrs){
            len += ba.length;
        }

        byte[] res = new byte[len];

        int k=0;
        for(int i=0;i<byteArrs.length;i++){
            for(int j=0;j<byteArrs[i].length;j++){
                res[k] = byteArrs[i][j];
                k++;
            }
        }

        return res;
    }

    public static long getUTC(String str){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        return java.time.LocalDateTime.parse(str, formatter).toEpochSecond(java.time.ZoneOffset.UTC);
    }

}
