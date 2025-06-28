import java.awt.*;
import java.awt.event.*;
import java.nio.channels.Pipe;
import java.util.ArrayList; //will contain all the pipe
import java.util.Random; //will arrange the pipe
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 1000;
    int boardHeight = 900;

    //Images
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;


    //Bird
    int birdx =boardWidth/8;
    int birdy =boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x= birdx;
        int y= birdy;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }

    }


    //pipes
    int pipex = boardWidth;
    int pipey = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    class Pipe{
        int x= pipex;
        int y= pipey;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = -5;
    int gravity = 1;


    ArrayList<Pipe> pipes;
    Random random = new Random();


    Timer gameLoop;
    Timer placePipeTimer;

    boolean gameOver = false;

    double score =0;


    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
    
        setFocusable(true);
        addKeyListener(this);

        //Load images
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImage =new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImage);
        pipes =new ArrayList<Pipe>();

        //place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() { //will place pipes every 1.5 sec
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });

        placePipeTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this); //1000/6 = 16.6 frame per second
        gameLoop.start();

    }


    public void placePipes(){
        //(0-1)*pipeHeight/2 -> (0-256)
        //128
        //0-128 -(0-256) --> 1/4 pipeHeight -> 3/4 pipeHeight
        int randomPipeY = (int) (pipey - pipeHeight/4 - Math.random()*(pipeHeight/2)); 
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y =topPipe.y + pipeHeight +openingSpace;
        pipes.add(bottomPipe);       

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {

        //background
        g.drawImage(backgroundImage, 0, 0,boardWidth, boardHeight, null);

        //bird
        g.drawImage(birdImage, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.setColor(Color.red);
            g.drawString("GAME OVER " + String.valueOf((int) score),10,35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {

        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; // 0.5 for one pipe

            }
            if (collision(bird, pipe)){
                gameOver =true;
            }

        }

        //gameover
        if(bird.y > boardHeight){
            gameOver =true;
        }
    
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }



    @Override
    public void keyTyped(KeyEvent e) { // not going to be used

    }



    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE);
        velocityY = -9;
        if(e.getKeyCode() == KeyEvent.VK_NUMPAD0);
        velocityY = -9; 
        if(gameOver){
            bird.y = birdy;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
        }
    }



    @Override
    public void keyReleased(KeyEvent e) { // not going to be used

    }
    
}
