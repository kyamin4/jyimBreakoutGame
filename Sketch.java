import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;
import processing.sound.SoundFile;

public class Sketch extends PApplet {

  public static int BALL_RADIUS = 8;
  public static int BALL_DIAMETER = BALL_RADIUS * 2;
  public static int PADDLE_WIDTH = 100;
  public static int PADDLE_HEIGHT = 10;
  public static int BRICK_WIDTH = 44;
  public static int BRICK_HEIGHT = 20;

  // Possible types of circle-rect collsions:
  public static final int NO_COLLISION = 0;
  public static final int VER_COLLISION = 1;
  public static final int HOR_COLLISION = 2;
  public static final int COR_COLLISION = 3;

  private SoundFile startScreenSound;
  // TODO: declare sound and image files here
  private PImage startBackground;
  private PImage gameOverBackground;
  private PImage winBackground;
  private PImage gameBackground;

  private SoundFile sidesHitSound;
  private SoundFile brickBreakSound;
  private SoundFile paddleBounceSound;
  private SoundFile levelWinSound;
  private SoundFile lifeLostSound;
  private SoundFile gameOverSound;
  private SoundFile gameWinSound;

  private ArrayList<Ball> balls = new ArrayList<Ball>();

  private ArrayList<Brick[]> levels = new ArrayList<Brick[]>();
  private Brick[] bricks = null; // current level loaded
  private int currentLevel = 0;

  private boolean hasGameStarted = false;
  private boolean isGameOver = false;
  private boolean didWinGame = false;

  private int paddleX;
  private int paddleY;

  // TODO: declare any other instance variables here
  public int lives = 3;
  public int score = 0;
  public int levelBricksBroken = 0;
  public boolean didGameOverPlay = false;
  public boolean didjaWinSon = false;

  // create levels
  Brick[] level1 = new Brick[50];
  Brick[] level2 = new Brick[40];
  Brick[] level3 = new Brick[61];

  public void settings() {
    size(480, 480);
  }

  public void setup() {
    size(480, 480);

    // Initial ball position
    Ball startingBall = new Ball(this, width / 2, height - PADDLE_HEIGHT - BALL_RADIUS);
    balls.add(startingBall);

    paddleX = width / 2 - PADDLE_WIDTH / 2;
    paddleY = height - PADDLE_HEIGHT;

    setupLevels();

    // TODO: load sounds and images
    startBackground = loadImage("backgroundStart.jpg");
    startBackground.resize(width, height);
    gameOverBackground = loadImage("backgroundGameOver.jpg");
    gameOverBackground.resize(width, height);
    winBackground = loadImage("winBackground.jpg");
    winBackground.resize(width,height);
    gameBackground = loadImage("gameBackground.jpg");
    gameBackground.resize(width,height);

    sidesHitSound = new SoundFile(this, "sidesHitSound.mp3");
    brickBreakSound = new SoundFile(this, "brickBreakSound.mp3");
    paddleBounceSound = new SoundFile(this, "paddleBounceSound.mp3");
    levelWinSound = new SoundFile(this, "levelWinSound.mp3");
    lifeLostSound = new SoundFile(this, "lifeLostSound.mp3");
    gameOverSound = new SoundFile(this, "gameOverSound.mp3");
    gameWinSound = new SoundFile(this, "gameWinSound.mp3");
    // startScreenSound = new SoundFile(this, "WiiSports.mp3");
    // startScreenSound.play();
  }

  public void setupLevels() {
    // TODO: create the first level bricks
    levels.clear();
    int redColor;
    int greenColor;
    int blueColor;

    // level 1
    redColor = 25;
    greenColor = 25;
    blueColor = 25;
    for (int i = 0; i < 10; i++) {
      level1[i] = new Brick(this, 0, 25 * i,color(redColor, greenColor, blueColor));
      level1[i + 10] = new Brick(this, 100, 25 * i,color(redColor, greenColor, blueColor));
      level1[i + 20] = new Brick(this, 200, 25 * i,color(redColor, greenColor, blueColor));
      level1[i + 30] = new Brick(this, 300, 25 * i,color(redColor, greenColor, blueColor));
      level1[i + 40] = new Brick(this, 400, 25 * i,color(redColor, greenColor, blueColor));
      redColor += 28;
      greenColor += 7;
      blueColor += 14;
    }

    // level 2
    redColor = 0;
    greenColor = 125;
    blueColor = 125;
    for (int i = 0; i < 10; i++) {
      level2[i] = new Brick(this, 50 * i, 20, color(redColor + 75, greenColor + 50, blueColor + 25));
      level2[i + 10] = new Brick(this, 50 * i, 45, color(redColor + 100, greenColor + 75, blueColor + 50));
      level2[i + 20] = new Brick(this, 50 * i, 70, color(redColor + 125, greenColor + 100, blueColor + 75));
      level2[i + 30] = new Brick(this, 50 * i, 95, color(redColor + 150, greenColor + 125, blueColor + 100));
      redColor += 10;
      greenColor += 20;
      blueColor += 30;
    }

    // level 3
    // ([Shape number], r, g, b)
    createLevel3(0, 180, 0, 0);
    createLevel3(1, 180, 180, 0);
    createLevel3(2, 0, 180, 0);

    // TODO: load the level bricks into the levels arraylist
    levels.add(level1);
    levels.add(level2);
    levels.add(level3);
  }

  public void draw() {
    if (!hasGameStarted) {
      drawGameStartScreen();
    } else if (!isGameOver) {
      drawGamePlayScreen();
    } else {
      drawGameOverScreen();
    }
  }

  /**
   * Draws the start screen.
   */
  public void drawGameStartScreen() {
    background(startBackground);
    fill(0);
    textAlign(CENTER);
    text("Click to start!", width / 2, height / 2 - 100);
    text("Move mouse to control paddle", width / 2, height / 2 - 50);
    text("Break all the bricks without letting the ball touch the bottom!", width / 2, height / 2);
    text("Watch out when your score increases, something might happen!", width / 2, height / 2 + 50);
  }

  /**
   * Draws the game play screen.
   */
  public void drawGamePlayScreen() {
    background(gameBackground);
    fill(255);
    text("Score: " + score, 100, height - 100);
    text("Lives: " + lives, width - 100, height - 100);
    text("Current Level: " + (currentLevel + 1), width / 2, height - 100);
    // Draw the ball.
    for (Ball ball : balls) {
      ball.draw();
      // ballSpeedX used to determine when sounds should be played
      float ballSpeedX = ball.getSpeedX();
      // TODO: check if the ball is out of bounds
      if (isOutOfBounds(ball)) {
        // when losing a life, return paddle and ball to start position
        ball.setX(paddleX + PADDLE_WIDTH / 2);
        ball.setY(height - PADDLE_HEIGHT - BALL_RADIUS);
        ball.setSpeedX(0);
        ball.setSpeedY(0);
        lives--;

        // check to see if game over/
        if (lives <= 0) {
          isGameOver = true;
        }

        // if game isnt over, play normal sound
        if (isGameOver == false) {
          lifeLostSound.play();
        }
      }
      // TODO: check for wall collisions
      if (didHitSide(ball) && ballSpeedX != 0) {
        sidesHitSound.play();
        ball.reverseSpeedX();
      }
      if (didHitTop(ball) && ballSpeedX != 0) {
        sidesHitSound.play();
        ball.reverseSpeedY();
      }

      // TODO: check for brick collisions
      int collision = checkForCollisions(ball);
      if (collision == HOR_COLLISION) {
        brickBreakSound.play();
        ball.reverseSpeedY();
        score++;
        levelBricksBroken++;
      }
      if (collision == VER_COLLISION) {
        brickBreakSound.play();
        ball.reverseSpeedX();
        score++;
        levelBricksBroken++;
      }
      if (collision == COR_COLLISION) {
        brickBreakSound.play();
        ball.reverseSpeedX();
        ball.reverseSpeedY();
        score++;
        levelBricksBroken++;
      }
      // TODO: check for paddle collision
      int paddleCollision = isBallCollidingWithPaddle(ball);

      if (paddleCollision == HOR_COLLISION && ballSpeedX != 0) {
        paddleBounceSound.play();
        ball.reverseSpeedY();
      }
      if (paddleCollision == VER_COLLISION && ballSpeedX != 0) {
        paddleBounceSound.play();
        ball.reverseSpeedX();
      }
      if (paddleCollision == COR_COLLISION && ballSpeedX != 0) {
        paddleBounceSound.play();
        ball.reverseSpeedX();
        ball.reverseSpeedY();
      }

      // check to see if all bricks broken, resets ball if true + change level
      if (areAllBricksBroken()) {
        // TODO: increase level, resets ball, resets bricks broken counter
        levelWinSound.play();
        currentLevel++;
        levelBricksBroken = 0;
        ball.setX(paddleX + PADDLE_WIDTH / 2);
        ball.setY(height - PADDLE_HEIGHT - BALL_RADIUS);
        ball.setSpeedX(0);
        ball.setSpeedY(0);
      }
    }

    // TODO: draw the bricks
    if (currentLevel < levels.size()) {
      // check that level is valid
      Brick[] level = levels.get(currentLevel);
      for (Brick b : level) {
        if (b != null) {
          b.draw();
        }
      }
    }
    // Draw the paddle.
    fill(165, 42, 42);
    if (score <= 25) {
      rect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
    } else if (score > 25 && score <= 40) {
      rect(paddleX, paddleY, PADDLE_WIDTH / 2, PADDLE_HEIGHT);
    } else if (score > 40) {
      rect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
      for (Ball ball : balls) {
        if ((ball.getSpeedX() == 5 || ball.getSpeedX() == -5) && (ball.getSpeedY() == 3 || ball.getSpeedY() == -3)) {
          ball.setSpeedX(8);
          ball.setSpeedY(-6);
        }
      }
    }
  }

  /**
   * Draws the game over screen. Should allow players to started the game.
   */
  public void drawGameOverScreen() {
    if (!didjaWinSon) {
      if (didGameOverPlay == false) {
        gameOverSound.play();
        didGameOverPlay = true;
      }
      background(gameOverBackground);
    } 
    
    else if (didjaWinSon) {
      if (didGameOverPlay == false) {
        gameWinSound.play();
        didGameOverPlay = true;
      }
      background(winBackground);
    }
    
    fill(255);
    textAlign(CENTER);
    text("Final Score: " + score, width / 2, height - 75);
    text("Click to restart!", width / 2, height - 50);
  }

  public void mouseMoved(MouseEvent event) {
    int x = event.getX();

    // center ball with smaller paddle
    if (score > 25 && score <= 40) {
      paddleX = x - PADDLE_WIDTH / 4;
    } else {
      paddleX = x - PADDLE_WIDTH / 2;
    }

    for (Ball ball : balls) {
      if (ball.getSpeedX() == 0 && ball.getSpeedY() == 0) {
        ball.setX(x);
      }
    }
  }

  public void mouseReleased(MouseEvent event) {
    if (!hasGameStarted) {
      hasGameStarted = true;
    } else if (!isGameOver) {
      for (Ball ball : balls) {
        if (ball.getSpeedX() == 0 && ball.getSpeedY() == 0 && didHitSide(ball) == false) {
          if (score <= 20) {
            ball.setSpeedX(5);
            ball.setSpeedY(-3);
          } else if (score > 20) {
            ball.setSpeedX(8);
            ball.setSpeedY(-6);
          }
        }
      }
    } else if (isGameOver) {
      restartGame();
    }
  }

  // when space key pressed, increase level
  public void keyReleased() {
    Brick[] level = levels.get(currentLevel);
    if (key == ' ') {
      levelBricksBroken = level.length;
    }
  }

  /**
   * Return true if the ball has hit the left or right sides.
   */
  public boolean didHitSide(Ball ball) {
    if (ball.getX() - ball.getRadius() < 0 || ball.getX() + ball.getRadius() > width) {
      return true;
    }

    return false;
  }

  /**
   * Return true if the ball has hit the top side.
   */
  public boolean didHitTop(Ball ball) {
    // TODO
    if (ball.getY() - ball.getRadius() < 0) {
      return true;
    }
    return false;
  }

  /**
   * Return true with the ball passed through the bottom.
   */
  public boolean isOutOfBounds(Ball ball) {
    // TODO
    if (ball.getY() - ball.getRadius() > height) {
      return true;
    }
    return false;
  }

  /**
   * Return true when the ball is colliding with the paddle.
   */
  public int isBallCollidingWithPaddle(Ball ball) {
    if (isBallCollidingWithRect(ball, paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT) != NO_COLLISION) {
      return isBallCollidingWithRect(ball, paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
    } else {
      return NO_COLLISION;
    }

  }

  /**
   * Detects whether the ball is colliding with a brick. Use a loop to check every
   * brick for collisions. If a brick has been hit, remove it and return the
   * specific collision code (HOR_COLLISION, VER_COLLSION, or COR_COLLISION) . If
   * no bricks are being hit, return NO_COLLISION.
   */
  public int checkForCollisions(Ball ball) {
    // TODO
    Brick[] level = levels.get(currentLevel);

    for (int i = level.length - 1; i >= 0; i--) {
      Brick b = level[i];

      if (b != null) {
        int collision = isBallCollidingWithRect(ball, b.getX(), b.getY(), b.getWidth(), b.getHeight());

        if (collision != NO_COLLISION) {
          level[i] = null;
          return collision;
        }
      }
    }
    return NO_COLLISION;
  }

  /**
   * Loops over every brick. If an unbroken brick is found, true false. If every
   * brick has been broken, return true.
   */
  public boolean areAllBricksBroken() {
    Brick[] level = levels.get(currentLevel);

    if (levelBricksBroken >= level.length) {
      // level is over/no more bricks, reset levelBricksBroken per level and increase
      // level
      if (currentLevel < levels.size() - 1){
        return true;
      } else{
        didjaWinSon = true;
        isGameOver = true;
      }

    }
    return false;
  }

  // function to create level 3, makes a "circle" of bricks. creates 20 new bricks
  // each run
  public void createLevel3(int n, int r, int g, int b) {
    for (int i = 0; i < 5; i++) {
      // (width / 2 - 25) to center blocks better
      // top half
      // right side
      level3[(i * 4 + 1) + (20 * n)] = new Brick(this, (width / 2 - 25) + (25 * (i + 1)), 20 + (25 * (i + 1)),
          color(r, g, b));
      // left side
      level3[(i * 4 + 2) + (20 * n)] = new Brick(this, (width / 2 - 25) - (25 * (i + 1)), 20 + (25 * (i + 1)),
          color(r, g, b));
      // bottom half
      // right side
      level3[(i * 4 + 3) + (20 * n)] = new Brick(this, (width / 2 - 25) + (25 * (5 - i)), 20 + (25 * (6 + i)),
          color(r, g, b));
      // left side
      level3[(i * 4 + 4) + (20 * n)] = new Brick(this, (width / 2 - 25) - (25 * (5 - i)), 20 + (25 * (6 + i)),
          color(r, g, b));
    }
  }

  // Restarts the game by reseting all of the instance variables.

  public void restartGame() {
    isGameOver = false;
    hasGameStarted = false;
    lives = 3;
    score = 0;
    levelBricksBroken = 0;
    currentLevel = 0;
    didGameOverPlay = false;
    didjaWinSon = false;
    setupLevels();
  }

  /*
   * Checks if the Ball object is colliding with the rectangle dimensions
   * provided.
   */
  public int isBallCollidingWithRect(Ball ball, int rx, int ry, int w, int h) {
    float cx = ball.getX();
    float cy = ball.getY();

    float minX = max(rx, min(cx, rx + w));
    float minY = max(ry, min(cy, ry + h));

    noStroke();
    line(cx, cy, minX, minY);

    if (dist(cx, cy, minX, minY) <= ball.getRadius()) {
      if (cx > rx && cx < rx + w) {
        return HOR_COLLISION;
      }
      if (cy > ry && cy < ry + h) {
        return VER_COLLISION;
      }
      return COR_COLLISION;
    } else {
      return NO_COLLISION;
    }
  }
}