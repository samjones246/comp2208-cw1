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
}
