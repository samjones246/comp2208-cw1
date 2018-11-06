public class Tile {
    private boolean block;
    private char value;
    Tile(char value){
        block = true;
        this.value=value;
    }
    Tile(){
        block = false;
    }
    public boolean isBlock() {
        return block;
    }

    public char getValue() throws Exception {
        if(block) {
            return value;
        }else throw new Exception("No value: tile is not a block");
    }
}
