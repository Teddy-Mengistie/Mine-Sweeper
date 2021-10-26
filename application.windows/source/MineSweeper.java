/* autogenerated by Processing revision 1277 on 2021-10-25 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.ArrayDeque;
import java.util.Deque;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class MineSweeper extends PApplet {




boolean gameStarted = false;
public int sizeOfGame = 30;
PFont font;
Square[][] board = new Square[sizeOfGame][sizeOfGame];
// -1 if bomb and 0 to 9 bombs around a square
int dimension = 900/sizeOfGame;
static PShape flag, mine, unrevealed, one, two, three, four, five, six, seven, eight;
int mouseXIndex, mouseYIndex;
// returns a square given coordinates or null if out of grid
 public Square getSquare(int i, int j) {
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
 public Square[] getSurrounding(int i, int j){
   return new Square[]{getSquare(i, j + 1), getSquare(i + 1, j), getSquare(i, j - 1), getSquare(i - 1, j), getSquare(i + 1, j + 1), getSquare(i - 1, j + 1), getSquare(i + 1, j - 1), getSquare(i - 1, j - 1)};
}

 public void endGame(){
  // reveal every square
  for (Square[] a: board) {
        for (Square s: a) {
            s.reveal();
            noLoop();
        }
    }
}
// expands out and reveals squares that are not mines 
 public void expandSquare(int maxDepth, int x, int y){
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

 public void expandEmptySquare(int x, int y){
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
 public void settings(){
  size((int)(dimension * sizeOfGame), (int)(dimension * sizeOfGame));
}
 public void setup(){
  // shape setup
  // TODO
  font = createFont("Blockletter.otf", Math.abs(55 - sizeOfGame));
  for(int i = 0; i < board.length; i++){
    for(int j = 0; j < board[i].length; j++){
      board[i][j] = new Square(i, j);
    }
  }
}
 public void draw(){
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
 public void win(){
  background(200);
  textAlign(CENTER);
  textSize(72);
  textFont(font);
  stroke(211, 112, 211);
  fill(112, 111, 112);
  int coordinate = dimension*sizeOfGame/2;
  text("YOU WIN!",coordinate, coordinate);
}
 public void startGame(int x, int y){
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
 public void activate(){
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
      expandSquare((int)((Math.random() + 0.01f) * (sizeOfGame) / 10), j, i);
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
 public void mousePressed(){
   mouseXIndex = mouseX/dimension;
   mouseYIndex = mouseY/dimension;
}
 public void mouseReleased(){
  int i = mouseY/dimension;
  int j = mouseX/dimension;
  if(mouseYIndex == i && mouseXIndex == j){
    activate();
  }
}
class Square {
  int y;
  int x;
  final int X; // in pixels
  final int Y; // in pixels
  private int mines;
  private boolean isMine;
  private boolean revealed;
  private boolean flagged;
  Square(int y, int x){
    this.y = y;
    this.x = x;
    X = dimension * x;
    Y = dimension * y;
    revealed = false;
  }
   public boolean isMine(){
     return isMine; 
  }
   public void placeMine(){
    isMine = true;
  }
   public boolean isRevealed(){
    return revealed;
  }
   public void reveal(){
    revealed = true;
  }
   public void setSurroundingMines(int mines){
    this.mines = mines;
  }
   public int getMines(){
    return mines;
  }
   public void flag(){
    flagged = true;
  }
   public void unFlag(){
    flagged = false;
  }
   public boolean isFlagged(){
    return flagged;
  }
  // draws the square on the canvas
   public void show() { //display this square, either a food or a snake
      // TODO
      fill(200);
      noStroke();
      square(X, Y, dimension);
      if(!isRevealed()){
        // draw a box
        drawBox();
        if(flagged){
          //draw a flag on top
          drawFlag();
        }
      }else if(isMine){
        // draw a mine
        drawMine();
      }else if(mines == 0){
        fill(200);
        square(X, Y, dimension);
      }else{
        // check each possible number and draw
        
        textAlign(CENTER);
        textSize(50 - sizeOfGame/3);
        textFont(font);
        fill(0);
        switch(mines){
          // do 1 through 8
          case 1:drawOne();break;
          case 2:drawTwo();break;
          case 3:drawThree();break;
          case 4:drawFour();break;
          case 5:drawFive();break;
          case 6:drawSix();break;
          case 7:drawSeven();break;
          case 8:drawEight();break;
        }
      }
  }
  
    // draws a flag on the square
  private final void drawFlag(){
    fill(255, 0, 0);
    beginShape();
    vertex(dimension/3 + X, dimension/2 + Y);
    vertex(2*dimension/3 + X, dimension/3 + Y);
    vertex(2*dimension/3 + X, 2*dimension/3 + Y);
    endShape(CLOSE);
    stroke(0);
    line(2*dimension/3 + X, dimension/3 + Y, 2*dimension/3 + X, 2*dimension/3 + Y);
  }
  // draws a box
  private final void drawBox(){
    final float pixelsIn = dimension / 6;
    final float remainingPixels = dimension - pixelsIn;
    fill(100);
    noStroke();
    beginShape();
    vertex(X, Y);
    vertex(X + pixelsIn, Y + pixelsIn);
    vertex(X + pixelsIn, Y + remainingPixels);
    vertex(X + remainingPixels, Y + remainingPixels);
    vertex(X + dimension, Y + dimension);
    vertex(X, Y + dimension);
    endShape(CLOSE);
    fill(255);
    noStroke();
    beginShape();
    vertex(X, Y);
    vertex(X + pixelsIn, Y + pixelsIn);
    vertex(X + remainingPixels, Y + pixelsIn);
    vertex(X + remainingPixels, Y + remainingPixels);
    vertex(X + dimension, Y + dimension);
    vertex(X + dimension, Y);
    endShape(CLOSE);
  }
  private final void drawMine(){
    final float CENTER_X = dimension/2 + X;
    final float CENTER_Y = dimension/2 + Y;
    final float RADIUS = 2 * Math.abs(dimension/3 - dimension/2);
    fill(0);
    circle(CENTER_X, CENTER_Y, 2 * RADIUS); // outer circle has 2*RADIUS diameter
    fill(50, 30, 30);
    circle(CENTER_X, CENTER_Y, 2 * (2 * RADIUS / 2.5f)); // inner circle is a third of the radius
  }
  private final void drawOne(){
    text("1", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
  private final void drawTwo(){
    text("2", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
  private final void drawThree(){
    text("3", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
  private final void drawFour(){
    text("4", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
  private final void drawFive(){
    text("5", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
  private final void drawSix(){
    text("6", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
  private final void drawSeven(){
    text("7", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
  private final void drawEight(){
    text("8", dimension/2 + X, dimension/2 + Y + (55 - sizeOfGame)/3);
  }
}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "MineSweeper" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
