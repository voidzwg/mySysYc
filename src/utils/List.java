package utils;

import java.util.Collection;
import java.util.LinkedList;

public class List<NodeType, ListType> extends LinkedList<NodeType> {
    private ListType value;

    public List(ListType value) {
        this.value = value;
    }

    public List(Collection<? extends NodeType> collection, ListType value) {
        super(collection);
        this.value = value;
    }

    public ListType getValue() {
        return value;
    }

    public void setValue(ListType value) {
        this.value = value;
    }
}
