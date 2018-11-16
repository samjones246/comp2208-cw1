import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
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

    public void dfs(BlockWorld start, BlockWorld goal){
        List<Integer> directions = new ArrayList<>();
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
                System.out.println(lastAction);
            }
            expansions++;
            if(Arrays.deepEquals(state.getGrid(), goal.getGrid())){
                System.out.println("Reached goal state after "+expansions+" expansions.");
                break;
            }
            Collections.shuffle(directions);
            for(int i=0;i<4;i++){
                try {
                    s.push(moveAgent(state, directions.get(i)));
                    lastAction = dirStrings[directions.get(i)];
                }catch (IndexOutOfBoundsException ignored){}
            }
        }

    }
    public void bfs(BlockWorld start, BlockWorld goal){
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
        int expansions = 0;
        Queue<BlockWorld> queue = new LinkedList<>();
        HashMap<BlockWorld, BlockWorld> parent = new HashMap<>();
        HashMap<BlockWorld, String> action = new HashMap<>();
        queue.add(start);
        parent.put(start, null);
        action.put(start, null);
        BlockWorld last = start;
        while (!queue.isEmpty()){
            BlockWorld state=queue.remove();
            if(Arrays.deepEquals(state.getGrid(), goal.getGrid())){
                last=state;
                break;
            }
            for(int i=0;i<4;i++){
                try{
                    BlockWorld child = moveAgent(state, i);
                    queue.add(child);
                    parent.put(child, state);
                    action.put(child, dirStrings[i]);
                }catch (IndexOutOfBoundsException ignored){}
            }
            expansions++;
        }
        List<String> path = new ArrayList<>();
        while(parent.get(last)!=null){
            path.add(action.get(last));
            last = parent.get(last);
        }
        Collections.reverse(path);
        for(String s : path){
            System.out.println(s);
        }
        System.out.println("Reached goal state after "+expansions+" expansions.");
    }
    public void ids(BlockWorld start, BlockWorld goal){
        HashMap<BlockWorld, BlockWorld> parent;
        HashMap<BlockWorld, String> action;
        int depth = 0;
        IntInABox expansions = new IntInABox(0);
        List<String> path = null;
        while(path==null){
            parent = new HashMap<>();
            action = new HashMap<>();
            parent.put(start, null);
            action.put(start, null);
            path=dls(start, goal, depth, parent, action, expansions);
            depth++;
        }
        for(String s : path){
            System.out.println(s);
        }
        System.out.println("Reached goal state after "+expansions.getVal()+" expansions");
    }
    private List<String> dls(BlockWorld state, BlockWorld goal, int depth, HashMap<BlockWorld, BlockWorld> parent, HashMap<BlockWorld, String> action, IntInABox expansions){
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
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
                    List<String> path = dls(child, goal, depth - 1, parent, action, expansions);
                    if (path != null) {
                        return path;
                    }
                }catch (IndexOutOfBoundsException ignored){}
            }
            return null;
        }
    }
    public void ash(BlockWorld start, BlockWorld goal){
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
        List<BlockWorld> frontier = new ArrayList<>();
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
            if (Arrays.deepEquals(state.getGrid(), goal.getGrid())) {
                last = state;
                break;
            }
            frontier.remove(state);
            expansions++;
            for(int i=0;i<4;i++){
                try {
                    BlockWorld child = moveAgent(state, i);
                    distanceFromRoot.put(child, distanceFromRoot.get(state)+1);
                    frontier.add(child);
                    parent.put(child, state);
                    score.put(child, evaluate(child, goal)+distanceFromRoot.get(child));
                    action.put(child, dirStrings[i]);
                }catch (IndexOutOfBoundsException ignored){}
            }
        }
        List<String> path = new ArrayList<>();
        while(parent.get(last)!=null){
            path.add(action.get(last));
            last = parent.get(last);
        }
        Collections.reverse(path);
        for(String s : path){
            System.out.println(s);
        }
        System.out.println("Reached goal state after "+expansions+" expansions.");

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
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                startGrid[i][j] = ' ';
            }
        }
        char[][] goalGrid = new char[size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                goalGrid[i][j] = ' ';
            }
        }
        startGrid[0][0] = 'A';
        startGrid[1][0] = 'B';
        startGrid[2][0] = 'C';
        goalGrid[1][2] = 'A';
        goalGrid[1][1] = 'B';
        goalGrid[1][0] = 'C';
        BlockWorld startState = new BlockWorld(startGrid, 3, 0);
        BlockWorld goalState = new BlockWorld(goalGrid, 3, 0);
        System.out.println("---Depth First Search---");
        main.dfs(startState,goalState);
        System.out.println("---Iterative Deepening Search---");
        main.ids(startState,goalState);
        System.out.println("---A* Search---");
        main.ash(startState,goalState);
        System.out.println("---Breadth First Search---");
        main.bfs(startState,goalState);
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
}
