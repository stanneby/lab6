package bsu.rfe.java.group9.lab5.Nebyshinets.varC;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
@SuppressWarnings("serial")
public class Field extends JPanel {
    private boolean paused;
    private ArrayList<BouncingBall> balls = new ArrayList<BouncingBall>(10);
    private double startX;
    private double startY;
    private long startTime;
    private BouncingBall currentBall = null;
    private MainFrame outerFrame;

    private Timer repaintTimer = new Timer(10, new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            repaint();
        }
    });

    public Field(MainFrame outer) {
        setBackground(Color.WHITE);
        this.addMouseListener(new Field.MouseHandler());
        outerFrame = outer;
        repaintTimer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
        for (BouncingBall ball: balls) {
            ball.paint(canvas);
        }
    }

    public void addBall() {
        balls.add(new BouncingBall(this));
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    public synchronized void canMove(BouncingBall ball) throws
            InterruptedException {
        if (paused) {
            wait();
        }
    }

    public class MouseHandler extends MouseAdapter {
        public MouseHandler() {
        }

        public void mouseClicked(MouseEvent ev) {
        }

        public void mousePressed(MouseEvent ev) {
            if (ev.getButton() == 1) {
                pause();
                startX = ev.getX();
                startY = ev.getY();
                startTime = System.currentTimeMillis();
                for( int i = 0; i < balls.size(); i++ ){
                    BouncingBall ball = balls.get(i);
                    if( Math.pow(startX - ball.getX(), 2) + Math.pow(startY - ball.getY(), 2) < Math.pow(ball.getRadius(), 2)){
                        currentBall = ball;
                        break;
                    }
                }
            }
        }

        public void mouseReleased(MouseEvent ev) {
            if (ev.getButton() == 1) {
                if(currentBall != null) {
                    double deltaX = ev.getX() - startX;
                    double deltaY = ev.getY() - startY;
                    long deltaTime = System.currentTimeMillis() - startTime;

                    double angle = Math.atan( deltaY/deltaX );
                    if(deltaX < 0){
                        if(deltaY > 0){
                            angle += Math.PI;
                        } else {
                            angle -= Math.PI;
                        }
                    }

                    int newSpeed = 3*(int)Math.sqrt( deltaX*deltaX + deltaY*deltaY )/(int)deltaTime;

                    currentBall.setSpeed(newSpeed, angle);

                    currentBall = null;
                }
                if(outerFrame.getPaused()) {
                    resume();
                }
            }
        }
    }
}
