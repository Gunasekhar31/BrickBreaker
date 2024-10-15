import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BrickBreaker extends JPanel implements ActionListener {
    private Timer timer;
    private Paddle paddle;
    private Ball ball;
    private Brick[][] bricks;
    private final int ROWS = 5;
    private final int COLS = 8;
    private int score = 0;

    public BrickBreaker() {
        paddle = new Paddle(350, 550);
        ball = new Ball(400, 300);
        bricks = new Brick[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                bricks[row][col] = new Brick(col * 100 + 20, row * 30 + 50);
            }
        }

        timer = new Timer(10, this);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                paddle.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                paddle.keyReleased(e);
            }
        });
        startGame();
    }

    public void startGame() {
        score = 0;
        resetBricks();
        ball = new Ball(400, 300);
        timer.start();
    }

    private void resetBricks() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                bricks[row][col] = new Brick(col * 100 + 20, row * 30 + 50);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paddle.draw(g);
        ball.draw(g);
        for (Brick[] row : bricks) {
            for (Brick brick : row) {
                if (brick.isVisible()) {
                    brick.draw(g);
                }
            }
        }
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ball.move();
        ball.checkCollision(paddle);

        for (Brick[] row : bricks) {
            for (Brick brick : row) {
                if (brick.isVisible() && ball.checkCollision(brick)) {
                    brick.setVisible(false);
                    ball.reverseDirection();
                    score += 10;
                }
            }
        }

        // Game Over logic (if the ball falls below the paddle)
        if (ball.getY() > getHeight()) {
            timer.stop();
            int response = JOptionPane.showConfirmDialog(this, "Game Over! Your score: " + score + "\nWould you like to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                startGame();
            } else {
                System.exit(0);
            }
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker");
        BrickBreaker game = new BrickBreaker();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Paddle {
    private int x, y;
    private final int width = 100;
    private final int height = 20;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public Paddle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
        move();
    }

    public void move() {
        if (leftPressed && x > 0) {
            x -= 5;
        }
        if (rightPressed && x < 800 - width) {
            x += 5;
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }

    // Getter methods for position and size
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

class Ball {
    private int x, y, diameter = 20;
    private int xSpeed = 2, ySpeed = -2;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, diameter, diameter);
    }

    public void move() {
        x += xSpeed;
        y += ySpeed;

        if (x < 0 || x > 800 - diameter) {
            xSpeed = -xSpeed;
        }
        if (y < 0) {
            ySpeed = -ySpeed;
        }
    }

    public void reverseDirection() {
        ySpeed = -ySpeed;
        y = y - diameter; // Adjust position to avoid immediate re-collision
    }

    public int getY() {
        return y;
    }

    public boolean checkCollision(Paddle paddle) {
        return (x + diameter > paddle.getX() && x < paddle.getX() + paddle.getWidth() && y + diameter > paddle.getY() && y + diameter < paddle.getY() + paddle.getHeight());
    }

    public boolean checkCollision(Brick brick) {
        return (x + diameter > brick.getX() && x < brick.getX() + brick.getWidth() && y + diameter > brick.getY() && y < brick.getY() + brick.getHeight());
    }
}

class Brick {
    private int x, y, width = 80, height = 20;
    private boolean visible;

    public Brick(int x, int y) {
        this.x = x;
        this.y = y;
        this.visible = true;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
