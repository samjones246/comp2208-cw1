import java.util.*;

public class Main {
    public BlockWorld moveAgent(BlockWorld state, int direction) throws UnsupportedOperationException, IndexOutOfBoundsException{
        int x1,y1,x2,y2;
        x1 = x2 = state.getAgentX();
        y1 = y2 = state.getAgentY();
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
        char[][] grid = state.getGrid();
        grid[x1][y1] = grid[x2][y2];
        grid[x2][y2] = ' ';
        newState = new BlockWorld(grid, x2, y2);
        return newState;
    }

    public void dfs(BlockWorld start, BlockWorld goal){
        List<Integer> directions = new ArrayList<>();
        String[] dirStrings = new String[]{"UP", "DOWN", "LEFT", "RIGHT"};
        for(int i=0;i<4;i++){
            directions.add(i);
        }
        Stack<BlockWorld> s = new Stack<>();
        s.push(start);
        int expansions = 0;
        while (!s.empty()){
            BlockWorld state = s.pop();
            if(expansions!=0){
                System.out.println();
            }
            Collections.shuffle(directions);
            for(int i=0;i<4;i++){
                try {
                    s.push(moveAgent(state, directions.get(i)));
                }catch (IndexOutOfBoundsException ignored){}
            }
        }

    }
    public void bfs(BlockWorld start, BlockWorld goal){
    }
    public void ids(BlockWorld start, BlockWorld goal){
    }
    public void ash(BlockWorld start, BlockWorld goal){
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
        char[][] goalGrid = startGrid.clone();
        startGrid[0][0] = 'A';
        startGrid[1][0] = 'B';
        startGrid[2][0] = 'C';
        goalGrid[1][2] = 'A';
        goalGrid[1][1] = 'B';
        goalGrid[1][0] = 'C';
        BlockWorld startState = new BlockWorld(startGrid, 3, 0);
        BlockWorld goalState = new BlockWorld(goalGrid, 3, 0);
        main.dfs(startState,goalState);
        main.bfs(startState,goalState);
        main.ids(startState,goalState);
        main.ash(startState,goalState);
    }
}
