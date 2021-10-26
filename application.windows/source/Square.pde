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
  boolean isMine(){
     return isMine; 
  }
  void placeMine(){
    isMine = true;
  }
  boolean isRevealed(){
    return revealed;
  }
  void reveal(){
    revealed = true;
  }
  void setSurroundingMines(int mines){
    this.mines = mines;
  }
  int getMines(){
    return mines;
  }
  void flag(){
    flagged = true;
  }
  void unFlag(){
    flagged = false;
  }
  boolean isFlagged(){
    return flagged;
  }
  // draws the square on the canvas
  void show() { //display this square, either a food or a snake
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
    circle(CENTER_X, CENTER_Y, 2 * (2 * RADIUS / 2.5)); // inner circle is a third of the radius
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
