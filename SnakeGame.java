import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    // 游戏面板尺寸：25x25 格子，每个格子 25 像素
    private static final int BOARD_WIDTH = 25;
    private static final int BOARD_HEIGHT = 25;
    private static final int TILE_SIZE = 25;
    
    // 游戏窗口尺寸
    private static final int GAME_WIDTH = BOARD_WIDTH * TILE_SIZE;
    private static final int GAME_HEIGHT = BOARD_HEIGHT * TILE_SIZE;
    
    // 游戏速度（毫秒）
    private static final int DELAY = 100;
    
    // 方向常量
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    
    // 蛇的身体坐标
    private int[] snakeX = new int[BOARD_WIDTH * BOARD_HEIGHT];
    private int[] snakeY = new int[BOARD_WIDTH * BOARD_HEIGHT];
    private int snakeLength;
    
    // 食物坐标
    private int foodX, foodY;
    
    // 当前方向
    private int direction;
    
    // 游戏状态
    private boolean running = false;
    private Timer timer;
    private Random random;
    
    // 分数
    private int score;
    
    public SnakeGame() {
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        
        random = new Random();
        startGame();
    }
    
    private void startGame() {
        // 初始化蛇：长度为3，位于面板中央
        snakeLength = 3;
        snakeX[0] = BOARD_WIDTH / 2;
        snakeY[0] = BOARD_HEIGHT / 2;
        snakeX[1] = snakeX[0] - 1;
        snakeY[1] = snakeY[0];
        snakeX[2] = snakeX[0] - 2;
        snakeY[2] = snakeY[0];
        
        direction = RIGHT;
        score = 0;
        
        spawnFood();
        
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    private void spawnFood() {
        // 随机生成食物位置，确保不生成在蛇身上
        boolean validPosition = false;
        while (!validPosition) {
            foodX = random.nextInt(BOARD_WIDTH);
            foodY = random.nextInt(BOARD_HEIGHT);
            validPosition = true;
            for (int i = 0; i < snakeLength; i++) {
                if (snakeX[i] == foodX && snakeY[i] == foodY) {
                    validPosition = false;
                    break;
                }
            }
        }
    }
    
    private void move() {
        // 移动身体（从尾部开始向前复制）
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }
        
        // 移动头部
        switch (direction) {
            case UP:
                snakeY[0]--;
                break;
            case DOWN:
                snakeY[0]++;
                break;
            case LEFT:
                snakeX[0]--;
                break;
            case RIGHT:
                snakeX[0]++;
                break;
        }
    }
    
    private void checkFood() {
        // 检查是否吃到食物
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            snakeLength++;
            score += 10;
            spawnFood();
        }
    }
    
    private void checkCollisions() {
        // 检查墙壁碰撞
        if (snakeX[0] < 0 || snakeX[0] >= BOARD_WIDTH || 
            snakeY[0] < 0 || snakeY[0] >= BOARD_HEIGHT) {
            running = false;
        }
        
        // 检查自身碰撞（从第4节开始检查，因为头部不可能撞到前三节）
        for (int i = 4; i < snakeLength; i++) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                running = false;
                break;
            }
        }
        
        if (!running) {
            timer.stop();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (running) {
            // 绘制食物（红色圆形）
            g.setColor(Color.RED);
            g.fillOval(foodX * TILE_SIZE, foodY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            
            // 绘制蛇（绿色渐变：头部亮绿，身体暗绿）
            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(new Color(0, 180, 0));  // 头部深绿色
                } else {
                    g.setColor(new Color(0, 120, 0));  // 身体暗绿色
                }
                g.fillRect(snakeX[i] * TILE_SIZE, snakeY[i] * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
            }
            
            // 绘制网格线
            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i <= BOARD_WIDTH; i++) {
                g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, GAME_HEIGHT);
                g.drawLine(0, i * TILE_SIZE, GAME_WIDTH, i * TILE_SIZE);
            }
            
            // 显示分数
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        } else {
            // 游戏结束画面
            gameOver(g);
        }
    }
    
    private void gameOver(Graphics g) {
        // 半透明黑色背景
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        // 游戏结束文字
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER";
        int textX = (GAME_WIDTH - metrics.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, textX, GAME_HEIGHT / 2 - 30);
        
        // 最终分数
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreText = "Score: " + score;
        int scoreX = (GAME_WIDTH - metrics.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, scoreX, GAME_HEIGHT / 2 + 20);
        
        // 重启提示
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String restartText = "Press SPACE to restart";
        int restartX = (GAME_WIDTH - metrics.stringWidth(restartText)) / 2;
        g.drawString(restartText, restartX, GAME_HEIGHT / 2 + 70);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        // 防止180度掉头
        if (key == KeyEvent.VK_UP && direction != DOWN) {
            direction = UP;
        } else if (key == KeyEvent.VK_DOWN && direction != UP) {
            direction = DOWN;
        } else if (key == KeyEvent.VK_LEFT && direction != RIGHT) {
            direction = LEFT;
        } else if (key == KeyEvent.VK_RIGHT && direction != LEFT) {
            direction = RIGHT;
        } else if (key == KeyEvent.VK_SPACE && !running) {
            startGame();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("贪吃蛇游戏 - Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}