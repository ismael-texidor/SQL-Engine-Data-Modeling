package project;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Stack;

class Record {
    public String record_date;
    public Stack<String> wareHouseRecords = new Stack<String>();      // -- s
    public ArrayList<String> cells = new ArrayList<String>();

    // record constructor
    public Record() {
        this.record_date = (new Date()).toString();
    }

    // constructor for loading database records
    public Record(String date, ArrayList<String> tuples) {
        record_date = (new Date()).toString();
        cells = tuples;
        wareHouseRecords.push(record_date);      // -- s
    }



    // toString
    public String toString() {
        if (this.cells.size() == 0)
            return "";
        else {
            // enumerate through all items of the record, display them
            String it = "";
            for (int i = 0; i < this.cells.size(); i++) {
                if (i != 0)
                    it += ", ";
                it += this.cells.get(i);
            }
            return it;
        }
    }
}