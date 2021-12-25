import java.lang.Integer;
import java.lang.Math;
import java.util.*;

public class WaterJugProblem {
    static int[] capacity;
    static int[] volume;
    static int target;
    static boolean isSolvable;

    public static void main(String[] args) {
        //Scanner for user input
        Scanner input = new Scanner(System.in);

        //User inputted strings
        String jugString = input.nextLine();
        target = input.nextInt();

        //Splits input string into separate substrings
        String[] jugSubstrings = jugString.split(" ");
        //Turns every substring into an int with the correct jug size and assigns it to an integer array
        capacity = new int[jugSubstrings.length];
        for (int i = 0; i < jugSubstrings.length; i++) {
            capacity[i] = Integer.parseInt(jugSubstrings[i]);
        }
        //Array to track current value of water in jugs
        volume = new int[capacity.length];
        //Assigns initial volume values to the jugs
        for (int i = 0; i < capacity.length; i++) {
            if (i == 0)
                volume[i] = capacity[i];
            else
                volume[i] = 0;
        }
        //checks to see if problem is solvable (target value has to be divisible by gcd of the jug sizes)
        int temp = gcd(capacity[0], capacity[1]);
        for (int i = 2; i < capacity.length; i++) {
            temp = gcd(temp, capacity[i]);
        }
        if (target % temp == 0)
            isSolvable = true;
        else {
            isSolvable = false;
            System.out.println("NOT POSSIBLE");
        }
        //runs is problem is solvable
        if (isSolvable) {
            //creates start node for BFS
            Node startNode = new Node(capacity, volume, null, null);
            //calls BFS on start node
            BFS(startNode);
        }
    }

    //find greatest common denominator of two inputted integers
    public static int gcd(int a, int b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }

    //makes valid pours for a node
    public static void validPours(Node n) {
        for (int i = 0; i < n.getVolume().length; i++) {
            for (int j = 0; j < n.getVolume().length; j++) {
                int[] newVol = pour(i, j, n.getCapacity(), n);
                if (newVol != null && i != j) {
                    String temp = i + " " + j;
                    Node child = new Node(capacity, newVol, n, temp);
                    n.addChild(child);
                }
            }
        }
    }

    //If a valid number, pours water from one jug to another
    public static int[] pour(int fromIndex, int toIndex, int[] capacity, Node n) {
        int[] temp = Arrays.copyOf(n.getVolume(), n.getVolume().length);
        //Picks max amount that can be added to jug to avoid going over capacity
        int difference = Math.min(temp[fromIndex], capacity[toIndex] - temp[toIndex]);
        if (difference >= 1) {
            temp[fromIndex] -= difference;
            temp[toIndex] += difference;
            return temp;
        }
        return null;
    }

    public static void BFS(Node s) {
        LinkedList<Node> queue = new LinkedList();
        ArrayList<int[]> visited = new ArrayList();
        boolean check = false;

        queue.add(s);

        while (!queue.isEmpty()) {
            check = false;
            s = queue.poll();
            if (s.checkVolumes(target)) {
                s.printPath();
                break;
            }
            for(int i = 0; i < visited.size(); i++) {
                if(Arrays.equals(s.getVolume(), visited.get(i)) == true) {
                    check = true;
                    break;
                }
            }
            if(!check) {
                validPours(s);
                visited.add(s.getVolume());
            }
            //if the node has children, and the volumes haven't been checked, add to the queue
            if (s.hasChildren()) {
                for (int i = 0; i < s.children.size(); i++) {
                    if (s.getChild(i) != null && !visited.contains(s.getChild(i).getVolume())) {
                        queue.add(s.getChild(i));
                    }
                }
            }
        }

    }

    static class Node {
        int[] capacity;
        int[] volume;
        int pourNum;
        String[] path;
        Node root;
        ArrayList<Node> children;

        public Node(int[] capacity, int[] volume, Node root, String pour) {
            this.capacity = capacity;
            this.volume = volume;
            this.root = root;
            children = new ArrayList<Node>();
            if (root != null) {
                pourNum = root.getPourNum() + 1;
                path = Arrays.copyOf(root.getRootPath(), root.getRootPath().length + 1);
                path[root.getRootPath().length] = pour;
            }
            else {
                pourNum = 0;
                path = new String[0];
            }
        }

        //adds child to nodes arrayList of child nodes
        public void addChild(Node n) {
            children.add(n);
        }
        //returns/sets Node properties
        public int[] getCapacity() {
            return capacity;
        }
        public int[] getVolume() {
            return volume;
        }
        public int getPourNum() {
            return pourNum;
        }
        public String[] getRootPath() {
            return path;
        }
        public Node getChild(int i) {
            return children.get(i);
        }
        public boolean hasChildren() {
            if (children.size() != 0)
                return true;
            return false;
        }

        //prints path to node
        public void printPath() {
            for(int i = 0; i < path.length; i++) {
                System.out.println(path[i]);
            }
        }
        //checks the volumes of the current node for a target value
        public boolean checkVolumes(int target) {
            for (int i = 0; i < volume.length; i++) {
                if (volume[i] == target)
                    return true;
            }
            return false;
        }
    }

}