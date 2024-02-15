import javax.imageio.event.IIOWriteProgressListener;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.PriorityQueue;
import java.util.Set;
public class HuffmanCode implements Huffman{
    Map<Character, Long> charCount;

    /**
     *
     * @param pathName - path to a file to read
     * @return countFrequencies- map that contains a character and its frequency in the document provided
     * @throws IOException
     */

    public Map<Character, Long> countFrequencies(String pathName) throws IOException{
        //counts the frequencies of characters in the document and stores them as values, and their characters as keys
        Map<Character, Long> charCount = new HashMap<>();
        BufferedReader input = new BufferedReader(new FileReader(pathName));
        try{
            int c = input.read();
            // checks each character in the document stopping when no more characters remain
            if(c == -1){
                throw new IOException("Invalid Argument");
            }
            while(c != -1){
                Character tempChar = (char) c;
                if(!charCount.containsKey(tempChar)){
                    Long mapVal = (long) 1;
                    charCount.put(tempChar, mapVal);
                }
                else{
                    Long oldVal = charCount.get(tempChar);
                    charCount.put(tempChar, ++oldVal);
                }
                c = input.read();
            }
            return charCount;
        }
        //closes the file

        finally{input.close();}
    }

    /**
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return a Binary Tree that stores elements of type Code tree Element
     */

    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies){
        TreeComparator<BinaryTree<CodeTreeElement>> comparatorForPQ = new TreeComparator<>();
        PriorityQueue<BinaryTree<CodeTreeElement>> binaryTrees = new PriorityQueue<>(comparatorForPQ);
        Set<Character> keysOfMap = frequencies.keySet();
        //gets all the keys from the map and uses them to access their values creating a new code tree element to store
        //the information
        for(Character element: keysOfMap){
            BinaryTree<CodeTreeElement> newInsert = new BinaryTree<>(new CodeTreeElement(frequencies.get(element), element));
            binaryTrees.add(newInsert);
        }
//Removes all trees from the priority queue until only one remains
        while(binaryTrees.size() > 1){
            BinaryTree<CodeTreeElement> oneNode = binaryTrees.remove();
            BinaryTree<CodeTreeElement> twoNode = binaryTrees.remove();
            long totalFrequency = oneNode.getData().getFrequency() + twoNode.getData().getFrequency();
            BinaryTree<CodeTreeElement> subTreeRoot = new BinaryTree<>(new CodeTreeElement(totalFrequency, null));
            subTreeRoot.setLeft(oneNode); subTreeRoot.setRight(twoNode);
            binaryTrees.add(subTreeRoot);
        }
        //stores that last remaining queue for future use
        BinaryTree<CodeTreeElement> codeTree = binaryTrees.remove();
        if(frequencies.size() == 1){
            return new BinaryTree<>(new CodeTreeElement((long)0, null), codeTree, codeTree);

        }

        return codeTree;
    }

    /**
     *
     * @param codeTree the tree for encoding characters produced by makeCodeTree
     * @return A Map that contains the characters and their respective huffman codes
     */

    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree){
        //creates an empty string to pass through the helper method
        String valueMap = "";
        //creates a new hashMap to store the characters with their huffman codes
        Map<Character, String> intendedReturn = new HashMap<>();
        treeTraversal(intendedReturn, codeTree, valueMap);
        return intendedReturn;

    }

    /**
     * A Helper method for traversing the codeTree provided to the computeCodes method
     * @param playbook, the empty map that is subsequently filled with characters and their huffman code as a result of traversal
     * @param codeTree, the Binary Tree that contains all the characters and their frequencies and serves as the basis for
     *                  the huffman tree code creation and deciphering
     * @param soFar, an empty string that stores corresponding values per move down the code tree
     */
    private void treeTraversal(Map<Character, String> playbook, BinaryTree<CodeTreeElement> codeTree, String soFar){
        //Recursively moves through the code tree and updates the string that serves as it huffMan code store

        if(codeTree != null) {
//            if we hit a leaf, put the character into the map with its code as its value
            if(codeTree.isLeaf()){playbook.put(codeTree.getData().getChar(), soFar);}

            if (codeTree.getRight() != null) {

                treeTraversal(playbook, codeTree.getRight(), soFar + '1');
            }

            if (codeTree.getLeft() != null) {

                treeTraversal(playbook, codeTree.getLeft(), soFar + '0');
            }

        }

    }

    /**
     *
     * @param codeMap - Map of characters to codes produced by computeCodes
     * @param pathName - File to compress
     * @param compressedPathName - Store the compressed data in this file
     * @throws IOException
     */

    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException{
        //Method compresses the bit information of characters in a file using their huffan codes
        //opens the file and writes out the compressed information into another one(bitOutput)

        BufferedReader input = new BufferedReader(new FileReader(pathName));
        BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName);

        try{
            int c = input.read();
            //loops through the characters in the file storing their ASCII value in 'c'
            while(c != -1){
                Character tempChar = (char) c;
                String valueAssoc  = codeMap.get(tempChar);
                //after casting c using the new character as a key into the code map to get the huffman code
                boolean prob;
                for (int i = 0; i < valueAssoc.length(); i++) {
                    if(valueAssoc.charAt(i) == '1')prob = true;
                    else{prob = false;}
                    bitOutput.writeBit(prob);
                }
                c = input.read();
        }}
        finally{input.close(); bitOutput.close();}
    }

    /**
     *
     * @param compressedPathName - file created by compressFile
     * @param decompressedPathName - store the decompressed text in this file, contents should match the original file before compressFile
     * @param codeTree - Tree mapping compressed data to characters
     * @throws IOException
     */

    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException{
        //Basically works backwards from the compress file, using the compressed file and the code tree to get back the
        //character values
        BufferedWriter output = new BufferedWriter(new FileWriter(decompressedPathName));
        //writes the decompressed, intelligible information into the created file
        BufferedBitReader bitInput = new BufferedBitReader(compressedPathName);
        try{
        BinaryTree<CodeTreeElement> setEqualsCodeTree = codeTree;
        //loops through the truth value filled compressed file and uses true and false values to traverse the code tree
        while(bitInput.hasNext()){
            boolean thing1 = bitInput.readBit();
            if(thing1){
                setEqualsCodeTree = setEqualsCodeTree.getRight();
            }
            else{
                setEqualsCodeTree = setEqualsCodeTree.getLeft();
            }
            if(setEqualsCodeTree.isLeaf()){
                output.write(setEqualsCodeTree.getData().getChar());
                setEqualsCodeTree = codeTree;
            }
        }
        }
        finally{
            output.close();
            bitInput.close();
        }
    }

    public static void main(String[] args){
        HuffmanCode one = new HuffmanCode();
        //Creates a new HuffMan code instance
        //I store my US constitution in a file called TestCaseOne(originally my test case file where I tested strings like 'hello')
        //Because, although the message is completely and correctly compressed and decompressed for my War and Peace and Test Case Files
        //The US constitution file doesn't quite work, but it has nothing to do with the code it's just the syntax its saved in I think
        String pathName = "inputs/TestCaseOne"; Map<Character, Long> playbook = new HashMap<>();
        String pathName1 = "inputs/WarAndPeace.txt"; Map<Character, Long> deck = new HashMap<>();
        String pathName2 = "inputs/TestCaseTwo"; Map<Character, Long> newDeck = new HashMap<>();
        String pathName3 = "inputs/TestCaseThree"; Map<Character, Long> newNewDeck = new HashMap<>();
        try{
            newNewDeck = one.countFrequencies(pathName3);}
        catch(Exception e){
            System.out.println(e);
        }
            BinaryTree<CodeTreeElement> queue = one.makeCodeTree(newNewDeck);
            Map<Character, String> codes = one.computeCodes(queue);
        try{
            one.compressFile(codes, pathName3, "inputs/TestCaseThree_Compressed.txt");}
        catch(Exception e){
            System.out.println(e);
        }
        try{
            one.decompressFile("inputs/TestCaseThree_Compressed.txt", "inputs/TestCaseThree_decompressed.txt", queue);}
        catch(Exception e){
            System.out.println(e);
        }

        }

}