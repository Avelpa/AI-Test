import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JFrame;


// make sure you rename this class if you are doing a copy/paste
public class Main extends JComponent implements MouseListener, KeyListener
{

    // Height and Width of our game
    static final int WIDTH = 600;
    static final int HEIGHT = 600;
    
    // 1 = obstacle, 2 = player, 3 = enemy;
    int[][] map = {
        {0,0,0,0,0,0},
        {0,0,0,0,0,0},
        {0,0,0,0,0,0},
        {0,0,0,0,0,0},
        {0,0,0,0,0,0},
        {0,0,0,0,0,0},
    };
    int width = WIDTH/map[0].length;
    int height = HEIGHT/map.length;
    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000)/desiredFPS;
    
    int mx = 0;
    int my = 0;
    boolean lPressed = false;
    boolean rPressed = false;
    boolean middlePressed = false;
    boolean delete = false;
    
    int targetX = -100, targetY = -100;
    int enemyX = -100, enemyY = -100;
    
    boolean running = false;
    
    int timer = 0;
    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g)
    {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);
        
        if (running)
        {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }
        g.setColor(Color.BLACK);
        // GAME DRAWING GOES HERE 
        for (int y = 0; y <= HEIGHT; y += height)
        {
            g.drawLine(0, y, WIDTH, y);
        }
        for (int x = 0; x <= WIDTH; x += width)
        {
            g.drawLine(x, 0, x, HEIGHT);
        }
        for (int y = 0; y < map.length; y ++)
        {
            for (int x = 0; x < map[y].length; x ++)
            {
                if (map[y][x] == 1)
                {
                    g.setColor(Color.GRAY);
                    g.fillRect(x*width, y*height, width, height);
                }
                if (map[y][x] == 3)
                {
                    g.setColor(Color.GREEN);
                    g.fillRect(x*width, y*height, width, height);
                }
                if (map[y][x] == 2)
                {
                    g.setColor(Color.RED);
                    g.drawLine(x*width, y*height, x*width+width, y*height+height);
                    g.drawLine(x*width, y*height+height, x*width+width, y*height);
                }
            }
        }
        // GAME DRAWING ENDS HERE
    }
    
    
    // The main game loop
    // In here is where all the logic for my game will go
    public void run()
    {
        
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;
        
        // the main game loop section
        // game will end if you set done = false;
        boolean done = false; 
        while(!done)
        {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();
            
            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            if (!running)
            {
                timer = 0;
                if (!delete)
                {
                    if (lPressed)
                    {
                        int[] newCoords = getCoords(mx, my);

                        if (map[newCoords[1]][newCoords[0]] == 0)
                        {
                            map[newCoords[1]][newCoords[0]] = 1;
                        }
                        lPressed = false;
                    }
                    if (middlePressed)
                    {
                        if (enemyX < 0 && enemyY < 0)
                        {
                            int[] newCoords = getCoords(mx, my);
                            if (map[newCoords[1]][newCoords[0]] == 0)
                            {
                                map[newCoords[1]][newCoords[0]] = 3;  
                                enemyX = newCoords[0]*width;
                                enemyY = newCoords[1]*height;
                            }
                            middlePressed = false;
                        }
                    }
                    if (rPressed)
                    {
                        if (targetX < 0 && targetY < 0)
                        {
                            int[] newCoords = getCoords(mx, my);
                            if (map[newCoords[1]][newCoords[0]] == 0)
                            {
                                map[newCoords[1]][newCoords[0]] = 2;  
                                targetX = newCoords[0]*width;
                                targetY = newCoords[1]*height;
                            }
                            rPressed = false;
                        }
                    }
                }
                else if (lPressed)
                {
                    int[] newCoords = getCoords(mx, my);
                    if (map[newCoords[1]][newCoords[0]] == 3)
                    {
                        enemyX = -100;
                        enemyY = -100;
                    }
                    else if(map[newCoords[1]][newCoords[0]] == 2)
                    {
                        targetX = -100;
                        targetY = -100;
                    }
                    map[newCoords[1]][newCoords[0]] = 0;        
                    lPressed = false;
                }
            }
            // GAME LOGIC ENDS HERE 
            else if (running && targetX >= 0 && targetY >= 0 && enemyX >= 0 && enemyY >= 0)
            {
                timer ++;
                if (timer >= 60)
                {
                    moveEnemy(enemyX/width, enemyY/height);
                    timer = 0;
                }
            }
            // update the drawing (calls paintComponent)
            repaint();
            
            
            
            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            if(deltaTime > desiredTime)
            {
                //took too much time, don't wait
            }else
            {
                try
                {
                    Thread.sleep(desiredTime - deltaTime);
                }catch(Exception e){};
            }
        }
    }
    
    
    public int moveEnemy(int x, int y)
    {
        
        int tX = targetX/width;
        int tY = targetY/height;
        
        if (map[y][x] != 2)
        {
            int diffX = tX-x;
            int diffY = tY-y;
            map[y][x] = 0;
            
            if (diffY == diffX)
            {
                int ran = (int)(Math.random()*2);
                if (ran == 0)
                {
                    x += (int)Math.signum(diffX);
                }
                else if (ran == 1)
                {
                    y += (int)Math.signum(diffY);
                }
            }
            else
            {
                if (diffY == 0 || diffX < diffY && diffX != 0)
                {
                    x += (int)Math.signum(diffX);
                }
                else
                {
                    y += (int)Math.signum(diffY);
                }
            }
            
            if (map[y][x] == 2)
            {
                targetY = -100;
                targetX = -100;
            }
            enemyX = x*width;
            enemyY = y*width;
            map[y][x] = 3;
        }
        return 1;
    }
    
    public int[] getCoords(int mx, int my)
    {
        int gridX = mx/width;
        int gridY = my/height;
        
        
        return new int[] {gridX, gridY};
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates a windows to show my game
        JFrame frame = new JFrame("AI test");
       
        // creates an instance of my game
        Main game = new Main();
        // sets the size of my game
        game.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        // adds the game to the window
        frame.add(game);
         
        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);
        // starts my game loop
        game.addMouseListener(game);
        frame.addKeyListener(game);
        game.run();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!running)
        {
            if (e.getButton() == 1)
            {
                middlePressed = true;
            }
            else if (e.getButton() == 3)
            {
                rPressed = true;
            }
            else if (e.getButton() == 2)
            {
                lPressed = true;
            }
            mx = e.getX();
            my = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL && !running)
        {
            delete = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            running = running?false:true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            delete = false;
        }
    }
}