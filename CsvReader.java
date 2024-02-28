import java.io.RandomAccessFile;

public class CsvReader {
    private RandomAccessFile csv;

    public CsvReader(String path) throws Exception{
        csv = new RandomAccessFile(path, "r");
        csv.readLine();
    }

    public void loadFromCsv(FileManager fm) throws Exception{
        Produto p;
        int id = 1;
        while(csv.getFilePointer() < csv.length()){
            p = loadProduto();
            p.setId(id);
            fm.writeElement(p);
            id++;
        }
    }

    public Produto loadProduto() throws Exception{
        Produto p = new Produto();
        p.setAlive(true);
        p.setUrl(readString());
        p.setSku(readString());
        p.setName(readString());
        p.setDescription(readString());
        p.setPrice(Float.parseFloat(readString()));
        p.setCurrency(readString());
        p.setImages(readStringList());
        p.setDate(Util.getUTC(readString()));
        p.setTerms(readString());
        p.setSection(readString().equals("MAN"));
        p.setImageDownloads(readStringList());
        
        return p;
    }

    public String readString() throws Exception{
        byte b = csv.readByte();
        byte endMark = (byte) ',';
        if(b == (byte) '"'){
            endMark = (byte) '"';
            b = (byte) csv.readByte();
        }
        String buffer = "";
        while(b != endMark){
            if(b!='\n' && b!=13) buffer += (char) b;
            b = (byte) csv.readByte();
        }

        if(endMark == (byte) '"') csv.readByte();
        

        return buffer;
    }

    public String[] readStringList() throws Exception{
        csv.readByte();
        String buffer = "";
        byte b = (byte) csv.readByte();
        while(b!=(byte)']'){
            buffer += (char) b;
            b = (byte) csv.readByte();
        }

        String[] separated = buffer.split(",");

        for(int i=0;i<separated.length;i++){
            separated[i] = separated[i].substring(1);
        }
        

        for(int i=0;i<separated.length;i++){
            separated[i] = separated[i].substring(1, separated[i].length()-1);
        }

        csv.readByte();
        csv.readByte();

        return separated;
    }


    public void close() throws Exception{
        csv.close();
    }
    
}
