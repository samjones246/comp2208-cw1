import java.util.Arrays;

public class BlockWorld {
    private char[][] grid;
    private int agentX, agentY;

    public BlockWorld(char[][] grid, int agentX, int agentY) {
        this.grid = grid;
        this.agentX = agentX;
        this.agentY = agentY;
    }

    public char[][] getGrid() {
        return grid;
    }

    public int getAgentX() {
        return agentX;
    }

    public int getAgentY() {
        return agentY;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BlockWorld){
            if(Arrays.deepEquals(((BlockWorld) obj).getGrid(), grid)){
                if(((BlockWorld) obj).getAgentX()==agentX&&((BlockWorld) obj).getAgentY()==agentY){
                    return true;
                }
            }
        }
        return false;
    }
}
