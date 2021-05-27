import processing.core.PApplet;

public class Brick
{
  private PApplet app;
  private int x; 
  private int y;
  private int w = Sketch.BRICK_WIDTH;
  private int h = Sketch.BRICK_HEIGHT;
  private int c;

  public Brick(PApplet a, int initX, int initY, int initColor)
  {
    app = a;
    x = initX;
    y = initY;
    c = initColor;
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  public int getWidth() {
    return w;
  }
  
  public int getHeight() {
    return h;
  }

  public void draw()
  {
    app.fill(c);
    app.noStroke();
    app.rect(x, y, w, h);
  }
}