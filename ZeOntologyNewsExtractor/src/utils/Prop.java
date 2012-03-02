package utils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Prop {
    private Property type=null;
    private String value=null;
    private boolean isLiteral;

    public Prop(Property t, String value){
        this(t,value,true);
    }

    public Prop(String type, String value) {
        this(type,value,true);
    }

    public Prop(String t, String value, boolean isLi){
        this(ResourceFactory.createProperty(t),value,isLi);
    }
    
    public Prop(Property t, String value, boolean isLi){
        this.type = t;
        this.value= value;
        this.isLiteral = isLi;
    }
    
    public Property getType() {
        return type;
    }

    public void setType(Property type) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Prop other = (Prop) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        if (this.isLiteral != other.isLiteral) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 31 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 31 * hash + (this.isLiteral ? 1 : 0);
        return hash;
    }

}
