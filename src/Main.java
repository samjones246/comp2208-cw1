import java.io.*;
import java.util.*;

public class Main {
    public BlockWorld moveAgent(BlockWorld state, int direction) throws UnsupportedOperationException, IndexOutOfBoundsException{
        int x1,y1,x2,y2;
        x1 = state.getAgentX();
        x2 = state.getAgentX();
        y1 = state.getAgentY();
        y2 = state.getAgentY();
        switch (direction){
            case 0:
                y2++;
                break;
            case 1:
                y2--;
                break;
            case 2:
                x2--;
                break;
            case 3:
                x2++;
                break;
            default:
                throw new UnsupportedOperationException(direction + " is not in {0,1,2,3})");
        }
        BlockWorld newState;
        int size = state.getGrid()[0].length;
        char[][] grid = new char[size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                grid[i][j] =  state.getGrid()[i][j];
            }
        }
        grid[x1][y1] = grid[x2][y2];
        grid[x2][y2] = ' ';
        newState = new BlockWorld(grid, x2, y2);
        return newState;
    }

    public Result dfs(BlockWorld start, BlockWorld goal){
        List<Integer> directions = new ArrayList<>();
        List<String> path = new ArrayList<>();
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
        String lastAction = "";
        for(int i=0;i<4;i++){
            directions.add(i);
        }
        Stack<BlockWorld> s = new Stack<>();
        s.push(start);
        int expansions = 0;
        while (!s.empty()){
            BlockWorld state = s.pop();
            if(expansions!=0){
                path.add(lastAction);
            }
            if(Arrays.deepEquals(state.getGrid(), goal.getGrid())){
                System.out.println("Reached goal state after "+expansions+" expansions.");
                break;
            }
            expansions++;
            Collections.shuffle(directions);
            for(int i=0;i<4;i++){
                try {
                    s.push(moveAgent(state, directions.get(i)));
                    lastAction = dirStrings[directions.get(i)];
                }catch (IndexOutOfBoundsException ignored){}
            }
            System.out.println("Frontier Size: "+s.size()+", Depth: "+expansions);
        }
        return new Result(expansions, path);
    }
    public Result bfs(BlockWorld start, BlockWorld goal){
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
        int expansions = 0;
        int lastDepth = 0;
        Queue<BlockWorld> queue = new LinkedList<>();
        List<BlockWorld> visited = new ArrayList<>();
        HashMap<BlockWorld, BlockWorld> parent = new HashMap<>();
        HashMap<BlockWorld, String> action = new HashMap<>();
        HashMap<BlockWorld, Integer> depth = new HashMap<>();
        queue.add(start);
        parent.put(start, null);
        action.put(start, null);
        depth.put(start, 0);
        BlockWorld last = start;
        System.out.println("Searching level 0...");
        while (!queue.isEmpty()){
            BlockWorld state=queue.remove();
            if(state!=start){
                depth.put(state, depth.get(parent.get(state))+1);
            }
            if(depth.get(state)>lastDepth){
                System.out.println("Searched "+visited.size()+" nodes so far at level "+lastDepth);
                lastDepth=depth.get(state);
                System.out.println("Searching level "+lastDepth+"...");
            }
            visited.add(state);
            if(Arrays.deepEquals(state.getGrid(), goal.getGrid())){
                last=state;
                break;
            }
            for(int i=0;i<4;i++){
                try{
                    BlockWorld child = moveAgent(state, i);
                    if(!visited.contains(child)) {
                        queue.add(child);
                        parent.put(child, state);
                        action.put(child, dirStrings[i]);
                    }
                }catch (IndexOutOfBoundsException ignored){}
            }
            expansions++;
//            System.out.println("Frontier Size: "+queue.size()+", Depth: "+depth.get(state));
        }
        List<String> path = new ArrayList<>();
        while(parent.get(last)!=null){
            path.add(action.get(last));
            last = parent.get(last);
        }
        Collections.reverse(path);
        System.out.println("Reached goal state after "+expansions+" expansions.");
        return new Result(expansions, path);
    }
    public Result ids(BlockWorld start, BlockWorld goal){
        Map<BlockWorld, BlockWorld> parent;
        Map<BlockWorld, String> action;
        List<BlockWorld> visited;
        int depth = 0;
        IntInABox expansions = new IntInABox(0);
        List<String> path = null;
        while(path==null){
            parent = new HashMap<>();
            action = new HashMap<>();
            visited = new ArrayList<>();
            System.out.println("Searching level "+depth+"...");
            path = dls(start,goal,depth, parent, action, expansions, visited);
            System.out.println("Searched "+visited.size()+" nodes at depth limit "+depth);
            depth++;
        }
        System.out.println("Reached goal state after "+expansions.getVal()+" expansions at depth "+(depth-1));
        return new Result(expansions.getVal(), path);
    }
    private List<String> dls(BlockWorld state, BlockWorld goal, int depth, Map<BlockWorld, BlockWorld> parent, Map<BlockWorld, String> action, IntInABox expansions, List<BlockWorld> visited){
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
        visited.add(state);
        if(depth==0){
            if(Arrays.deepEquals(state.getGrid(), goal.getGrid())){
                List<String> path = new ArrayList<>();
                while(parent.get(state)!=null){
                    path.add(action.get(state));
                    state = parent.get(state);
                }
                Collections.reverse(path);
                return path;
            }else return null;
        }else{
            expansions.inc();
            for(int i=0;i<4;i++){
                try {
                    BlockWorld child = moveAgent(state, i);
                    parent.put(child, state);
                    action.put(child, dirStrings[i]);
                    List<String> path = dls(child, goal, depth - 1, parent, action, expansions, visited);
                    if (path != null) {
                        return path;
                    }
                }catch (IndexOutOfBoundsException ignored){}
            }
            return null;
        }
    }
    public Result ash(BlockWorld start, BlockWorld goal){
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
        List<BlockWorld> frontier = new ArrayList<>();
        List<BlockWorld> visited = new ArrayList<>();
        HashMap<BlockWorld, BlockWorld> parent = new HashMap<>();
        HashMap<BlockWorld, Integer> distanceFromRoot = new HashMap<>();
        HashMap<BlockWorld, Integer> score = new HashMap<>();
        HashMap<BlockWorld, String> action = new HashMap<>();
        int expansions = 0;
        BlockWorld last = start;
        distanceFromRoot.put(start, 0);
        score.put(start, evaluate(start, goal));
        frontier.add(start);
        while(!frontier.isEmpty()){
            int min = -1;
            int mindex = 0;
            for(int i=0;i<frontier.size();i++){
                if(min<0||score.get(frontier.get(i))<min){
                    min=score.get(frontier.get(i));
                    mindex = i;
                }
            }
            BlockWorld state = frontier.get(mindex);
            System.out.println("Node Score: "+score.get(state)+", Node Depth: "+distanceFromRoot.get(state));
            visited.add(state);
            if (Arrays.deepEquals(state.getGrid(), goal.getGrid())) {
                last = state;
                break;
            }
            frontier.remove(state);
            expansions++;
            for(int i=0;i<4;i++){
                try {
                    BlockWorld child = moveAgent(state, i);
                    if(!visited.contains(child)) {
                        distanceFromRoot.put(child, distanceFromRoot.get(state) + 1);
                        frontier.add(child);
                        parent.put(child, state);
                        score.put(child, evaluate(child, goal) + distanceFromRoot.get(child));
                        action.put(child, dirStrings[i]);
                    }
                }catch (IndexOutOfBoundsException ignored){}
            }
        }
        List<String> path = new ArrayList<>();
        while(parent.get(last)!=null){
            path.add(action.get(last));
            last = parent.get(last);
        }
        Collections.reverse(path);
        System.out.println("Reached goal state after "+expansions+" expansions.");
        return new Result(expansions, path);

    }
    public int evaluate(BlockWorld state, BlockWorld goal){
        HashMap<Character, Integer[]> stateBlocks = new HashMap<>(), goalBlocks = new HashMap<>();
        int size = state.getGrid()[0].length;
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                char s = state.getGrid()[i][j], g = goal.getGrid()[i][j];
                if(s!=' ') {
                    stateBlocks.put(state.getGrid()[i][j], new Integer[]{i, j});
                }
                if(g!=' ') {
                    goalBlocks.put(goal.getGrid()[i][j], new Integer[]{i, j});
                }
            }
        }
        int val = 0;
        for(Character c : stateBlocks.keySet()){
            int xdif = Math.abs(goalBlocks.get(c)[0] - stateBlocks.get(c)[0]);
            int ydif = Math.abs(goalBlocks.get(c)[1] - stateBlocks.get(c)[1]);
            val+=xdif+ydif;
        }
        return val;
    }

    public static void main(String[] args) {
        Main main = new Main();
        int size = 4;
        char[][] startGrid = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                startGrid[i][j] = ' ';
            }
        }
        char[][] goalGrid = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                goalGrid[i][j] = ' ';
            }
        }
        startGrid[0][3] = 'A';
        startGrid[3][0] = 'B';
        startGrid[3][3] = 'C';
        goalGrid[1][2] = 'A';
        goalGrid[1][1] = 'B';
        goalGrid[1][0] = 'C';
        BlockWorld startState = new BlockWorld(startGrid, 0, 0);
        BlockWorld goalState = new BlockWorld(goalGrid, 3, 0);
        boolean run = true;
        while (run) {
            System.out.println("Select function:");
            System.out.println("1 - Single test");
            System.out.println("2 - DFS Average");
            System.out.println("3 - Scalability data");
            System.out.println("4 - Exit");
            int input = main.readInt(1, 4);
            if(input==1) {
                System.out.println("Select search method: ");
                System.out.println("1 - Depth First Search");
                System.out.println("2 - Breadth First Search");
                System.out.println("3 - Iterative Deepening Search");
                System.out.println("4 - A* Search");
                input = main.readInt(1, 4);
                if (input == 1) {
                    System.out.println("Running DFS...");
                    List<String> path = main.dfs(startState, goalState).getPath();
                    for (String s : path) {
                        System.out.println(s);
                    }
                } else if (input == 2) {
                    System.out.println("Running BFS...");
                    List<String> path = main.bfs(startState, goalState).getPath();
                    for (String s : path) {
                        System.out.println(s);
                    }
                } else if (input == 3) {
                    System.out.println("Running IDS...");
                    List<String> path = main.ids(startState, goalState).getPath();
                    for (String s : path) {
                        System.out.println(s);
                    }
                } else if (input == 4) {
                    System.out.println("Running A*...");
                    List<String> path = main.ash(startState, goalState).getPath();
                    for (String s : path) {
                        System.out.println(s);
                    }
                }
            }else if(input==2){
                System.out.println("How many iterations? (1-1000)");
                input=main.readInt(1, 1000);
                int total =0;
                for(int i=0;i<input;i++){
                    total+=main.dfs(startState,goalState).getExpansions();
                }
                System.out.println("Average: "+total/input);
            }else if(input==3){
                try {
                    main.scalabilityStudy(startState, goalState);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else{
                run = false;
            }
        }
    }
    public int readInt(int lower, int upper){
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int input = lower-1;
        while (input<lower||input>upper) {
            System.out.print(">");
            try {
                input= Integer.parseInt(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException ignored){}
        }
        return input;
    }
    public void scalabilityStudy(BlockWorld startState, BlockWorld goalState) throws FileNotFoundException {
        List<String> path = ash(startState, goalState).getPath();
        PrintStream writer = new PrintStream("scalabilityData.txt");
        writer.println("Solution Depth\tDFS\tBFS\tIDS\tA*");
        int maxDepth = path.size();
        for(int i=maxDepth;i>=0;i--){
            BlockWorld state = startState;
            for(int j=0;j<i;j++){
                String dir = path.get(j);
                if(Objects.equals(dir, "UP")){
                    state = moveAgent(state, 0);
                }else if(Objects.equals(dir, "DOWN")){
                    state = moveAgent(state, 1);
                }else if(Objects.equals(dir, "LEFT")){
                    state = moveAgent(state, 2);
                }else if(Objects.equals(dir, "RIGHT")){
                    state = moveAgent(state, 3);
                }
            }
            int depth = maxDepth-i;
            int dfs = 0;
            //for(int k=0;k<100;k++) {
                dfs+=dfs(state, goalState).getExpansions();
            //}
            //dfs=dfs/100;
            int bfs = bfs(state, goalState).getExpansions();
            int ids;
            if(depth<15) {
                ids = ids(state, goalState).getExpansions();
            }else{
                ids = 0;
            }
            int ash = ash(state, goalState).getExpansions();
            writer.println(depth+"\t"+dfs+"\t"+bfs+"\t"+ids+"\t"+ash);
        }
    }

    class IntInABox{
        private int val;
        public IntInABox(int val) {
            this.val = val;
        }
        public void inc(){
            val++;
        }
        public int getVal() {
            return val;
        }
    }

    class Result{
        private int expansions;
        private List<String> path;
        public Result(int expansions, List<String> path) {
            this.expansions = expansions;
            this.path = path;
        }
        public int getExpansions() {
            return expansions;
        }
        public List<String> getPath() {
            return path;
        }
    }
}
