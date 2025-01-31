package org.cis1200.flappyBird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameDisplay extends JPanel {
    // the state of the game logic
    private Bird bird = new Bird(1000, 400); // controllable player
    private ArrayList<Obstacle> obstacles = new ArrayList<>(); // collection that manages obstacles
    private ArrayList<Coin> coins = new ArrayList<>(); // collection that manages coins
    private boolean playing = true; // whether the game is paused
    private int score = 0;
    private int highScore = 0;
    private int lastObstacle = -1;
    private int tickCounter = 0;
    private boolean isGameOver = false; // if game is over

    // Game constants
    private final JLabel scoreBoard;
    private static final int COURT_WIDTH = 1000;
    private static final int COURT_HEIGHT = 400;
    private static final int BIRD_VELOCITY_X = 10;
    private static final int BIRD_VELOCITY_Y = 5;

    // Update interval for timer, in milliseconds
    private static final int INTERVAL = 20;

    // GETTERS --------------------------------------------------------------------
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public ArrayList<Coin> getCoins() {
        return coins;
    }

    public int getScore() {
        return score;
    }

    public Bird getBird() {
        return bird;
    }

    // SETTERS --------------------------------------------------------------------

    private void setBird(Bird bird) {
        this.bird = bird;
    }

    private void setScore(int score) {
        this.score = score;
    }

    private void setObstacles(List<Obstacle> obstacles) {
        this.obstacles.addAll(obstacles);
    }

    private void setCoins(List<Coin> coins) {
        this.coins.addAll(coins);
    }

    private void setTickCounter(int tickCounter) {
        this.tickCounter = tickCounter;
    }

    private void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    // CONSTRUCTOR
    // -----------------------------------------------------------------------------

    public GameDisplay(JLabel scoreBoard, boolean isStartTick) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if (isStartTick) {
            Timer timer = new Timer(INTERVAL, e -> tick());
            timer.start();
        }
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    bird.setVx(-BIRD_VELOCITY_X);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    bird.setVx(BIRD_VELOCITY_X);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    bird.setVy(BIRD_VELOCITY_Y);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    bird.setVy(-BIRD_VELOCITY_Y);
                }
            }

            public void keyReleased(KeyEvent e) {
                bird.setVx(0);
                bird.setVy(0);
            }
        });
        this.scoreBoard = scoreBoard;
    }

    // TICK
    // -----------------------------------------------------------------------------

    void tick() {
        if (playing && !isGameOver) {
            bird.move();
            scoreBoard.setText(
                    "High Score: " + highScore + "                         Score: " + score
            );
            generateObjects();
            moveObjects();
            updateCoins();
            updateObstacles();
        }
        repaint();
        requestFocusInWindow();
        tickCounter++;
    }

    private void moveObjects() {
        if (tickCounter % 2 == 0) {
            for (Obstacle o : obstacles) {
                o.move();
            }
            for (Coin c : coins) {
                c.move();
            }
        }
    }

    private void generateObjects() {
        if (!obstacles.isEmpty()) {
            Obstacle lastOb = obstacles.get(obstacles.size() - 1);
            if (COURT_WIDTH - lastOb.getPx() - lastOb.getWidth() > 200) {
                Obstacle.generateRandomObstacle(obstacles);
                Coin.generateRandomCoin(coins, obstacles);
            }
        }
    }

    private void updateObstacles() {
        Iterator<Obstacle> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()) {
            Obstacle o = obstacleIterator.next();
            if (bird.intersects(o)) {
                playing = false;
                isGameOver = true;
                AudioPlayer.playEffect("files/explosion.wav");
                break;
            }
            if (o.getPx() + o.getWidth() < bird.getPx()) {
                if (lastObstacle < o.getId()) {
                    score += 100;
                    AudioPlayer.playEffect("files/score.wav");
                    if (score > highScore) {
                        highScore = score;
                    }
                    lastObstacle = o.getId();
                }
            }
            if (o.isOutOfBounds()) {
                obstacleIterator.remove();
            }
        }
    }

    private void updateCoins() {
        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin c = coinIterator.next();
            if (bird.intersects(c)) {
                score += 10;
                AudioPlayer.playEffect("files/score.wav");
                if (score > highScore) {
                    highScore = score;
                }
                coinIterator.remove();
                break;
            }
            if (c.isOutOfBounds()) {
                coinIterator.remove();
            }
        }
    }

    // RESET & PAUSE
    // -----------------------------------------------------------------------------

    public void reset() {
        bird = new Bird(COURT_WIDTH, COURT_HEIGHT);
        obstacles = new ArrayList<>();
        Obstacle.generateRandomObstacle(obstacles);
        coins = new ArrayList<>();
        Coin.generateRandomCoin(coins, obstacles);
        playing = true;
        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
        score = 0;
        tickCounter = 0;
        isGameOver = false;
    }

    public void pauseLabelController(JButton button) {
        if (!playing) {
            button.setText("Unpause");
        } else {
            button.setText("Pause");
        }
    }

    public void pauseToggler(JButton button) {
        playing = !playing;
        pauseLabelController(button);
    }

    // SAVE & LOAD
    // -----------------------------------------------------------------------------

    public void saveGame() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("gameState.txt"));
            writer.write("Bird: " + bird.toString() + "\n");
            writer.write("Score: " + score + "\n");
            writer.write("HighScore: " + highScore + "\n");
            writer.write("TickCounter: " + tickCounter + "\n");
            writer.write("Playing: " + playing + "\n");
            writer.write("GameOver: " + isGameOver + "\n");
            writer.write("Coins: " + coins.size() + "\n");
            for (Coin coin : coins) {
                writer.write(coin.toString() + "\n");
            }
            writer.write("Obstacles: " + obstacles.size() + "\n");
            for (Obstacle obstacle : obstacles) {
                writer.write(obstacle.toString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving game" + e.getMessage());
        }
    }

    private static Color parseColor(String colorString) {
        String[] colors = colorString.split(",");
        int red = Integer.parseInt(colors[0].split("=")[1]);
        int green = Integer.parseInt(colors[1].split("=")[1]);
        String b = colors[2].split("=")[1];
        b = b.substring(0, b.length() - 1);
        int blue = Integer.parseInt(b);
        return new Color(red, green, blue);
    }

    private static int parseNumber(BufferedReader reader, String label) throws IOException {
        String score = reader.readLine();
        String s = label + ": ";
        if (!score.startsWith(s)) {
            throw new IllegalArgumentException("Not a valid file");
        }
        score = score.substring(s.length());
        return Integer.parseInt(score);
    }

    private static boolean parseBoolean(BufferedReader reader, String label) throws IOException {
        String playing = reader.readLine();
        String b = label + ": ";
        if (!playing.startsWith(b)) {
            throw new IllegalArgumentException("Not a valid file");
        }
        return playing.substring(b.length()).equals("true");
    }

    public void loadGame() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("gameState.txt"));
            Bird bird = parseBird(reader);
            int score = parseNumber(reader, "Score");
            int highScore = parseNumber(reader, "HighScore");
            int tickCount = parseNumber(reader, "TickCounter");
            this.playing = parseBoolean(reader, "Playing");
            this.isGameOver = parseBoolean(reader, "GameOver");
            int x = parseNumber(reader, "Coins");
            ArrayList<Coin> coins = parseCoins(x, reader);
            int count = parseNumber(reader, "Obstacles");
            ArrayList<Obstacle> obstacleList = parseObstacles(count, reader);
            this.setCoins(coins);
            this.setBird(bird);
            this.setObstacles(obstacleList);
            this.setScore(score);
            this.setHighScore(highScore);
            this.setTickCounter(tickCount);
            scoreBoard.setText(
                    "High Score: " + highScore + "                         Score: " + score
            );
        } catch (IOException e) {
            throw new RuntimeException("Error loading game" + e.getMessage());
        }
    }

    private static ArrayList<Coin> parseCoins(int x, BufferedReader reader) throws IOException {
        ArrayList<Coin> coins = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            String coinPosition = reader.readLine();
            String[] coinPos = coinPosition.split(", ");
            int coinX = Integer.parseInt(coinPos[0]);
            int coinY = Integer.parseInt(coinPos[1]);
            Coin c = new Coin(coinX, coinY);
            coins.add(c);
        }
        return coins;
    }

    private static ArrayList<Obstacle> parseObstacles(int count, BufferedReader reader)
            throws IOException {
        ArrayList<Obstacle> obstacleList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String obstacle = reader.readLine();
            String[] fields = obstacle.split(", ");
            int obstacleX = Integer.parseInt(fields[0]);
            int obstacleY = Integer.parseInt(fields[1]);
            int obstacleWidth = Integer.parseInt(fields[2]);
            int obstacleHeight = Integer.parseInt(fields[3]);
            int obstacleVelY = Integer.parseInt(fields[4]);
            Color color = parseColor(fields[5]);
            int obstacleId = Integer.parseInt(fields[6]);
            Obstacle o = new Obstacle(
                    obstacleWidth, obstacleHeight,
                    obstacleY, obstacleVelY, color, obstacleId
            );
            o.setPx(obstacleX);
            obstacleList.add(o);
        }
        return obstacleList;
    }

    private static Bird parseBird(BufferedReader reader) throws IOException {
        String birdPosition = reader.readLine();
        if (!birdPosition.startsWith("Bird: ")) {
            throw new IllegalArgumentException("Not a valid file");
        }
        birdPosition = birdPosition.substring("Bird: ".length());
        String[] birdPos = birdPosition.split(", ");
        int birdX = Integer.parseInt(birdPos[0]);
        int birdY = Integer.parseInt(birdPos[1]);
        Bird bird = new Bird(COURT_WIDTH, COURT_HEIGHT);
        bird.setPx(birdX);
        bird.setPy(birdY);
        return bird;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        bird.draw(g);
        coins.forEach(coin -> coin.draw(g));
        obstacles.forEach(obstacle -> obstacle.draw(g));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}
