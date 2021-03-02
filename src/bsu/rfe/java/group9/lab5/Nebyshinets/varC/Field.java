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
    private BouncingBall currentBall = null;

    private Timer repaintTimer = new Timer(10, new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            repaint();
        }
    });

    public Field() {
        setBackground(Color.WHITE);
        this.addMouseListener(new Field.MouseHandler());
        repaintTimer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
        for (BouncingBall ball: balls) {
            ball.paint(canvas);
        }
    }
    // Метод добавления нового мяча в список
    public void addBall() {
//Заключается в добавлении в список нового экземпляра BouncingBall
// Всю инициализацию положения, скорости, размера, цвета
// BouncingBall выполняет сам в конструкторе
        balls.add(new BouncingBall(this));
    }
    // Метод синхронизированный, т.е. только один поток может
// одновременно быть внутри
    public synchronized void pause() {
// Включить режим паузы
        paused = true;
    }
    // Метод синхронизированный, т.е. только один поток может
// одновременно быть внутри
    public synchronized void resume() {
// Выключить режим паузы
        paused = false;
// Будим все ожидающие продолжения потоки
        notifyAll();
    }
    // Синхронизированный метод проверки, может ли мяч двигаться
// (не включен ли режим паузы?)
    public synchronized void canMove(BouncingBall ball) throws
            InterruptedException {
        if (paused) {
// Если режим паузы включен, то поток, зашедший
// внутрь данного метода, засыпает
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
                //System.out.println(startX + " " + startY);
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
                    System.out.println(deltaX + " " + deltaY);
                    double angle = Math.atan( deltaY/deltaX );
                    if(deltaX < 0){
                        if(deltaY > 0){
                            angle += Math.PI;
                        } else {
                            angle -= Math.PI;
                        }
                    }
                    currentBall.setSpeed(angle);

                    currentBall = null;
                }
                resume();
            }
        }
    }
}
