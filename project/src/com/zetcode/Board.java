package com.zetcode;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;


    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;
    private int applesEaten;
    private String highscore = "";

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        
        initBoard();
    }
    
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void initGame() {

        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        
        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {

        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            if (highscore.equals("")) {
                highscore = this.GetHighScore();
            }

            String msg1 = "High Score "+highscore;
            Font small1 = new Font("Helvetica", Font.BOLD, 25);
            FontMetrics metr1 = getFontMetrics(small1);

            g.setColor(Color.white);
            g.setFont(small1);
            g.drawString(msg1, (B_WIDTH - metr1.stringWidth(msg1)) / 2, B_HEIGHT /1);

            String msg = "Score: "+applesEaten;
            Font small = new Font("Helvetica", Font.BOLD, 25);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, g.getFont().getSize());

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }        
    }

    private void gameOver(Graphics g) {
        
        String msg1 = "Game Over";
        Font small1 = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr1 = getFontMetrics(small1);

        g.setColor(Color.white);
        g.setFont(small1);
        g.drawString(msg1, (B_WIDTH - metr1.stringWidth(msg1)) / 2, B_HEIGHT / 2);

        String msg2 = "Score: "+applesEaten;
        Font small2 = new Font("Helvetica", Font.BOLD, 25);
        FontMetrics metr2 = getFontMetrics(small2);

        g.setColor(Color.white);
        g.setFont(small2);
        g.drawString(msg2, (B_WIDTH - metr2.stringWidth(msg2)) / 2, g.getFont().getSize());

        String msg3 = "High Score "+highscore;
        Font small3 = new Font("Helvetica", Font.BOLD, 25);
        FontMetrics metr3 = getFontMetrics(small3);

        g.setColor(Color.white);
        g.setFont(small3);
        g.drawString(msg3, (B_WIDTH - metr3.stringWidth(msg3)) / 2, B_HEIGHT /1);
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++;
            applesEaten++;
            locateApple();
            CheckScore(); // Call CheckScore to update high score immediately
        }
    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
                CheckScore();
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
            CheckScore();
        }

        if (y[0] < 0) {
            inGame = false;
            CheckScore();
         
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
            CheckScore();
        }

        if (x[0] < 0) {
            inGame = false;
            CheckScore();
        }
        
        if (!inGame) {
            timer.stop();
            CheckScore();
        }
    }

    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    public String GetHighScore() {
        FileReader readFile = null;
        BufferedReader reader = null;
        try {
            readFile = new FileReader("highscore.dat");
            reader = new BufferedReader(readFile);
            return reader.readLine();
        }
        catch (Exception e) {
            return ":0";
        }
        finally {
            try {
                if (reader != null)
                reader.close();
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }   
        }
    }

    public void CheckScore() {
        System.out.println(highscore);

        if (applesEaten > Integer.parseInt((highscore.split(":")[1]))) {
            highscore = ":" + applesEaten;

            File scoreFile = new File("highscore.dat");
            if (!scoreFile.exists()) {
                try {
                    scoreFile.createNewFile();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            FileWriter writeFile = null;
            BufferedWriter writer = null;
            try {
                writeFile = new FileWriter(scoreFile);
                writer = new BufferedWriter(writeFile);

                writer.write(this.highscore);
            }
            catch (Exception e) {

            }
            finally {
                try {
                    if (writer != null)
                        writer.close();
                    }
                    catch (Exception e) {

                    }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_A) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_D) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_W) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_S) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
