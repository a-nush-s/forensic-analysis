package forensic;

/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {

        int numOfStrs = StdIn.readInt();
        STR strs[] = new STR[numOfStrs];
        for (int i = 0; i<numOfStrs; i++){
            String strString = StdIn.readString();
            int occurrences = StdIn.readInt();
            STR temp = new STR(strString, occurrences);
            strs[i] = temp;
        }
        Profile profile = new Profile(strs);
        
        return profile; // update this line
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        TreeNode node = new TreeNode(name, newProfile, null, null);
        if (treeRoot == null){
            treeRoot = node;
            return;
        }
        TreeNode curr = treeRoot;
        String currname = treeRoot.getName();
        while (curr!=null){
            if (name.compareTo(currname)<0 && curr.getLeft()!=null){
                curr = curr.getLeft();
            } else if (name.compareTo(currname)<0 && curr.getLeft()==null){
                curr.setLeft(node);
                return;
            } else if (name.compareTo(currname)>0 && curr.getRight()!=null){
                curr = curr.getRight();
            } else if (name.compareTo(currname)>0 && curr.getRight()==null){
                curr.setRight(node);
                return;
            }
            currname = curr.getName();
        }

    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    public int getMatchingProfileCount(boolean isOfInterest) {
        
        int total = 0;
        Queue<TreeNode> queue = new Queue<TreeNode>();
        qOfNodes(queue, treeRoot);
        TreeNode last = new TreeNode();
        queue.enqueue(last);
        TreeNode curr = queue.dequeue();
        Profile currProfile = curr.getProfile();
        while (!queue.isEmpty()){
            if (currProfile.getMarkedStatus() == isOfInterest){
                total++;
            }
            curr = queue.dequeue();
            if(queue.isEmpty()) break;
            currProfile = curr.getProfile();
        }


        return total; // update this line
    }

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() {

        Queue<TreeNode> queue = new Queue<TreeNode>();
        qOfNodes(queue, treeRoot);
        TreeNode last = new TreeNode();
        queue.enqueue(last);
        TreeNode curr = queue.dequeue();
        Profile currProfile = curr.getProfile();
        STR strs[] = currProfile.getStrs();
        int half = 0;
        if(strs.length%2==1){
            half=strs.length/2 + 1;
        } else {
            half=strs.length/2;
        }
        int count = 0;
        while (!queue.isEmpty()){
            for (int i = 0; i<strs.length; i++){
                int occurrences = strs[i].getOccurrences();
                String str = strs[i].getStrString();
                int unknownoccur1 = numberOfOccurrences(firstUnknownSequence, str);
                int unknownoccur2 = numberOfOccurrences(secondUnknownSequence, str);
                int total = unknownoccur1 + unknownoccur2;
                if (occurrences==total){
                    count++;
                }
                if(count==half){
                    currProfile.setInterestStatus(true);
                    break;
                }
            }
            curr = queue.dequeue();
            if (queue.isEmpty()) break;
            currProfile = curr.getProfile();
            strs = currProfile.getStrs();
            if(strs.length%2==1){
                half=strs.length/2 + 1;
            } else {
                half=strs.length/2;
            }
            count = 0;
        }
    }

    //my own method to create a queue with all nodes in the tree - inorder
    private void qOfNodes(Queue<TreeNode> queue, TreeNode root){
        if (root!=null){
            qOfNodes(queue, root.getLeft());
            queue.enqueue(root);
            qOfNodes(queue, root.getRight());
        }
    }

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {

        int length = getMatchingProfileCount(false);
        String unmarked[] = new String[length];
        TreeNode node = treeRoot;
        Queue<TreeNode> queue = new Queue<TreeNode>();
        queue.enqueue(node);
        for (int i = 0; i<length; i++){
            while (!queue.isEmpty()){
                node = queue.dequeue();
                if (node.getLeft()!=null) queue.enqueue(node.getLeft());
                if (node.getRight()!=null) queue.enqueue(node.getRight());
                Profile profile = node.getProfile();
                if (!profile.getMarkedStatus()){
                    unmarked[i] = node.getName();
                    break;
                }
            }
        }

        return unmarked; // update this line
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        treeRoot = delete(treeRoot, fullName);
    }

    private TreeNode deleteMin(TreeNode node){
        if (node.getLeft()==null) return node.getRight();
        node.setLeft(deleteMin(node.getLeft()));
        return node;
    }

    private TreeNode delete(TreeNode node, String key){
        if (node==null) return null;
        int cmp = key.compareTo(node.getName());
        if (cmp<0) {
            node.setLeft(delete(node.getLeft(), key));
        } else if (cmp>0) {
            node.setRight(delete(node.getRight(), key));
        } else {
            if (node.getRight()==null) return node.getLeft();
            if (node.getLeft()==null) return node.getRight();
            TreeNode temp = node;
            node = min(temp.getRight());
            node.setRight(deleteMin(temp.getRight()));
            node.setLeft(temp.getLeft());
        }
        return node;
    }

    private TreeNode min(TreeNode node){
        TreeNode min = node;
        while (min.getLeft()!=null){
            min = min.getLeft();
        }
        return min;
    }


    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        String unmarked[] = getUnmarkedPeople();
        for (int i = 0; i<unmarked.length; i++){
            removePerson(unmarked[i]);
        }

    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
