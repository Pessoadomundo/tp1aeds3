import java.io.RandomAccessFile;
import java.util.function.Predicate; 

public class FileManager {
    private RandomAccessFile raf;

    public FileManager(){
        this.raf = null;
    }

    public FileManager(String path) throws Exception{
        this.start(path);
    }

    public void start(String path) throws Exception{
        this.raf = new RandomAccessFile(path, "rw");
        raf.setLength(0);
        raf.seek(0);
        raf.writeInt(0);
    }

    public void loadFile(String path) throws Exception{
        this.raf = new RandomAccessFile(path, "rw");
    }

    public void writeBytes(byte[] bArr) throws Exception{
        raf.write(bArr);
    }

    public byte[] readBytes(int len) throws Exception{
        byte[] bArr = new byte[len];
        raf.read(bArr);
        return bArr;
    }

    public Produto readElement() throws Exception{
        raf.readByte();
        int len = raf.readInt();
        raf.seek(raf.getFilePointer()-5);
        byte[] bArr = new byte[len+5];
        raf.read(bArr);
        Produto p = new Produto(bArr);
        return p;
    }

    public void writeElement(Produto p) throws Exception{
        raf.seek(0);
        int lastId = raf.readInt();
        
        p.setId(lastId+1);

        raf.seek(raf.length());
        raf.write(p.toByteArray());

        raf.seek(0);
        raf.writeInt(lastId+1);
    }

    public Produto readElement(int id) throws Exception{
        raf.seek(0);
        raf.readInt();
        while(raf.getFilePointer() < raf.length()){
            Produto p = readElement();
            if(p.getId() == id && p.getAlive()){
                return p;
            }
        }

        return null;
    }

    public void close() throws Exception{
        raf.close();
    }

    public boolean deleteElement(int id) throws Exception{
        raf.seek(0);
        raf.readInt();
        long pos = 0;
        while(raf.getFilePointer() < raf.length()){
            pos = raf.getFilePointer();
            Produto p = readElement();
            if(p.getId() == id){
                p.setAlive(false);
                raf.seek(pos);
                raf.write(p.toByteArray());
                return true;
            }
        }

        return false;
    }

    public boolean updateElement(Produto p) throws Exception{
        raf.seek(0);
        raf.readInt();
        long pos = 0;

        while(raf.getFilePointer() < raf.length()){
            pos = raf.getFilePointer();
            Produto p2 = readElement();
            if(p2.getId() == p.getId() && p2.getAlive()){
                byte[] bArr = p.toByteArray();
                int len = bArr.length;
                raf.seek(pos+1);
                int len2 = raf.readInt()+5;
                if(len <= len2){
                    raf.seek(pos);
                    raf.write(bArr);
                    raf.seek(pos+1);
                    raf.writeInt(len2-5);
                }else{
                    raf.seek(pos);
                    raf.writeByte((byte)'*');
                    raf.seek(raf.length());
                    raf.write(bArr);
                }

                return true;
            }
        }

        return false;
    }

    public void loadFromCsv(String path) throws Exception{
        RandomAccessFile csv = new RandomAccessFile(path, "r");
        csv.seek(0);
        Produto p = new Produto();
        byte b = (byte) csv.readByte();
        String buffer = ""+b;
        while(csv.getFilePointer() < csv.length()){
            while(b!=(byte)','){
                b = (byte) csv.readByte();
                buffer += (char) b;
            }
            p.setUrl(buffer);
            buffer = "";

            b = (byte) csv.readByte();
            while(b!=(byte)','){
                b = (byte) csv.readByte();
                buffer += (char) b;
            }
            p.setSku(buffer);
            buffer = "";

        }
        


        csv.close();
    }




    public Produto[] conditionalSearch(Predicate<Produto> condition, int max) throws Exception{
        raf.seek(0);
        raf.readInt();
        Produto[] res = new Produto[max];
        int count = 0;
        while(raf.getFilePointer() < raf.length() && count < max){
            Produto p = readElement();
            if(condition.test(p)){
                res[count] = p;
                count++;
            }
        }

        return res;
    }

    public void resetPosition() throws Exception{
        raf.seek(0);
        raf.readInt();
    }

    public Produto[] readNext(int n){
        Produto[] res = new Produto[n];
        for(int i=0;i<n;i++){
            try{
                res[i] = readElement();
            }catch(Exception e){
                res[i] = null;
            }
        }

        return res;
    }

    public boolean hasNext() throws Exception{
        return raf.getFilePointer() < raf.length();
    }

    public int getProductAmount() throws Exception{
        int amount = 0;
        resetPosition();
        while(hasNext()){
            readElement();
            amount++;
        }

        return amount;
    }


}
