package utils;

/**
 *
 * @author descl
 */
public class Prop {
    private String type;
    private String value;
    private boolean isLiteral;

    public Prop(String type, String value) {
        this(type,value,true);
        
    }

    public Prop(String type, String value, boolean isLi){
        this.type = type;
        this.value = value;
        this.isLiteral = isLi;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isLiteral() {
        return isLiteral;
    }

    public void setLiteral(boolean isLiteral) {
        this.isLiteral = isLiteral;
    }
    
    @Override
    public String toString(){
        return "Prop("+this.getType()+","+this.getValue()+","+this.isLiteral()+")";
    }
}
