package utils;

import java.util.*;
import java.util.List;

public class Graph<NodeType> {
    private final Matrix<Boolean> edgeMatrix;
    private final TreeSet<Node<NodeType>> nodeSet;
    private final HashMap<Node<NodeType>, Integer> index;
    private int count;

    public Graph() {
        edgeMatrix = new Matrix<>();
        nodeSet = new TreeSet<>(Comparator.comparingInt(Node::hashCode));
        index = new HashMap<>();
        count = 1;
    }

    public Graph(Graph<NodeType> nodeTypeGraph) {
        if (nodeTypeGraph == null) {
            this.edgeMatrix = new Matrix<>();
            this.nodeSet = new TreeSet<>(Comparator.comparingInt(node -> node.digit));
            this.index = new HashMap<>();
            this.count = 1;
            return;
        }
        this.edgeMatrix = new Matrix<>(nodeTypeGraph.edgeMatrix);
        this.nodeSet = new TreeSet<>(nodeTypeGraph.nodeSet);
        this.index = new HashMap<>(nodeTypeGraph.index);
        this.count = nodeTypeGraph.count;
    }

    public void addNode(NodeType context) {
        Node<NodeType> newNode = new Node<>(context);
        if (!index.containsKey(newNode)) {
            nodeSet.add(newNode);
            edgeMatrix.expand(1, false);
            index.put(newNode, count);
            count++;
        }
    }

    public void removeNode(NodeType context) {
        Node<NodeType> node = getNode(context);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    public void removeNode(Node<NodeType> node) {
        // remove all the edge attached to the node
        for (Node<NodeType> otherNode : nodeSet) {
            if (otherNode != node) {
                removeDoubleWayEdge(node, otherNode);
            }
        }
        // remove the row and col in adjacency matrix
        edgeMatrix.removeRow(index.get(node));
        edgeMatrix.removeCol(index.get(node));
        // remove the node
        nodeSet.remove(node);
        // reset the index count
        for (Node<NodeType> nodeIndex : index.keySet()) {
            if (index.get(nodeIndex) > index.get(node)) {
                index.replace(nodeIndex, index.get(nodeIndex) - 1);
            }
        }
        count--;
        // remove the mapping of instance to index in matrix
        index.remove(node);
    }

    public boolean contains(NodeType context) {
        for (Node<NodeType> node : nodeSet) {
            if (node.getContext().equals(context)) {
                return true;
            }
        }
        return false;
    }

    public Node<NodeType> getNode(NodeType context) {
        for (Node<NodeType> node : nodeSet) {
            if (node.getContext().equals(context)) {
                return node;
            }
        }
        return null;
    }

    public TreeSet<Node<NodeType>> getNodeSet() {
        return nodeSet;
    }

    public void addOneWayEdge(Node<NodeType> source, Node<NodeType> target) {
        if (!edgeMatrix.get(index.get(source), index.get(target))) {
            edgeMatrix.replace(index.get(source), index.get(target), true);
            source.setOutDigit(source.outDigit + 1);
            target.setInDigit(target.inDigit + 1);
        }
    }

    public void addOneWayEdge(NodeType from, NodeType to) {
        Node<NodeType> source = getNode(from);
        Node<NodeType> target = getNode(to);
        if (source != null && target != null) {
            addOneWayEdge(source, target);
        }
    }

    public void addDoubleWayEdge(Node<NodeType> a, Node<NodeType> b) {
        addOneWayEdge(a, b);
        addOneWayEdge(b, a);
    }

    public void addDoubleWayEdge(NodeType a, NodeType b) {
        Node<NodeType> a1 = getNode(a);
        Node<NodeType> b1 = getNode(b);
        if (a1 != null && b1 != null) {
            addDoubleWayEdge(a1, b1);
        }
    }

    public void removeOneWayEdge(Node<NodeType> source, Node<NodeType> target) {
        if (edgeMatrix.get(index.get(source), index.get(target))) {
            edgeMatrix.replace(index.get(source), index.get(target), false);
            source.setOutDigit(source.outDigit - 1);
            target.setInDigit(target.inDigit - 1);
        }
    }

    public void removeOneWayEdge(NodeType from, NodeType to) {
        Node<NodeType> source = getNode(from);
        Node<NodeType> target = getNode(to);
        if (source != null && target != null) {
            removeOneWayEdge(source, target);
        }
    }

    public void removeDoubleWayEdge(Node<NodeType> a, Node<NodeType> b) {
        removeOneWayEdge(a, b);
        removeOneWayEdge(b, a);
    }

    public void removeDoubleWayEdge(NodeType a, NodeType b) {
        Node<NodeType> a1 = getNode(a);
        Node<NodeType> b1 = getNode(b);
        if (a1 != null && b1 != null) {
            removeDoubleWayEdge(a1, b1);
        }
    }

    public boolean hasRelationship(Node<NodeType> a, Node<NodeType> b) {
        return edgeMatrix.get(index.get(a), index.get(b)) || edgeMatrix.get(index.get(b), index.get(a));
    }

    public ArrayList<Node<NodeType>> getRelatedNodes(Node<NodeType> node) {
        ArrayList<Node<NodeType>> relatedNodes = new ArrayList<>();
        for (Node<NodeType> otherNode : nodeSet) {
            if (hasRelationship(node, otherNode)) {
                relatedNodes.add(otherNode);
            }
        }
        return relatedNodes;
    }

    public Node<NodeType> getSmallestDigitNode() {
        int digit = nodeNum() + 1;
        Node<NodeType> smallestDigitNode = null;
        for (Node<NodeType> node : nodeSet) {
            if (node.getDigit() < digit) {
                smallestDigitNode = node;
                digit = node.getDigit();
            }
        }
        return smallestDigitNode;
    }

    public boolean isEmpty() {
        return nodeSet.isEmpty();
    }

    public int nodeNum() {
        return nodeSet.size();
    }

    public int getDigit(Node<NodeType> node) {
        return node.getDigit();
    }

    public List<Node<NodeType>> getKey(HashMap<Node<NodeType>, Integer> map, Integer value){
        List<Node<NodeType>> keyList = new ArrayList<>();
        for(Node<NodeType> key: map.keySet()){
            if(map.containsKey(key) && map.get(key).equals(value)){
                keyList.add(key);
            }
        }
        return keyList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("nodes:\n");
        List<Map.Entry<Node<NodeType>, Integer>> mapList = new ArrayList<>(index.entrySet());
        mapList.sort(Map.Entry.comparingByValue());
        for (Map.Entry<Node<NodeType>, Integer> node : mapList) {
            builder.append(node.getKey().toString()).append("\n");
        }
        builder.append("\nadjacency matrix:\n").append(edgeMatrix);
        return builder.toString();
    }

    public static class Node<NodeType> {
        private NodeType context;
        private int digit, inDigit, outDigit;

        public Node(NodeType context) {
            this.context = context;
            this.digit = 0;
            this.inDigit = 0;
            this.outDigit = 0;
        }

        public void setContext(NodeType context) {
            this.context = context;
        }

        public NodeType getContext() {
            return context;
        }

        public int getDigit() {
            return digit / 2;
        }

        public int getInDigit() {
            return inDigit;
        }

        private void setInDigit(int inDigit) {
            this.digit = this.digit - this.inDigit + inDigit;
            this.inDigit = inDigit;
        }

        public int getOutDigit() {
            return outDigit;
        }

        private void setOutDigit(int outDigit) {
            this.digit = this.digit - this.outDigit + outDigit;
            this.outDigit = outDigit;
        }
        @Override
        public String toString() {
            return context.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return context.equals(node.context);
        }

        @Override
        public int hashCode() {
            return context.hashCode();
        }
    }
}
