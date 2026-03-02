package flappygame;

import javax.swing.*;      //gui elements ke liye    jframe,jpanel,joptinpane wagera..
import java.awt.*;         //drawing aur colors ke liye    graphics,color,font wagera wagera
import java.awt.event.*;   //keyboard aur timer events handle karne ke liye
import java.util.ArrayList; //pipes ko list me store karne ke liye
import java.util.Random;   //pipes ki random height banane ke liye

public class FlappyBird extends JPanel implements ActionListener,KeyListener{

    //game board ka size 
    int boardwidth=360;    //screen ki width
    int boardheight=640;   //screen ki height
    //  game me use hone wali images,   graphics
    Image backgroundimg,birdimg,toppipeimg,bottompipeimg;

    //bird ki starting position aur size 
    int birdx=boardwidth/8;     //bird ka x position
    int birdy=boardheight/2;    //bird ka y position
    int birdwidth=34;             //bird ki width
    int birdheight=24;            //bird ki height

//bird class jo bird ki position, size, aur image store karti hai 
class Bird{
        int x=birdx;       //x position
        int y=birdy;       //y position
        int width=birdwidth;   //width
        int height=birdheight; //height
        Image img;               //bird ki image
        
        Bird(Image img){
            this.img=img;     //constructor me image assign kar rahe hain
        }
}
    //pipe ki width aur height, aur opening gap 
    int pipewidth=64;         //pipe ki width
    int pipeheight=512;       //pipe ki height
    final int openingspace=boardheight/4; //gap jo upar aur neeche pipe ke darmiyan hota hai

//pipe class har pipe ki properties define karti hai 
class Pipe{
        int x;                //pipe ka x position
        int y;                //pipe ka y position
        int width=pipewidth;
        int height=pipeheight;
        Image img;            //pipe ki image
        boolean passed=false;  //kya bird ne is pipe ko cross kiya?

        Pipe(Image img,int x,int y){
            this.img=img;
            this.x=x;
            this.y=y;
        }
}
    Bird bird;                 //bird object

    int velocityx;             //pipes ki speed, left move krte hain
    int basespeed=-4;        //default speed, difficulty k hisab s set hogi
    int maxspeed=-10;        //max speed, hrdest lvl
    int velocityy=0;         //bird ki vertical speed
    int gravity=1;           //neeche girne wali force, gravity

    ArrayList<Pipe> pipes;     //pipes ka dynamic list
    Random random=new Random(); //random height banane ke liye

    Timer gameloop;            //main game loop , 60 times per second chalta h
    Timer placepipetimer;      //har 1.5 second me naye pipes add karta hai

    boolean gameover=false;      //game over flag
    boolean gamestarted=false;   //game start hua ya nahi
    double score=0;              //player ka current score
    double highscore=0;          //sabse zyada banaya gaya score

//constructor,game start hone par sab kuch initialize karta hai 
FlappyBird(){
        setPreferredSize(new Dimension(boardwidth,boardheight)); //panel ka size set
        setFocusable(true);        //keyboard events ka focus milta hai
        addKeyListener(this);      //key events sunne ke liye

        //images load karna, resources folder se 
        backgroundimg=new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdimg=new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        toppipeimg=new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottompipeimg=new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird=new Bird(birdimg);  //bird ka object banaya
        pipes=new ArrayList<>(); //pipes ka arraylist banaya

        //game loop set kiya , 60 fps k lyie 
        gameloop=new Timer(1000/60,this);

        //har 1500ms me naye pipes add karne wala timer 
        placepipetimer=new Timer(1500,e->placepipes());

        //game start hone se pehle difficulty pochi jayegi
        choosedifficulty();
}

//difficulty choose karne ka dialog box function
void choosedifficulty(){
        String[] options={"Easy","Medium","Hard"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select Difficulty:",        //dialog ka title
                "Flappy Bird",              //window ka title
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        //speed set karte hain difficulty ke according 
        switch(choice){
            case 0: //easy
                basespeed=-3;
                maxspeed=-6;
                break;
            case 1: //medium
                basespeed=-4;
                maxspeed=-8;
                break;
            case 2: //hard
                basespeed=-5;
                maxspeed=-10;
                break;
            default:
                basespeed=-4;
                maxspeed=-8;
                break;
        }
        velocityx=basespeed;  //pipes ki speed set kar rahe hain
        gameloop.start();       //game loop start
        placepipetimer.start(); //pipe placement timer start
        gamestarted=true;
}

//random height ke sath pipes ko screen pe laana 
void placepipes(){
        int minpipetopy=-pipeheight+100;  //upar ki pipe ki lowest position
        int maxpipetopy=-100;               //upar ki pipe ki highest position
        int randompipey=minpipetopy+random.nextInt(maxpipetopy-minpipetopy+1);

        //dono pipes banate hain , upr aur nchay
        Pipe toppipe=new Pipe(toppipeimg,boardwidth,randompipey);
        Pipe bottompipe=new Pipe(bottompipeimg,boardwidth,randompipey+pipeheight+openingspace);

        pipes.add(toppipe);
        pipes.add(bottompipe);
}

//screen draw karne ka method, aueomatic cal hota h
public void paintComponent(Graphics g){
        super.paintComponent(g); //background clear karta hai
        draw(g);                 //sab cheezen draw karne ke liye
}

//game ka main drawing function 
public void draw(Graphics g){
        g.drawImage(backgroundimg,0,0,boardwidth,boardheight,null); //background
        g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null); //bird
    
        for(Pipe pipe:pipes){
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null); //pipes
        }

        //score aur high score text 
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.BOLD,32));
        g.drawString("Score: "+(int)score,10,35);
        g.drawString("High: "+(int)highscore,200,35);

        //agar game over ho jaye to message dikhana 
        if(gameover){
            g.setFont(new Font("Arial",Font.BOLD,24));
            g.drawString("Game Over",100,boardheight/2);
            g.setFont(new Font("Arial",Font.PLAIN,16));
            g.drawString("Press SPACE to Restart",80,boardheight/2+40);
        }
}

//har frame pe bird aur pipes ki position update karna 
public void move(){
        velocityy+=gravity;  //gravity lagti hai bird pe
        bird.y+=velocityy;   //bird neeche girta hai

        for(Pipe pipe:pipes){
            pipe.x+=velocityx; //pipes left move karti hain
            
            //agar bird ne pipe cross kar li to score badhao 
            if(!pipe.passed && bird.x>pipe.x+pipe.width){               
                score+=0.5;         //har 2 pipes cross karne pe 1 point milta hai (0.5 har pipe ka)
                pipe.passed=true;  //pipe ko mark karte hain ke ye pass ho chuki hai

                //gar 10 score pe game ki speed tez ho jati hai 
                if((int)score % 10==0 && velocityx>maxspeed){
                    velocityx-=1;  //speed aur tez karte hain , difficukty barhti h
                }
            }

            //bird aur pipe ke darmiyan collision check karte hain 
            if(collision(bird,pipe)){
                gameover=true;   //agar takra gaya to game over ho jata hai
            }
        }

        //agar bird neeche gir jaye ya screen ke bahar chali jaye to game over 
        if(bird.y>boardheight || bird.y<0){
            gameover=true;
        }

        //jo pipes screen ke bahar chale jayein, unhe hata do, memory save hoti h
        pipes.removeIf(pipe->pipe.x+pipe.width<0);
}

//collision detection function, bird aur pipe takren ya nh
boolean collision(Bird a,Pipe b){
        return a.x<b.x+b.width &&       //bird ka right side pipe ke left side ke andar ho
               a.x+a.width>b.x &&       //bird ka left side pipe ke right side ke andar ho
               a.y<b.y+b.height &&      //bird ka bottom pipe ke top se takra raha ho
               a.y+a.height>b.y;        //bird ka top pipe ke bottom se takra raha ho
}

//ye method har frame me call hota hai , 60 time per second
@Override
public void actionPerformed(ActionEvent e){
        if(gamestarted && !gameover){
            move();      //game ki logic update karo
            repaint();   //dubara draw karo
        }
        if(gameover){
            if(score>highscore){
                highscore=score; //naya high score set krhy
            }
            gameloop.stop();        //game loop band krhy
            placepipetimer.stop();  //pipe ka timer bhi band
    }
}

//jab space key press ho to bird jump kare ya game reset ho
@Override
public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            velocityy=-9; //bird jump karega , upr ki traf move

            if(gameover){
                //game reset karte hain 
                bird.x=birdx;
                bird.y=birdy;
                velocityy=0;
                velocityx=basespeed;
                pipes.clear();      //saari purani pipes hata do
                score=0;
                gameover=false;
                gameloop.start();       //game loop dobara start
                placepipetimer.start(); //pipe wala timer bhi start
        }
    }
}
//ye 2 methods abhi use nahi ho rahi lekin required hain by interface
@Override public void keyTyped(KeyEvent e){}
@Override public void keyReleased(KeyEvent e){}

public static void main(String[] args){
        JFrame f=new JFrame("Flappy Bird");     // ek nayi window banao
        FlappyBird fb=new FlappyBird();     // game ka object banao
        f.add(fb);                        // game panel ko window me add karo
        f.setSize(fb.boardwidth,fb.boardheight); // window size set karo
        f.setLocationRelativeTo(null);            // window ko screen ke center me lao
        f.setResizable(false);                    // window resize na ho
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close pe program band ho
        f.pack();                                 // auto layout fix karo
        fb.requestFocus();                    // game input ka focus le
        f.setVisible(true);                       // window show karo
    }
}
