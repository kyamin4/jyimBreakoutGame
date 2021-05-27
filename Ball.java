import processing.core.PApplet;

public class Ball
{
  private PApplet app;
  private float x;
  private float y;
  private float speedX;
  private float speedY;
  private float radius = Sketch.BALL_DIAMETER / 2;

  public Ball(PApplet a, int ix, int iy)
  {
    app = a;
    x = ix;
    y = iy;
  }

  public float getX()
  {
    return x; 
  }
  
  public float getY()
  {
    return y;
  }

  public void setX(int ix)
  {
    x = ix;
  }

  public void setY(int iy)
  {
    y = iy;
  }
  
  public float getRadius() {
    return radius;
  }

  public void reverseSpeedX()
  {
    speedX = -speedX;
  }

  public void reverseSpeedY()
  {
    speedY = -speedY;
  }

  public void setSpeedX(int isx)
  {
    speedX = isx;
  }

  public void setSpeedY(int isy)
  {
    speedY = isy;
  }

  public float getSpeedX() {
    return speedX;
  }

  public float getSpeedY() {
    return speedY;
  }

  public void draw()
  {
    x += speedX;
    y += speedY;

    app.fill(30, 144, 255);
    app.noStroke();
    app.ellipse(x, y, radius * 2, radius * 2);
  }
}