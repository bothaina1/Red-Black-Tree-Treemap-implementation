package eg.edu.alexu.csd.filestructure.redblacktree.Trees;

import eg.edu.alexu.csd.filestructure.redblacktree.Tests.TestRunner;
import org.junit.Assert;

import javax.management.RuntimeErrorException;
import java.util.*;
import java.util.TreeMap;

public class RedBlackTree<T extends Comparable<T>, V> implements IRedBlackTree<T, V> {
    private final INode<T, V> nil = new Node<>();
    private INode<T, V> root;
    private Set<Map.Entry<T,V>> entries;

    public RedBlackTree() {
        root = nil;
        entries = new LinkedHashSet<>();
    }


    @Override
    public INode<T,V> getRoot() {
        return this.root;
    }

    @Override
    public boolean isEmpty() {
        return this.root == nil;
    }

    @Override
    public void clear() {
         this.root = nil;
    }

    @Override
    public V search (T key) {
            INode<T, V> node = search(root, key);
            if (node != null)
                return node.getValue();
        return null;
    }


    protected INode<T,V> search(INode<T,V> root, T key) {
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
        return contains(root,key);
    }


    private boolean contains(INode<T,V> root, T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error("Tree doesn't contain null key"));
        }
        if (root != nil) {
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
        newNode.setParent(nil);
        if (root == nil) {
            root = newNode;
            root.setColor(false);
            return;
        }

        if (insertNode(this.root, newNode))                //BST Insertion
            insertCases(newNode);                        //Insertion Cases
    }

    /**
     * Insert new in Binary Search Tree
     *
     */
    private boolean insertNode(INode<T, V> root, INode<T, V> newNode) {
        try {
            if (newNode.getKey().compareTo(root.getKey()) > 0) {    //newNode > root
                if (root.getRightChild() == nil) {
                    root.setRightChild(newNode);
                    newNode.setParent(root);
                }
                insertNode(root.getRightChild(), newNode);
            } else if (newNode.getKey().compareTo(root.getKey()) < 0) { //newNode < root
                if (root.getLeftChild() == nil) {
                    root.setLeftChild(newNode);
                    newNode.setParent(root);
                }
                insertNode(root.getLeftChild(), newNode);
            } else {
                root.setValue(newNode.getValue());
                return false;
            }
            return true;
        } catch (ClassCastException e) {
            throw new ClassCastException("can't compare different data types");
        }
    }

    private void insertCases(INode<T, V> newNode) {
        Node<T, V> parent = (Node<T, V>) newNode.getParent();
        Node<T, V> grandParent = (Node<T, V>) newNode.getParent().getParent();
        //Easy Case ,Parent is Black OR it does not have grandparent
        if (!newNode.getParent().getColor() || grandParent == nil)
            return;
        boolean uncleColor = ((Node) newNode).getUncle().getColor();
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
        ((Node<T, V>) newNode).getUncle().setColor(false);
        if (grandParent.getParent() != nil) {
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
        //TODO Stackflow ???
        //TODO sibling null ??
        System.out.println("before deletion");
        inOrder(root);
        System.out.println("IAM DELETING "+key);

        if(key == null)
            throw new RuntimeErrorException(new Error("Can't delete null key"));
//        return delete( search(root,key));
        //TODO fix
        boolean returnV = delete( search(root,key));
        System.out.println("After deletion");
        inOrder(root);
        return returnV;

    }

    private boolean delete(INode<T, V> deletedNode) {
        if (deletedNode == null)
            return false;
        //TODO if parent is null (root case) //1
        if(deletedNode.getLeftChild().isNull() && deletedNode.getRightChild().isNull()){
            deletedNode.setColor(false);
            deletedNode.setKey(null);
            deletedNode.setValue(null);
            if(!deletedNode.getColor()){
                doubleBlack(deletedNode);
            }
        }
        else if(deletedNode.getLeftChild().isNull()){
            if(!deletedNode.getColor())
                deletedNode.getRightChild().setColor(false);
            if(deletedNode.getParent() != null)
                deletedNode.getParent().setRightChild(deletedNode.getRightChild());
        }
        else if(deletedNode.getRightChild().isNull()){
            if(!deletedNode.getColor())
                deletedNode.getLeftChild().setColor(false);
            if(deletedNode.getParent() != null)
                deletedNode.getParent().setLeftChild(deletedNode.getLeftChild());
        }
        else {
            INode<T,V> predecessor = findMin(deletedNode.getRightChild());
            deletedNode.setValue(predecessor.getValue());
            deletedNode.setKey(predecessor.getKey());
            delete(predecessor);
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
        if (node.getLeftChild() != nil) {
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
        if (node.getRightChild() != nil) {
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
        if (rotateNode.getParent() != nil) {
            if (((Node<T, V>) rotateNode.getParent()).isRightChild(rotateNode))
                rotateNode.getParent().setRightChild(node);
            else
                rotateNode.getParent().setLeftChild(node);
        } else {
            node.setParent(nil);
            this.root = node;
        }
        rotateNode.setParent(node);
    }

//    protected INode<T,V> search(INode<T,V> root,T key){
//        if(root == null)
//            return null;
//        if(key.compareTo(root.getKey()) < 0)
//             return search(root.getLeftChild(),key);
//        else if(key.compareTo(root.getKey()) > 0)
//            return search(root.getRightChild(),key);
//        return root;
//    }
    protected INode<T,V> findMin(INode<T,V> node){
        if(node.isNull())
            return null;
        while (!node.getLeftChild().isNull())
            node = node.getLeftChild();
        return node;
    }
    protected INode<T,V> findMax(INode<T,V> node){
        if(node.isNull())
            return null;
        while (!node.getRightChild().isNull())
            node = node.getRightChild();
        return node;
    }
    private void doubleBlack(INode<T,V> node) {
        //CASE 0: IF DB IS ROOT
        INode<T, V> sibling = ((Node<T,V>)node).getSibling();
        if(node.getParent() == null || sibling == null )
            return;
        //CASE 1: IF SIBLING IS RED
        if(sibling.getColor())
        {
            node.getParent().setColor(true);
            sibling.setColor(false);
            if(((Node<T, V>) node).isChildLeft()) rotateLeft(node.getParent());
            else rotateRight(node.getParent());
            doubleBlack(node);
        }
        //CASE 2: IF SIBLING IS BLACK AND BOTH CHILDREN ARE BLACK /Nil
        else if(!sibling.getLeftChild().getColor() && !sibling.getRightChild().getColor())
        {
            INode<T, V> parent = node.getParent();
            if(parent.getColor())
            {
                parent.setColor(false);
                sibling.setColor(true);
            }
            else
            {
                sibling.setColor(true);
                doubleBlack(parent);
            }
        }
        // if black sibling + at least one red child{near & far > rotations} //1
        else if((sibling.getLeftChild().getColor())){
            if(!((Node<T,V>)sibling).isChildLeft()){
                sibling.setColor(true);
                sibling.getLeftChild().setColor(false);
                rotateLeft(sibling);
            }
            sibling.getLeftChild().setColor(false);
            rotateRight(sibling.getParent());
        }
        else if((sibling.getRightChild().getColor())){
            if(((Node<T,V>)sibling).isChildLeft()){
                sibling.setColor(true);
                sibling.getRightChild().setColor(false);
                rotateRight(sibling);
            }
            sibling.getRightChild().setColor(false);
            rotateLeft(sibling.getParent());
        }
    }
    private void inorderTraverse(INode<T,V> root){
        if(root == nil)
            return ;
        inorderTraverse(root.getLeftChild());
        entries.add(new MapEntry<T,V>(root.getKey(),root.getValue()));
        inorderTraverse(root.getRightChild());
    }
    public Set<Map.Entry<T, V>> getEntries(INode node) {
        inorderTraverse(node);
        return entries;
    }
    public void inOrder(INode<T,V> node){
        if (node.isNull()) {
            return;
        }
        inOrder(node.getLeftChild());
        System.out.println("key "+ node.getKey() + " value " + node.getValue()+" color " + node.getColor());
        inOrder(node.getRightChild());
    }

    public static void main(String[] args) {
        IRedBlackTree<Integer, String> redBlackTree = (IRedBlackTree<Integer, String>) TestRunner.getImplementationInstanceForInterface(IRedBlackTree.class);
//        redBlackTree.insert(2, "soso");
//			redBlackTree.insert(3, "soso");
//			redBlackTree.insert(4, "soso");
			redBlackTree.insert(6, "soso");
             redBlackTree.insert(7, "soso");
//			Assert.assertTrue(redBlackTree.delete(3));
//        Assert.assertTrue(redBlackTree.delete(4));
        Assert.assertTrue(redBlackTree.delete(6));
//        try {
//            Random r = new Random();
//            HashSet<Integer> list = new HashSet<>();
//            for (int i = 0; i < 5; i++) {
//                int key = r.nextInt(10);
//                list.add(key);
//                redBlackTree.insert(key, "soso" + key);
//            }
//
//            for (Integer elem : list)
//                Assert.assertTrue(redBlackTree.delete(elem));
//            INode<Integer, String> node = redBlackTree.getRoot();
//            if (!(node == null || node.isNull()))
//                Assert.fail();
//        } catch (Throwable e) {
//            TestRunner.fail("Fail to handle deletion", e);
//        }

    }
}
