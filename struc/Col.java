package project.struc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by spencesouthard on 4/11/16.
 */
public class Col {

    //Decs
    private ArrayList<Rec> recs = new ArrayList<>();
    private String type = "varchar(255)";
    private boolean nullable;
    private int maxLength = 255;
    private int decimalsAllowed = 0;
    private String name;

    public Col(){

    }

    public Col(String type){
        this.type = type;
    }

    public Col(String type, int maxLength){
        this.type = type;
        this.maxLength = maxLength;
    }

    public Col(String type, int maxLength, boolean nullable){
        this.type = type;
        this.maxLength = maxLength;
        this.nullable = nullable;
    }

    public Col(String type, boolean nullable){
        this.type = type;
        this.nullable = nullable;
    }

    public Col(String name, String type, int maxLength, int decimalsAllowed, boolean nullable){
        this.name = name;
        this.type = type;
        this.maxLength = maxLength;
        this.decimalsAllowed = decimalsAllowed;
        this.nullable = nullable;
    }

    public void copyAttributes(Col col){
        this.type = col.getType();
        this.nullable = col.getNullable();
        this.maxLength = col.getMaxLength();
        this.decimalsAllowed = col.getDecimalsAllowed();
        this.name = col.getName();
    }

    public void copyAttributesAndRecords(Col col){
        this.type = col.getType();
        this.nullable = col.getNullable();
        this.maxLength = col.getMaxLength();
        this.decimalsAllowed = col.getDecimalsAllowed();
        this.name = col.getName();
        this.recs = col.getRecs();
    }

    public String getType(){
        return type;
    }

    public boolean getNullable(){
        return nullable;
    }

    public int size(){
        return recs.size();
    }

    public void addRec(Rec rec){
        recs.add(rec);
    }

    public void addRec(String data){
        recs.add(new Rec(data));
    }

    public void addRec(String data, String timestamp){
        recs.add(new Rec(data, timestamp));
    }

    public Rec getRec(int index){
        return recs.get(index);
    }

    public void addRec(HashMap<String, String> dataArrayList) {
        recs.add(new Rec(dataArrayList));
    }

    public void deleteRec(int index){
        recs.remove(index);
    }

    public ArrayList<Rec> getRecs(){
        return recs;
    }

    public String getName(){
        return name;
    }

    public void clear(){
        recs.clear();
    }

    public int getMaxLength(){
        return maxLength;
    }

    public int getDecimalsAllowed(){
        return decimalsAllowed;
    }

    @Override
    public String toString(){
        return getName() + ":" + getType();
    }

}
