import java.util.ArrayDeque;
import java.util.Deque;

boolean gameStarted = false;
public int sizeOfGame = 30;
PFont font;
Square[][] board = new Square[sizeOfGame][sizeOfGame];
// -1 if bomb and 0 to 9 bombs around a square
int dimension = 900/sizeOfGame;
static PShape flag, mine, unrevealed, one, two, three, four, five, six, seven, eight;
int mouseXIndex, mouseYIndex;
// returns a square given coordinates or null if out of grid
Square getSquare(int i, int j) {
    for (Square[] a: board) {
        for (Square s: a) {
            if (s.y == i && s.x == j) {
                return s;
            }
        }
    }
    return null;
}

// generates bombs around this square given the amount to generate and the coordinates
Square[] getSurrounding(int i, int j){
   return new Square[]{getSquare(i, j + 1), getSquare(i + 1, j), getSquare(i, j - 1), getSquare(i - 1, j), getSquare(i + 1, j + 1), getSquare(i - 1, j + 1), getSquare(i + 1, j - 1), getSquare(i - 1, j - 1)};
}

void endGame(){
  // reveal every square
  for (Square[] a: board) {
        for (Square s: a) {
            s.reveal();
            noLoop();
        }
    }
}
// expands out and reveals squares that are not mines 
void expandSquare(int maxDepth, int x, int y){
  board[y][x].reveal();
  Deque<Square> openList = new ArrayDeque();
  Deque<Square> closedList = new ArrayDeque();
  openList.add(board[y][x]);
  int depth = 0;
  while(!openList.isEmpty()){
    Square curr = openList.poll();
    depth ++;
    if(depth > sq(2 * maxDepth)){
      break;
    }
    Square[] neighbors = getSurrounding(curr.y, curr.x);
    for(Square neighbor: neighbors){
      if(neighbor == null || neighbor.isMine() || closedList.contains(neighbor)) continue;
      board[neighbor.y][neighbor.x].reveal();
      if(neighbor.getMines() == 0) expandEmptySquare(neighbor.x, neighbor.y);
      openList.add(neighbor);
    }
    closedList.add(curr);
  }
}

void expandEmptySquare(int x, int y){
  Deque<Square> openList = new ArrayDeque();
  Deque<Square> closedList = new ArrayDeque();
  openList.add(board[y][x]);
  while(!openList.isEmpty()){
    Square curr = openList.poll();
    board[curr.y][curr.x].reveal();
    if(curr.getMines() > 0){
      closedList.add(curr);
      continue;
    }
    Square[] neighbors = getSurrounding(curr.y, curr.x);
    
    for(Square neighbor: neighbors){
      if(neighbor == null || neighbor.isMine() || closedList.contains(neighbor)) continue;
      openList.add(neighbor);
    }
    closedList.add(curr);
  }
}
void settings(){
  size((int)(dimension * sizeOfGame), (int)(dimension * sizeOfGame));
}
void setup(){
  // shape setup
  // TODO
  font = createFont("Blockletter.otf", Math.abs(55 - sizeOfGame));
  for(int i = 0; i < board.length; i++){
    for(int j = 0; j < board[i].length; j++){
      board[i][j] = new Square(i, j);
    }
  }
}
void draw(){
  background(0);
  int mines = 0;
  int minesFlagged = 0;
  for (Square[] a : board) {
      for (Square temp: a) {
          temp.show();
          if(temp.isMine()){
            mines ++;
            if(temp.isFlagged()) minesFlagged ++;
          }
      }
  }
  if(gameStarted && mines-1 <= minesFlagged) win();
  noLoop();
}
void win(){
  background(200);
  textAlign(CENTER);
  textSize(72);
  textFont(font);
  stroke(211, 112, 211);
  fill(112, 111, 112);
  int coordinate = dimension*sizeOfGame/2;
  text("YOU WIN!",coordinate, coordinate);
}
void startGame(int x, int y){
    // place mines around world and wake up each square
  for(int i = 0; i < board.length; i++){
    for(int j = 0; j < board[i].length; j++){
      double rand = Math.random() * 100;
      board[i][j] = new Square(i, j);
      if (rand < 20 && board[i][j].getMines() == 0 && i != y && j != x) board[i][j].placeMine(); // 20% possibility of square being a mine
    }
  }
  // count the amount of mines around the non mine squares and set their values
  for(int i = 0; i < board.length; i++){
    for(int j = 0; j < board[i].length; j++){
      if(board[i][j].isMine()) continue;
      int mineCount = 0;
      for(Square s: getSurrounding(i, j)){
        if(s!= null && s.isMine()) mineCount++;
      }
      board[i][j].setSurroundingMines(mineCount);
    }
  }

}
//mouse pressed and stuff
void activate(){
  int i = mouseY/dimension;
  int j = mouseX/dimension;
  Square temp = board[i][j];
  if(mouseButton == LEFT && !temp.isFlagged()){
    if(temp.isMine()){
      endGame();
      noLoop();
      //game end
    }
    board[i][j].reveal();
    if(gameStarted == false) {
      // start game
      startGame(j, i);
      expandSquare((int)((Math.random() + 0.01) * (sizeOfGame) / 10), j, i);
      gameStarted = true;
    }
    // if this is a square that has no mines, then expand it
    if(board[i][j].getMines() == 0){
      expandEmptySquare(j, i);
    }
    
    
  }else if (mouseButton == RIGHT && !temp.isRevealed()) {
  
    if(!temp.isFlagged())board[i][j].flag();
    else{
      board[i][j].unFlag();
    }
    
  }
  loop();
}
// nice controls
void mousePressed(){
   mouseXIndex = mouseX/dimension;
   mouseYIndex = mouseY/dimension;
}
void mouseReleased(){
  int i = mouseY/dimension;
  int j = mouseX/dimension;
  if(mouseYIndex == i && mouseXIndex == j){
    activate();
  }
}
