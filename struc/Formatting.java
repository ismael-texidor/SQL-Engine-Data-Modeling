package project.struc;

import java.util.*;
import java.text.*;

public abstract class Formatting{
  private int maxLength;
  Formatting(){
    maxLength = 25;
  }
  public void setMaxLength(int maxLength){
    this.maxLength = maxLength;
  }
  public String toString(){
    return "";
  }
  public String toStringW(){
    return "";
  }
}
