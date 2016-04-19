package project.struc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Condition;

/**
 * Created by spencesouthard on 4/11/16.
 */
public class Relation {

    //Decs
    private String name;
    private ArrayList<Col> columns = new ArrayList<>();

    public Relation(){

    }

    public Relation(String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void insertColumn(Col record){
        columns.add(record);
    }

    public void insertRecordsIntoColumns(ArrayList<Rec> records){
        for(int i = 0; i < this.columns.size(); i++){
           this.columns.get(i).addRec(records.get(i));
        }
    }

    public void insert(ArrayList<String> values){

        if(columns.size() != values.size()){
            System.out.println("Mismatch between number of columns and values to be inserted");
        }

        for(int i = 0; i < values.size(); i++){
            columns.get(i).addRec(values.get(i));
        }

    }

    public void insert(ArrayList<String> params, ArrayList<String> values){

        if(columns.size() != values.size()){
            System.out.println("Mismatch between number of columns and values to be inserted");
        }

        for(int i = 0; i < params.size(); i++){

            Col col = getColumnByName(params.get(i));

            col.addRec(values.get(i));

        }

    }

    public Col getColumnByName(String name){
        for(Col col : columns){
            if(col.getName().equals(name)){
                return col;
            }
        }

        return null;
    }

    public ArrayList<Col> getColumns ()
    {
        return columns;
    }
    public void update(String param, String value, ArrayList<String> conditions){

        ArrayList<Col> tempColumns = new ArrayList<>(columns);
        HashSet<Integer> recordsToUpdate = new HashSet<>();
        int numOfRecords = columns.get(0).size();

        // Finds out which records need to be updated
        for (int i = 0; i < numOfRecords; i++) {
            for (Col column : tempColumns) {
                if (predicate(conditions, column.getName(), column.getRec(i).getLastEntry().getData(), false)) {
                    recordsToUpdate.add(i);
                }
            }
        }

        // Updates records
        for(int i = 0; i < numOfRecords; i++){

            for(Col column : tempColumns){

                if(recordsToUpdate.contains(i) && column.getName().equals(param)) {

                    // Update here
                    column.getRec(i).getLastEntry().setData(value);

                }
            }

            System.out.println();
        }
    }

    public boolean delete(ArrayList<String> conditions){

        if(conditions.size() == 0){

            System.out.println();
            int numOfRecords = columns.get(0).size();

            for(Col column : columns){
                column.clear();
            }

        }
        else if(conditions.size() > 0){

            ArrayList<Col> tempColumns = new ArrayList<>(columns);
            HashSet<Integer> recordsToDelete = new HashSet<>();
            int numOfRecords = columns.get(0).size();

            for (int i = 0; i < numOfRecords; i++) {
                for (Col column : tempColumns) {
                    if (predicate(conditions, column.getName(), column.getRec(i).getLastEntry().getData(), false)) {
                        recordsToDelete.add(i);
                    }
                }
            }

            System.out.println(recordsToDelete);

            for(int i = 0; i < numOfRecords; i++){

                for(Col column : tempColumns){

                    if(recordsToDelete.contains(i)){
                        column.deleteRec(i);
                    }

                }
            }

        }
        else{
            System.out.println("Unexpected scenario occurred");
            System.exit(2);
        }



        return false;
    }

    public Relation select(ArrayList<String> params, ArrayList<String> conditions){

        Relation derivedRelation = new Relation();

        if(params.size() == 0 && conditions.size() == 0){

            // Get header data from columns
            for(Col column : columns){
                System.out.print(column.getName() + "\t");
            }

            System.out.println();
            int numOfRecords = columns.get(0).size();

            for(int i = 0; i < numOfRecords; i++){
                for(Col column : columns){

                    System.out.print(column.getRec(i).getLastEntry().getData() + "\t");
                }

                System.out.println();
            }

            // Clean columns then add them to new derived table
            for(Col col : columns){
                Col c = new Col();
                c.copyAttributes(col);
                derivedRelation.insertColumn(c);
            }

        }
        else if(params.size() > 0 && conditions.size() == 0){

            // Get header data from columns
            for(Col column : columns){

                if(!contains(params, column.getName()))
                    continue;

                System.out.print(column.getName() + "\t");
            }

            System.out.println();
            int numOfRecords = columns.get(0).size();

            for(int i = 0; i < numOfRecords; i++){
                for(Col column : columns){

                    if(!contains(params, column.getName()))
                        continue;

                    if(i == 1){
                        derivedRelation.insertColumn(column);
                    }

                    System.out.print(column.getRec(i).getLastEntry().getData() + "\t");
                }

                System.out.println();
            }

            // Clean columns then add them to new derived table
            for(Col col : columns){
                Col c = new Col();
                c.copyAttributes(col);
                derivedRelation.insertColumn(c);
            }

            // Add records to new table columns
            for(int i = 0; i < columns.get(0).size(); i++){
                derivedRelation.insertRecordsIntoColumns(getRecordsByRowIndex(i));
            }

            ArrayList<String> columnsToDelete = new ArrayList<>();

            // Delete any column that is not in the params
            for(int i = 0; i < derivedRelation.getColumnSize(); i++){
                if(!contains(params, derivedRelation.columns.get(i).getName())){
                    columnsToDelete.add(derivedRelation.columns.get(i).getName());
                }
            }

            for(String name : columnsToDelete){
                derivedRelation.deleteColumnByName(name);
            }

        }
        else if(params.size() == 0 && conditions.size() > 0){

            HashSet<Integer> recordsToRemove = new HashSet<>();
            int numOfRecords = columns.get(0).size();

            // Find rows that do not meet the WHERE condition
            for (int i = 0; i < numOfRecords; i++) {
                for (Col column : columns) {
                    if (!predicate(conditions, column.getName(), column.getRec(i).getLastEntry().getData())) {
                        recordsToRemove.add(i);
                    }
                }
            }

            // Get header data from columns
            for(Col column : columns){

                System.out.print(column.getName() + "\t");
            }

            System.out.println();
            boolean addedColumn = false;

            // Print out rows that do meet the WHERE conditions
            for(int i = 0; i < numOfRecords; i++){

                for(Col column : columns){

                    if(recordsToRemove.contains(i)){
                        continue;
                    }

                    System.out.print(column.getRec(i).getLastEntry().getData() + "\t");
                }

                System.out.println();
            }

            // Clean columns then add them to new derived table
            ArrayList<Col> tempColumns = new ArrayList<>();
            for(Col col : columns){
                Col c = new Col();
                c.copyAttributes(col);
                derivedRelation.insertColumn(c);
            }

            // Add records to new table columns
            for(int i = 0; i < columns.get(0).size(); i++){

                if(!recordsToRemove.contains(i)){
                    derivedRelation.insertRecordsIntoColumns(getRecordsByRowIndex(i));
                }

            }

        }
        else if(params.size() > 0 && conditions.size() > 0){

                HashSet<Integer> recordsToRemove = new HashSet<>();
                int numOfRecords = columns.get(0).size();

                for (int i = 0; i < numOfRecords; i++) {
                    for (Col column : columns) {
                        if (!predicate(conditions, column.getName(), column.getRec(i).getLastEntry().getData())) {
                            recordsToRemove.add(i);
                        }
                    }
                }

                // Get header data from columns
                for(Col column : columns){

                    if(!contains(params, column.getName()))
                        continue;

                    System.out.print(column.getName() + "\t");
                }

                System.out.println();

                // Print out rows that do not meet the where condition
                for(int i = 0; i < numOfRecords; i++){

                    for(Col column : columns){

                        if(!contains(params, column.getName()))
                            continue;

                        if(recordsToRemove.contains(i)){
                            continue;
                        }

                        System.out.print(column.getRec(i).getLastEntry().getData() + "\t");
                    }

                    System.out.println();
                }

                // Clean columns then add them to new derived table
                ArrayList<Col> tempColumns = new ArrayList<>();
                for(Col col : columns){
                    Col c = new Col();
                    c.copyAttributes(col);
                    derivedRelation.insertColumn(c);
                }

                // Add records to new table columns
                for(int i = 0; i < columns.get(0).size(); i++){

                    if(!recordsToRemove.contains(i)){
                        derivedRelation.insertRecordsIntoColumns(getRecordsByRowIndex(i));
                    }

                }

                ArrayList<String> columnsToDelete = new ArrayList<>();

                // Delete any column that is not in the params
                for(int i = 0; i < derivedRelation.getColumnSize(); i++){
                    if(!contains(params, derivedRelation.columns.get(i).getName())){
                        columnsToDelete.add(derivedRelation.columns.get(i).getName());
                    }
                }

                for(String name : columnsToDelete){
                    derivedRelation.deleteColumnByName(name);
                }

        }
        else{
            System.out.println("Unexpected scenario occurred");
            System.exit(2);
        }



        return derivedRelation;
    }

    public void deleteColumnByName(String name){
        for(int i = 0; i < columns.size(); i++){
            if(columns.get(i).getName().equals(name)){
                columns.remove(i);
                return;
            }
        }
    }

    private boolean predicate(ArrayList<String> conditions, String name, String value){
        return predicate(conditions, name, value, true);
    }

    private boolean predicate(ArrayList<String> conditions, String name, String value, boolean defaults){

        for(String condition : conditions){
            String[] split = condition.split(" ");
            String term = split[0];
            String operator = split[1];
            String requirement = split[2];

            // This is the column that we want to compare
            if(term.equals(name)){

                if(operator.equals("=")){
                    return requirement.equals(value);
                }
                // Must be integers or numbers
                else if(operator.equals(">")){
                    int a = Integer.parseInt(value);
                    int b = Integer.parseInt(requirement);

                    return a > b;
                }
                else if(operator.equals("<")){

                    int a = Integer.parseInt(value);
                    int b = Integer.parseInt(requirement);

                    return a < b;

                }
                else if(operator.equals("!=")){

                    return !requirement.equals(value);
                }
                else{
                    System.out.println("Relation.predicate: Unexpected scenario occurred");
                    System.exit(2);
                }

            }

        }

        return defaults;
    }

    private boolean contains(ArrayList<String> list, String term){
        for(String str : list){
            if (str.equals(term)){
                return true;
            }
        }

        return false;
    }

    public void wUpdate(String param, String value, ArrayList<String> conditions){

        ArrayList<Col> tempColumns = new ArrayList<>(columns);
        HashSet<Integer> recordsToUpdate = new HashSet<>();
        int numOfRecords = columns.get(0).size();

        // Finds out which records need to be updated
        for (int i = 0; i < numOfRecords; i++) {
            for (Col column : tempColumns) {
                if (predicate(conditions, column.getName(), column.getRec(i).getLastEntry().getData(), false)) {
                    recordsToUpdate.add(i);
                }
            }
        }

        // Updates records
        for(int i = 0; i < numOfRecords; i++){

            for(Col column : tempColumns){

                if(recordsToUpdate.contains(i) && column.getName().equals(param)) {

                    // Update here
                    column.getRec(i).addEntry(value);

                }
            }

            System.out.println();
        }
    }

    public boolean wSelect(ArrayList<String> params, ArrayList<String> conditions){

        if(params.size() == 0 && conditions.size() == 0){

            // Get header data from columns
            for(Col column : columns){
                System.out.print(column.getName() + "\t");
            }

            System.out.println();
            int numOfRecords = columns.get(0).size();

            for(int i = 0; i < numOfRecords; i++){
                for(Col column : columns){

                    ArrayList<DataEntry> entries = column.getRec(i).getAllEntries();

                    for(DataEntry entry : entries){
                        System.out.print(entry.getData() + "\t" + entry.getTimeStamp() + "\t");
                    }
                }

                System.out.println();
            }

        }
        else if(params.size() > 0 && conditions.size() == 0){

            // Get header data from columns
            for(Col column : columns){

                if(!contains(params, column.getName()))
                    continue;

                System.out.print(column.getName() + "\t");
            }

            System.out.println();
            int numOfRecords = columns.get(0).size();

            for(int i = 0; i < numOfRecords; i++){
                for(Col column : columns){

                    if(!contains(params, column.getName()))
                        continue;

                    ArrayList<DataEntry> entries = column.getRec(i).getAllEntries();

                    for(DataEntry entry : entries){
                        System.out.print(entry.getData() + "\t" + entry.getTimeStamp() + "\t");
                    }
                }

                System.out.println();
            }

        }
        else if(params.size() == 0 && conditions.size() > 0){

            ArrayList<Col> tempColumns = new ArrayList<>(columns);
            HashSet<Integer> recordsToRemove = new HashSet<>();
            int numOfRecords = columns.get(0).size();

            for (int i = 0; i < numOfRecords; i++) {
                for (Col column : tempColumns) {
                    if (!predicate(conditions, column.getName(), column.getRec(i).getLastEntry().getData())) {
                        recordsToRemove.add(i);
                    }
                }
            }

            // Get header data from columns
            for(Col column : tempColumns){

                System.out.print(column.getName() + "\t");
            }

            System.out.println();

            for(int i = 0; i < numOfRecords; i++){

                for(Col column : tempColumns){

                    if(recordsToRemove.contains(i)){
                        continue;
                    }

                    System.out.print(column.getRec(i).getLastEntry().getData() + "\t");
                }

                System.out.println();
            }

        }
        else if(params.size() > 0 && conditions.size() > 0){

            ArrayList<Col> tempColumns = new ArrayList<>(columns);
            HashSet<Integer> recordsToRemove = new HashSet<>();
            int numOfRecords = columns.get(0).size();

            for (int i = 0; i < numOfRecords; i++) {
                for (Col column : tempColumns) {
                    if (!predicate(conditions, column.getName(), column.getRec(i).getLastEntry().getData())) {
                        recordsToRemove.add(i);
                    }
                }
            }

            // Get header data from columns
            for(Col column : tempColumns){

                if(!contains(params, column.getName()))
                    continue;

                System.out.print(column.getName() + "\t");
            }

            System.out.println();

            for(int i = 0; i < numOfRecords; i++){

                for(Col column : tempColumns){

                    if(!contains(params, column.getName()))
                        continue;

                    if(recordsToRemove.contains(i)){
                        continue;
                    }

                    System.out.print(column.getRec(i).getLastEntry().getData() + "\t");
                }

                System.out.println();
            }

        }
        else{
            System.out.println("Unexpected scenario occurred");
            System.exit(2);
        }


        return false;
    }

    /** Type is either a string or not a string.  Just because. */
    public Relation group(ArrayList<String> params, ArrayList<String> aggregates, ArrayList<String> conditions, String groupBy, String type){

        //Get distinct group values
        HashSet<String> distinct = new HashSet<>();
        HashMap<String, Relation> tables = new HashMap<>();

        Col col = getColumnByName(groupBy);
        for(Rec rec : col.getRecs()){
            distinct.add(rec.getLastEntry().getData());
        }

        for(String d : distinct) {

            ArrayList<String> p = new ArrayList<>();
            ArrayList<String> c = new ArrayList<>();

            c.add(groupBy + " = " + d);

            Relation r = this.select(p, c);
            tables.put(d, r);
        }

        // Get aggregates for each parameter as necessary
        int newTableSize = tables.size();
        Relation table = new Relation();

        // Create columns for new table
        for(int i = 0; i < params.size(); i++){
            Col c = getColumnByName(params.get(i));

            String name = params.get(i);

            if(!aggregates.get(i).isEmpty()){
                name += "-" + aggregates.get(i);
            }

            table.insertColumn(new Col(name, c.getType(), c.getMaxLength(), c.getDecimalsAllowed(), false));
        }

        for(String d : distinct){

            Relation r = tables.get(d);
            ArrayList<String> values = new ArrayList<>();

            for(int i = 0; i < params.size(); i++){

                if(aggregates.get(i).isEmpty()){
                    values.add(r.getColumnByName(params.get(i)).getRec(i).getLastEntry().getData());
                }
                else{
                    if(aggregates.get(i).equals("avg")){
                        values.add(Double.toString(r.average(params.get(i))));
                    }
                    else if(aggregates.get(i).equals("count")){
                        ArrayList<String> c = new ArrayList<>();
                        c.add(groupBy + " = " + d);

                        ArrayList<String> p = new ArrayList<>();
                        p.add(params.get(i));

                        values.add(Integer.toString(r.count(p, c)));
                    }
                    else if(aggregates.get(i).equals("sum")){
                        ArrayList<String> c = new ArrayList<>();
                        c.add(groupBy + " = " + d);

                        ArrayList<String> p = new ArrayList<>();
                        p.add(params.get(i));

                        values.add(Double.toString(r.sum(p, c)));
                    }
                    else if(aggregates.get(i).equals("min")){
                        ArrayList<String> c = new ArrayList<>();
                        c.add(groupBy + " = " + d);

                        ArrayList<String> p = new ArrayList<>();
                        p.add(params.get(i));

                        values.add(Double.toString(r.min(p, c)));
                    }
                    else if(aggregates.get(i).equals("max")){
                        ArrayList<String> c = new ArrayList<>();
                        c.add(groupBy + " = " + d);

                        ArrayList<String> p = new ArrayList<>();
                        p.add(params.get(i));

                        values.add(Double.toString(r.max(p, c)));

                    }
                    else{
                        System.out.println("Relation.group: Unexpected scenerio occurred");
                        System.exit(2);
                    }
                }

            }

            table.insert(values);
            values.clear();

        }

        return table;

    }

    private double max(ArrayList<String> params, ArrayList<String> conditions){

        double result = Double.MIN_VALUE;
        Relation r = select(params, conditions);

        for(Rec rec : r.columns.get(0).getRecs()){
            double value = Double.parseDouble(rec.getLastEntry().getData());

            if(value > result){
                result = value;
            }

        }

        return result;
    }

    private double min(ArrayList<String> params, ArrayList<String> conditions){

        double result = Double.MAX_VALUE;
        Relation r = select(params, conditions);

        for(Rec rec : r.columns.get(0).getRecs()){
            double value = Double.parseDouble(rec.getLastEntry().getData());

            if(value < result){
                result = value;
            }

        }

        return result;
    }

    private double sum(ArrayList<String> params, ArrayList<String> conditions){

        double result = 0;
        Relation r = select(params, conditions);

        for(Rec rec : r.columns.get(0).getRecs()){
            result += Double.parseDouble(rec.getLastEntry().getData());
        }

        return result;
    }

    private int count(ArrayList<String> params, ArrayList<String> conditions){

        HashSet<String> distinctValues = new HashSet<>();
        Relation r = select(params, conditions);

        for(Rec rec : r.columns.get(0).getRecs()){
            distinctValues.add(rec.getLastEntry().getData());
        }

        return distinctValues.size();
    }

    private double average(String field){
        ArrayList<Double> toBeAveraged = new ArrayList<>();

        for(Rec record : getColumnByName(field).getRecs()){
            toBeAveraged.add(Double.parseDouble(record.getLastEntry().getData()));
        }

        return calculateAverage(toBeAveraged);
    }

    private double calculateAverage(ArrayList <Double> marks) {

        double sum = 0;

        for (int i = 0; i< marks.size(); i++) {
            sum += marks.get(i);
        }

        return sum / marks.size();
    }

    public int getColumnSize(){
        return columns.size();
    }

    public int getRecordSize(){
        // Returns the number of records in column 0
        return columns.get(0).size();
    }

    public ArrayList<Rec> getRecordsByRowIndex(int index){
        ArrayList<Rec> values = new ArrayList<>();

        for(Col col : columns){
            values.add(col.getRec(index));
        }

        return values;
    }

    //Display Functions
    public void displayTable(ArrayList<String> headers,ArrayList<String[]> records){
      //Validation of Structure passed in 
      //Checks that each element has the same number of fields and finds each fields max size which
      //must be less than or equal to 25 else it will default to 25
      int[] colSize = new int[headers.size()];
      for(String[] rec : records){
        if(rec.length == headers.size()){
          for(int i = 0; i <  rec.length;i++){
            if(colSize[i] < rec[i].length()){
              if(rec[i].length() > 25){
                colSize[i] = 25;
              }
              else{
                colSize[i] = rec[i].length();
              }
            }
          }
        }
        else{
//          if(DEBUG_ON){
//            System.out.println("Error : Mismatching Header length with records");
//            DebugHandler.append("RelationErrorLog.txt", "Error : Mismatching Header length with records");
//          }
        }
      }
      String output = "";
      String divider = "\n";
      for(int i =0; i < headers.size();i++){
        output += String.format(" %"+colSize[i]+"s |",headers.get(i));
        for(int j = 0 ; j < colSize[i]; j++){
          divider += "-";
        }
        
      }
      divider += "\n";
      output += divider;
      for(String[] rec : records){
        for(int i =0; i < headers.size();i++){
          output += String.format(" %"+colSize[i]+"s |",rec[i]);
        }
        output += divider;
      }
      System.out.println(output);
    }

    /** NAGA: Table needs to be displayed on to string call */
    @Override
    public String toString(){
        return super.toString();
    }
}
