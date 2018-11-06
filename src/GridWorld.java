import java.util.*;

public class GridWorld {
    private int size;
    private Tile[][] tiles;
    private Agent agent;
    private Point agentPos;
    public static final int UP = 0, LEFT = 1, DOWN = 2, RIGHT = 3;
    GridWorld(int size, Point agentPos, Map<Point, Tile> blocks){
        this.size = size;
        tiles = new Tile[size][size];
        // Fill grid with white tiles
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                tiles[i][j] = new Tile();
            }
        }
        // Place provided blocks, overriding white tiles
        for(Point p : blocks.keySet()){
            tiles[p.getX()][p.getY()] = blocks.get(p);
        }
        // Place agent
        agent = new Agent();
        this.agentPos = agentPos;
        tiles[agentPos.getX()][agentPos.getY()] = agent;
    }
    public void moveAgent(int direction) throws Exception{
        int oldX = agentPos.getX(), oldY=agentPos.getY(), newX, newY;
        switch (direction){
            case UP:
                newX = oldX;
                newY = oldY+1;
                break;
            case DOWN:
                newX = oldX;
                newY = oldY-1;
                break;
            case LEFT:
                newX = oldX-1;
                newY = oldY;
                break;
            case RIGHT:
                newX=oldX+1;
                newY = oldY;
                break;
            default:
                throw new Exception("Direction must be UP, DOWN, LEFT or RIGHT");
        }
        try {
            Tile tile = tiles[newX][newY];
            tiles[newX][newY] = agent;
            tiles[oldX][oldY] = tile;
            agentPos = new Point(newX, newY);
        }catch (IndexOutOfBoundsException e){
            throw new Exception("Invalid move");
        }
    }
    public Map<Point, Tile> getBlockPositions(){
        Map<Point, Tile> blockPositions = new HashMap<>();
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(tiles[i][j].isBlock()){
                    blockPositions.put(new Point(i, j), tiles[i][j]);
                }
            }
        }
        return blockPositions;
    }
    public List<Integer> getValidMoves(){
        List<Integer> validMoves = new ArrayList<>();
        if(agentPos.getY()<size-1){
            validMoves.add(UP);
        }
        if (agentPos.getX()<size-1){
            validMoves.add(RIGHT);
        }
        if(agentPos.getY()>0){
            validMoves.add(DOWN);
        }
        if (agentPos.getX()>0){
            validMoves.add(LEFT);
        }
        return validMoves;
    }
}
