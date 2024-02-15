import java.util.*;
public class TreeComparator<T> implements Comparator<T> {
    /**
     *
     * @param t1 BinaryTree of type code tree element for comparison
     * @param t2 BinaryTree of type code tree element for comparison
     * @return an integer
     */
    public int compareTo(BinaryTree<CodeTreeElement> t1, BinaryTree<CodeTreeElement> t2){
        if(t1.getData().getFrequency() < t2.getData().getFrequency())return -1;
        if(t1.getData().getFrequency() > t2.getData().getFrequency())return 1;
        return 0;
    }
    public int compare(T t1, T t2){
        return compareTo((BinaryTree<CodeTreeElement>) t1, (BinaryTree<CodeTreeElement>) t2);
    }
}
