package project.struc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by spencesouthard on 4/11/16.
 */
public class Rec {

    //Dec
    private ArrayList<DataEntry> entries = new ArrayList<>();

    public Rec(){

    }

    public Rec(String data){
        addEntry(new DataEntry(data));
    }

    public Rec(String data, String timestamp){
        addEntry(data, timestamp);
    }

    Rec(HashMap<String, String> dataArrayList) {
        addEntry(dataArrayList);
    }
    

    /** To be used for SELECT */
    public DataEntry getLastEntry(){
        return entries.get(entries.size()-1);
    }

    public void updateLastEntry(String value){
        entries.get(entries.size()-1).setData(value);
    }

    /** To be used for wSELECT */
    public ArrayList<DataEntry> getAllEntries(){
        return entries;
    }

    private void addEntry(DataEntry entry){
        entries.add(entry);
    }

    public void addEntry(String data){
        entries.add(new DataEntry(data));
    }

    private void addEntry(String data, String timestamp){
        entries.add(new DataEntry(data, timestamp));
    }

    private void addEntry(HashMap<String, String> dataArrayList) 
    {
        for(Entry<String, String> entry: dataArrayList.entrySet() )
        {
            entries.add(new DataEntry(entry.getKey(), entry.getValue()));
        }
    }
    public int size(){
        return entries.size();
    }

}
