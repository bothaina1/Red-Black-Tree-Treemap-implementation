package eg.edu.alexu.csd.filestructure.redblacktree.Trees;


import javax.management.RuntimeErrorException;
import java.util.*;

public class RedBlackTree<T extends Comparable<T>, V> implements IRedBlackTree<T, V> {
    private final INode<T, V> nil = new Node<>();
    private INode<T, V> root;
    private int size;
    private final Set<Map.Entry<T,V>> entries;

    public RedBlackTree() {
        root = null;
        entries = new LinkedHashSet<>();
    }


    @Override
    public INode<T,V> getRoot() {
        return this.root;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public void clear() {
        size = 0;
         this.root = null;
    }

    @Override
    public V search (T key) {
        if(root == null)
            return null;
        INode<T, V> node = search(root, key);
        if (node != null)
            return node.getValue();
        return null;
    }

    @Override
    public int getSize(){
        return size;
    }

    protected INode<T,V> search(INode<T, V> root, T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error("Can't find null key"));
        }
        if (root.isNull()){
            return null;
        }
        if (root.getKey().compareTo(key) == 0) {
            return root;
        } else {
            return root.getKey().compareTo(key) < 0 ? this.search(root.getRightChild(), key) : this.search(root.getLeftChild(), key);
        }
    }

    @Override
    public boolean contains(T key) {
        if(root == null)
            return false;
        return contains(root,key);
    }


    private boolean contains(INode<T,V> root, T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error("Tree doesn't contain null key"));
        }
        if (!root.isNull()) {
            if (root.getKey().compareTo(key) == 0) {
                return true;
            } else {
                return root.getKey().compareTo(key) < 0 ? this.contains(root.getRightChild(), key) : this.contains(root.getLeftChild(), key);
            }
        } else {
            return false;
        }
    }

    @Override
    public void insert(T key, V value) {
        if (key == null || value == null)
            throw new RuntimeErrorException(new Error("Can't insert null key"));
        INode<T, V> newNode = new Node<>(key, value, true);
        newNode.setRightChild(nil);
        newNode.setLeftChild(nil);
        if (root==null || root.isNull()) {
            root = null;
            root = newNode;
            root.setColor(false);
            size++;
            return;
        }

        if (insertNode(this.root, newNode))  { //BST Insertion
            insertCases(newNode);                        //Insertion Cases
            size++;
        }

    }

    /**
     * Insert new in Binary Search Tree
     *
     */
    private boolean insertNode(INode<T, V> root, INode<T, V> newNode) {
        try {
            if (newNode.getKey().compareTo(root.getKey()) > 0) {    //newNode > root
                if (root.getRightChild().isNull()) {
                    root.setRightChild(newNode);
                    newNode.setParent(root);
                    return true;
                }
                return insertNode(root.getRightChild(), newNode);
            } else if (newNode.getKey().compareTo(root.getKey()) < 0) { //newNode < root
                if (root.getLeftChild().isNull()) {
                    root.setLeftChild(newNode);
                    newNode.setParent(root);
                    return true;
                }
                return insertNode(root.getLeftChild(), newNode);
            } else {
                root.setValue(newNode.getValue());
                return false;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("can't compare different data types");
        }
    }

    private void insertCases(INode<T, V> newNode) {
        INode<T, V> parent = newNode.getParent();
        INode<T, V> grandParent = newNode.getParent().getParent();
        //Easy Case ,Parent is Black OR it does not have grandparent
        if (!newNode.getParent().getColor() || grandParent == null)
            return;
        boolean uncleColor = newNode.getUncle().getColor();
        // Right  --> True , Left --> false
        boolean parentDirection = false, childDirection = false;

        if (grandParent.isRightChild(parent))
            parentDirection = true;
        if (parent.isRightChild(newNode))
            childDirection = true;
        //Case 1: Uncle is red
        if (uncleColor) {
            recoloring(newNode);
            return;
        }
        if (parentDirection && childDirection)
            insertRightRight(newNode);
        else if (!parentDirection && childDirection)
            insertLeftRight(newNode);
        else if (parentDirection)
            insertRightLeft(newNode);
        else
            insertLeftLeft(newNode);
    }


    private void recoloring(INode<T, V> newNode) {
        INode<T, V> grandParent = newNode.getParent().getParent();
        grandParent.setColor(true);
        newNode.getParent().setColor(false);
        newNode.getUncle().setColor(false);
        if (grandParent.getParent() != null) {
            if (grandParent.getParent().getColor())
                insertCases(grandParent);
        } else {
            grandParent.setColor(false);
        }
    }

    //Case 3: Uncle is Black, Inserted eg.edu.alexu.csd.filestructure.redblacktree.Interfaces.Node is a left child
    /*
     * Parent is left , right rotate
     * Parent is right , left rotate
     */
    private void insertLeftLeft(INode<T, V> newNode) {
        newNode.getParent().setColor(false);
        newNode.getParent().getParent().setColor(true);
        rotateRight(newNode.getParent().getParent());
    }

    private void insertRightRight(INode<T, V> newNode) {
        newNode.getParent().setColor(false);
        newNode.getParent().getParent().setColor(true);
        rotateLeft(newNode.getParent().getParent());
    }
    //Case Left Right : Uncle is Black, Inserted eg.edu.alexu.csd.filestructure.redblacktree.Interfaces.Node is a right child and its parent is a left child
    /*
     * Parent is left , parent left rotate
     * Go to case Left Left
     */
    private void insertLeftRight(INode<T, V> newNode) {
        rotateLeft(newNode.getParent());
        insertLeftLeft(newNode.getLeftChild());
    }

    //Case Right Left : Uncle is Black, Inserted is a left child and its parent is a right child
    /*
     * Parent is right , parent right rotate
     * Go to Case Right Right
     */
    private void insertRightLeft(INode<T, V> newNode){
        rotateRight(newNode.getParent());
        insertRightRight(newNode.getRightChild());
    }


    @Override
    public boolean delete(T key) {
        //TODO sibling null ??
        if(key == null)
            throw new RuntimeErrorException(new Error("Can't delete null key"));
        if( delete( search(root,key))){
            size--;
            return true;
        }
        return false;
    }

    private boolean delete(INode<T, V> deletedNode) {
        if (deletedNode == null)
            return false;
        //TODO if parent is null (root case) //1
        if(deletedNode.getLeftChild().isNull() && deletedNode.getRightChild().isNull()){
            if(deletedNode == root)
            {
                root.setValue(null);
                root.setKey(null);
                root.setColor(false);
            }
            else {
                deletedNode.setKey(null);
                deletedNode.setValue(null);
                deletedNode.setLeftChild(null);
                deletedNode.setRightChild(null);
                if(!deletedNode.getColor()){
                    doubleBlack(deletedNode);
                }//TODO CHECK COLOR
                deletedNode.setColor(false);

            }

        }
        else if(deletedNode.getLeftChild().isNull()){
            INode<T,V> rightChild = deletedNode.getRightChild();
            if(!deletedNode.getColor())
                deletedNode.getRightChild().setColor(false);
            deletedNode.getRightChild().setParent(deletedNode.getParent());
            if(deletedNode.getParent() != null) {
                if(deletedNode.isChildLeft())
                    deletedNode.getParent().setLeftChild(rightChild);
                else deletedNode.getParent().setRightChild(rightChild);
            }
            else
                root = rightChild;
        }
        else if(deletedNode.getRightChild().isNull()){
            //TODO here
            INode<T,V> leftChild = deletedNode.getLeftChild();
            if(!deletedNode.getColor())
                deletedNode.getLeftChild().setColor(false);
            deletedNode.getLeftChild().setParent(deletedNode.getParent());
            if(deletedNode.getParent() != null) {
                if ( deletedNode.isChildLeft())
                    deletedNode.getParent().setLeftChild(leftChild);
                else deletedNode.getParent().setRightChild(leftChild);
            }
            else
                root = leftChild;
        }
        else {
            INode<T,V> predecessor = findMin(deletedNode.getRightChild());
//            INode<T,V> temp = new INode<>(predecessor.getKey(),predecessor.getValue());
            deletedNode.setValue(predecessor.getValue());
            deletedNode.setKey(predecessor.getKey());
            return delete(predecessor);
        }
        return true;
    }

    /**
     * Rotate subtree at a given node to the left
     * used in insertion and deletion
     */
    public void rotateLeft(INode<T, V> rotateNode) {
        INode<T, V> node = rotateNode.getRightChild();         //Right Child of The rotate node
        rotateNode.setRightChild(node.getLeftChild());
        //Check Whether the right node of the rotate node has left child or not
        if (!node.getLeftChild().isNull()) {
            node.getLeftChild().setParent(rotateNode);
        }
        node.setLeftChild(rotateNode);
        setParentRotation(node, rotateNode);
    }


    /**
     * Rotate subtree at a given node to the right
     * used in insertion and deletion
     */
    public void rotateRight(INode<T, V> rotateNode) {
        INode<T, V> node = rotateNode.getLeftChild();         //Left Child of The rotate node
        rotateNode.setLeftChild(node.getRightChild());
        //Check Whether the left node of the rotate node has right child or not
        if (!node.getRightChild().isNull()) {
            node.getRightChild().setParent(rotateNode);
        }
        node.setRightChild(rotateNode);
        setParentRotation(node, rotateNode);
    }

    private void setParentRotation(INode<T, V> node, INode<T, V> rotateNode) {
        node.setParent(rotateNode.getParent());
        /* Check if the given node to be rotated has parent or not
         * if it has parent , then set the parent child to node
         * it checks whether the rotate node is right or left .
         */
        if (rotateNode.getParent() != null) {
            if (rotateNode.getParent().isRightChild(rotateNode))
                rotateNode.getParent().setRightChild(node);
            else
                rotateNode.getParent().setLeftChild(node);
        } else {
            node.setParent(null);
            this.root = node;
        }
        rotateNode.setParent(node);
    }

    @Override
    public INode<T,V> findMin(INode<T,V> node){
        if(node.isNull())
            return null;
        while (!node.getLeftChild().isNull())
            node = node.getLeftChild();
        return node;
    }
    @Override
    public INode<T,V> findMax(INode<T,V> node){
        if(node.isNull())
            return null;
        while (!node.getRightChild().isNull())
            node = node.getRightChild();
        return node;
    }
    private INode<T,V> doubleBlack(INode<T,V> node) {
        //CASE 0: IF DB IS ROOT
        if(node.getParent() == null)
            return null;
        //CASE 1: IF SIBLING IS RED
        INode<T, V> sibling = node.getSibling();
        if(sibling.getColor())
        {
            node.getParent().setColor(true);
            sibling.setColor(false);
            if(node.isChildLeft()) rotateLeft(node.getParent());
            else rotateRight(node.getParent());
            return doubleBlack(node);
        }
        //CASE 2: IF SIBLING IS BLACK AND BOTH CHILDREN ARE BLACK /Nil
        else if(!sibling.getLeftChild().getColor() && !sibling.getRightChild().getColor())
        {
            INode<T, V> parent = node.getParent();
            sibling.setColor(true);
            if(parent.getColor())
                parent.setColor(false);
            else
                return doubleBlack(parent);

        }
        // if black sibling + at least one red child{near & far > rotations} //1
        //if near:
        else if (sibling.isChildLeft() && sibling.getLeftChild().getColor() || !sibling.isChildLeft() && sibling.getRightChild().getColor())
        {
            boolean temp = node.getParent().getColor();
            node.getParent().setColor(sibling.getColor());
            sibling.setColor(temp);
            //TODO dasd
            if(sibling.isChildLeft())
                sibling.getLeftChild().setColor(false);
            else
                sibling.getRightChild().setColor(false);

            if( node.isChildLeft())
                rotateLeft(node.getParent());
            else rotateRight(node.getParent());

        }
        else
        {
            sibling.setColor(true);
            if(sibling.isChildLeft())
                sibling.getRightChild().setColor(false);
            else
                sibling.getLeftChild().setColor(false);
            if(sibling.isChildLeft())
                rotateLeft(sibling);
            else rotateRight(sibling);
            return doubleBlack(node);
        }

        return null;
    }
    private void inorderTraverse(INode<T,V> root){
        if(root.isNull())
            return ;
        inorderTraverse(root.getLeftChild());
        entries.add(new MapEntry<>(root.getKey(),root.getValue()));
        inorderTraverse(root.getRightChild());
    }
    public Set<Map.Entry<T, V>> getEntries(INode<T,V> node) {
        entries.clear();
        inorderTraverse(node);
        return entries;
    }

}
