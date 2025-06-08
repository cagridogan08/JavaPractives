package Base;

import Designer.DesignComponent;

import java.awt.*;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;

public class Property {

    public String name;
    public Class<?> type;
    public Object value;
    public Object newValue;
    public boolean readOnly;
    public boolean isChanged;
    public boolean achanged;
    public Component comp;

    public PropertyDescriptor propertyDescriptor;
    public EventSetDescriptor eventSetDescriptor;

    public boolean isReplaced;
    public boolean isLayoutProperty;

    public Property(PropertyDescriptor propertyDescriptor, Object value) {

        this.propertyDescriptor = propertyDescriptor;
        this.value = value;
        this.name = propertyDescriptor.getName();
        this.type = propertyDescriptor.getPropertyType();


        this.newValue = value;
        this.readOnly = propertyDescriptor.getWriteMethod() == null;
        this.isChanged = false;
        this.isReplaced = false;
        this.isLayoutProperty = false;
        this.comp = null;
        this.achanged = false;
        this.eventSetDescriptor = null;
    }

    public Property(String name, Class<?> type, Object value,boolean readOnly,boolean isLayoutProperty) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.readOnly = readOnly;
        this.isChanged = false;
        this.isReplaced = false;
        this.isLayoutProperty = isLayoutProperty;
        this.comp = null;
        this.achanged = false;
        this.eventSetDescriptor = null;
        this.propertyDescriptor = null;
    }

}
