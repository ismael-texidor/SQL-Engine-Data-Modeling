package project;
class Column {
    public String column_name;
    public String column_type;
    public int restriction;
    public Integer restriction_2 = null;
    public String is_null_allowed = "true";

    // column constructor with decimal restriction
    public Column(String name, String type, int restriction, int restriction_2, String is_null_allowed) {
        this.column_name = name;
        this.column_type = type;
        this.restriction = restriction;
        this.restriction_2 = restriction_2;
        this.is_null_allowed = is_null_allowed;
    }

    // toString
    public String toString() {
        if (this.restriction_2 == 0)
            return this.column_name + " " + this.column_type + " (" + this.restriction + ")";
        else
            return this.column_name + " " + this.column_type + " (" + this.restriction + "," + this.restriction_2 + ")";
    }
}