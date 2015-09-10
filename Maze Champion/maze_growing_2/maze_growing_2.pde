import ddf.minim.*;                //minim is an audio library

int cell_width; // width of each cell
int cell_height; // height of each cell
int cell_player_gapx; // gap that should be maintained between cell walls and player
int cell_player_gapy; // gap that should be maintained between cell walls and player
int player_width; // width of player
int player_height; // height of player
int[][][] maze; // maze
int blk_limit; // limit of number of blocks that a player can place for opponent 
int stuck_limit; // number of seconds that the player can get stuck
int level; // the level of the maze
int cell_startx; // x coordinate of first cell
int cell_starty; // y coordinate of first cell
int visitcount_1 = 0; // number of times player1 visited the key location
int visitcount_2 = 0; // number of times player1 visited the key location
boolean game_state, instructions; // game state variables
PFont font; // font variable
Players players; // the players
int enemy_numbers; // number of enemies constant for now
Enemy[] enemies; // array of enemies
Target target; // target and key class

PImage player1_key, player2_key, player1_door, player2_door; // display images for players' key and door

Minim minim;               // minim object
AudioPlayer song;          // Audio player for the song played in the game
AudioPlayer dead;          // Audio player for the sound played when player dies 
AudioPlayer door;          // Audio player for the sound played when door is unlocked
AudioPlayer key_sound;     // Audio player for the sound played when key is available 
AudioPlayer block;         // Audio player for the sound played when player places a block
AudioPlayer blocklimit;    // Audio player for the sound played when block limit is exceeded


// initializing the maze for a new maze
void init_Maze()
{
  levelGenerator gen = new levelGenerator(); // make new levelGenerator object
  gen.levelGenerator(); // initialize the object
  gen.mazeGenerator(); // generate the maze
  maze = gen.maze; // store the maze
  level = gen.level; // store the level
  game_state = false; // make game state as no player won

  players.players_init(level, blk_limit); // initialize the players

  enemy_numbers = 4; // the number of enemies is set to 4
  enemies = new Enemy[enemy_numbers]; // make the new enemies

  for (int i = 0; i < enemy_numbers; i++) // initialize all the enemies
  {
    enemies[i] = new Enemy(); // make an enemy object
    enemies[i].Enemy(level); // initialize the object
    enemies[i].init_enemy(level, maze, 0, i % 4); // set the initial conditions
  }

  target.target_init(level); // initialize the targets and keys for the players
}


boolean sketchFullScreen() {    //plays the game in full screen mode
  return true;
}

// setting up the sketch
void setup()
{
  
  minim = new Minim(this);                         
  song = minim.loadFile("game_music.mp3");         // variable to load game music     
  dead = minim.loadFile("death.mp3");              // variable to load death sound
  key_sound = minim.loadFile("key.mp3");           // variable to load key available sound
  door = minim.loadFile("door.mp3");               // variable to load door unlocked
  block = minim.loadFile("block.wav");             // variable to load place block sound
  blocklimit = minim.loadFile("blocklimit.mp3");   // variable to load block limit exceeded sound 
  
  song.play();
  song.loop();
  
/////////////////////////////////////////////////////////////
  
  size(displayHeight, displayHeight); // make canvas size 600 x 600
  cell_startx = 50; // make cells start after 50 pixels in both directions
  cell_starty = 50;

  // load the images for the players' keys and doors
  player1_key = loadImage("red_key.jpg");
  player2_key = loadImage("green_key.png");
  player1_door = loadImage("red_door.jpg");
  player2_door = loadImage("green_door.jpg");

  font = createFont("Arial", 16, true); // initialize font

  instructions = true; // display instructions before the first game

  stuck_limit = 5; // make the number of seconds player can get stuck 5
  blk_limit = 5; // initialize the max number of blocks a player can put

  players = new Players(); // make a new players object
  players.Players(blk_limit); // initialize the players object
  target = new Target(); // make a new target object
  target.Target(); // initialize the target object

    init_Maze(); // call maze initialization function

  cell_width = (displayHeight - 100) / level; // initialize cell width and height
  cell_height = (displayHeight - 100) / level;

  player_width = cell_width - 2; // specify the width and height of player
  player_height = cell_height - 2;

  // resize the images for keys and doors to fit the maze
  player1_key.resize(player_width, player_height);
  player2_key.resize(player_width, player_height);
  player1_door.resize(player_width, player_height);
  player2_door.resize(player_width, player_height);

  //  for (int i = 0; i < level; i++) //display maze in command line if you want
  //  {
  //    for (int j = 0; j < level; j++)
  //    {
  //      print(maze[i][j][0] + "" + maze[i][j][1] + "" + maze[i][j][2] + "" + maze[i][j][3] + "\t");
  //    }
  //    print("\n");
  //  }

}

// when a key is pressed
void keyPressed()
{
  if (instructions) // only SpaceBar can be pressed when in instructions page
  {
    if (key == ' ')
    {
      instructions = false; // instructions won't be dislayed again anymore
    }
  } else // if not in instructions page
  {
    if (!game_state) // if no one has won so far
    {
      switch(keyCode) // player 1 controls
      {
      case UP:  
        game_state = players.player1Up(maze, target, game_state, blk_limit);
        break;

      case RIGHT:
        game_state = players.player1Right(maze, target, game_state, blk_limit);
        break;

      case DOWN:
        game_state = players.player1Down(maze, target, game_state, blk_limit);
        break;

      case LEFT:
        game_state = players.player1Left(maze, target, game_state, blk_limit);
        break;
      }

      switch(key) // player 2 controls
      {
      case 'w':
      case 'W':  
        game_state = players.player2Up(maze, target, game_state, blk_limit);
        break;

      case 'd':
      case 'D':
        game_state = players.player2Right(maze, target, game_state, blk_limit);
        break;

      case 's':
      case 'S':
        game_state = players.player2Down(maze, target, game_state, blk_limit);
        break;

      case 'a':
      case 'A':
        game_state = players.player2Left(maze, target, game_state, blk_limit);
        break;

      case '?': // place block by player 2
      case '/':
        if (players.p1_blocks < blk_limit)
        {
          players.player1_blocks[players.p1_blocks][0] = players.player1_x;
          players.player1_blocks[players.p1_blocks][1] = players.player1_y;
          players.p1_blocks++;
          block.cue(0);     // When cue is used, it always measures from the beginning of the recording. 
                            // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                            // no matter where it currently is.
          block.play();     // plays the sound of block being placed on maze 
        }
        else                   // if block limit is exceeded
        {
          blocklimit.cue(0);   // When cue is used, it always measures from the beginning of the recording. 
                               // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                               // no matter where it currently is.
          blocklimit.play();   // plays the sound of block limit exceeded
        }
        break;

      case 'q': // place block by player 1
      case 'Q':
        if (players.p2_blocks < blk_limit)
        {
          players.player2_blocks[players.p2_blocks][0] = players.player2_x;
          players.player2_blocks[players.p2_blocks][1] = players.player2_y;
          players.p2_blocks++;
          block.cue(0);      // When cue is used, it always measures from the beginning of the recording. 
                             // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                             // no matter where it currently is.
          block.play();      // plays the sound of block being placed on maze 
        }
        else                 // if block limit is exceeded
        {
          blocklimit.cue(0);   // When cue is used, it always measures from the beginning of the recording. 
                               // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                               // no matter where it currently is.
          blocklimit.play();   // plays the sound of block limit exceeded
        }
        break;
      }

      if (players.player1_x == target.key1_x && players.player1_y == target.key1_y && target.key1_placed)
      {
         target.key_take_1(); // if player 1 has reached the tile containing his key then toggle this function
         if(visitcount_1<1)
         {
         door.cue(0);      // When cue is used, it always measures from the beginning of the recording. 
                           // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                           // no matter where it currently is.
         door.play();      // plays the door unlocked sound
         visitcount_1++;
         }
      }
 
      if (players.player2_x == target.key2_x && players.player2_y == target.key2_y && target.key2_placed)
      {
         target.key_take_2(); // if player 1 has reached the tile containing his key then toggle this function
         if(visitcount_2<1)
         {
         door.cue(0);      // When cue is used, it always measures from the beginning of the recording. 
                           // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                           // no matter where it currently is.
         door.play();      // plays the door unlocked sound
         visitcount_2++;
         }
      }
    }
    if (game_state) // if a player has won the game, then only r can be pressed to generate new maze
    {
      if (key == 'r' || key == 'R')
      {
        init_Maze();
      }
    }
  }
}

// draw function that is called every frame
void draw()
{

  background(0); // set background black

  if (instructions) // while displaying the instructions
  {
    // display all the instructions as specified below
    textFont(font, 60);
    fill(255, 255, 255);
    textAlign(CENTER);
    text("Maze Champion!", (displayHeight / 2), 100);
    textAlign(LEFT);
    textFont(font, 30);
    text("INSTRUCTIONS : ",100, 230);
    textFont(font, 20);
    text("** Player 1 is displayed in red.",100, 260);
    text("** Player 2 is displayed in green.",100, 290);
    text("** Player 1 uses arrow keys to move a unit.",100, 320);
    text("** Player 2 uses 'w', 'a', 's', 'd' keys to move a unit.",100, 350);
    text("** Player 1 uses '/' key to place block.",100, 380);
    text("** Player 2 uses 'q' key to place block.",100, 410);
    text("** Maximum allowed number of blocks at a time is 5.",100, 440);
    text("** A player stuck in opponent's block waits 5 seconds to move.",100, 470);
    text("** Enemies are displayed in black and can also be stuck in a block.",100, 500);
    text("** Enemies DO NOT like to be caught in blocks. Be wary and vigilant!",100, 530);
    text("** Everything pertaining to player 1 are red and those of player 2 are green.",100, 560);
    text("** The target for each player is shown only on taking the respective keys.",100, 590);
    text("** The keys are displayed after 20 moves for each player.",100, 620);
    text("** May the best player be named Maze Champion!",100, 650);
    textAlign(CENTER);
    textFont(font, 25);
    text("Hit SpaceBar to continue to game.", (displayHeight / 2), 760);
  } else { // if not displaying instructions
    if (!game_state) // if no one has won the game yet
    {
      fill(255); // display player stats in white
      textFont(font, 16);
      textAlign(LEFT);
      if (!target.key1_placed) { // if player 1 key has not yet been displayed
        text("PLAYER 1 : " + players.p1_score, 10, 20);
      } else if (!players.player1_key) { // if player 1 key has been displayed but not taken
        text("PLAYER 1 : " + players.p1_score + "   KEY PLACED", 10, 20);
      } else { // if player 1 key has been taken
        text("PLAYER 1 : " + players.p1_score + "   KEY TAKEN", 10, 20);
      }
      if (!target.target1_placed) { // if player 1 target has not yet been displayed
        text("BLOCKS LEFT : " + (blk_limit - players.p1_blocks), 10, 40);
      } else { // if player 1 target has been displayed
        text("BLOCKS LEFT : " + (blk_limit - players.p1_blocks) + "   TARGET PLACED", 10, 40);
      }
      textAlign(RIGHT);
      if (!target.key2_placed) { // if player 2 key has not yet been displayed
        text("PLAYER 2 : " + players.p2_score, displayHeight - 10, 20);
      } else if (!players.player2_key) { // if player 2 key has been displayed but not taken
        text("KEY PLACED   PLAYER 2 : " + players.p2_score, displayHeight - 10, 20);
      } else { // if player 2 key has been taken
        text("KEY TAKEN   PLAYER 2 : " + players.p2_score, displayHeight - 10, 20);
      }
      if (!target.target2_placed) { // if player 1 target has not yet been displayed
        text("BLOCKS LEFT : " + (blk_limit - players.p2_blocks), displayHeight - 10, 40);
      } else { // if player 1 target has been displayed
        text("TARGET PLACED   BLOCKS LEFT : " + (blk_limit - players.p2_blocks), displayHeight - 10, 40);
      }

      // display the background for the maze
      stroke(0);
      fill(255);
      rect(50, 50, displayHeight - 100, displayHeight - 100);
      strokeWeight(2);

      int x, y; // tempporary variables for displaying the maze

      for (int i = 0; i < level; i++) // take the maze and put appropriate asset in its place
      {
        for (int j = 0; j < level; j++)
        {

          if (maze[i][j][0] == 0 && maze[i][j][1] == 0  && maze[i][j][2] == 0  && maze[i][j][3] == 1)                //0001
          {            
            //image(img1, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            line(x, y, x + cell_width, y);
            line(x + cell_width, y, x + cell_width, y + cell_height);
            line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 0 && maze[i][j][1] == 0  && maze[i][j][2] == 1 && maze[i][j][3] == 0)                 //0010
          { 
            //image(img2, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            line(x, y, x + cell_width, y);
            line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 0 && maze[i][j][1] == 0  && maze[i][j][2] == 1  && maze[i][j][3] == 1)                //0011
          { 
            //image(img5, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            line(x, y, x + cell_width, y);
            line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 0 && maze[i][j][1] == 1  && maze[i][j][2] == 0  && maze[i][j][3] == 0)                // 0100
          { 
            //image(img3, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            line(x, y + cell_height, x + cell_width, y + cell_height);
            line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 0 && maze[i][j][1] == 1  && maze[i][j][2] == 0  && maze[i][j][3] == 1)                // 0101
          { 
            //image(img6, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 0 && maze[i][j][1] == 1  && maze[i][j][2] == 1  && maze[i][j][3] == 0)               // 0110
          { 
            //image(img8, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 0 && maze[i][j][1] == 1  && maze[i][j][2] == 1  && maze[i][j][3] == 1)              // 0111
          { 
            //image(img11, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 0  && maze[i][j][2] == 0  && maze[i][j][3] == 0)             // 1000
          { 
            //image(img4, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            line(x + cell_width, y, x + cell_width, y + cell_height);
            line(x, y + cell_height, x + cell_width, y + cell_height);
            line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 0  && maze[i][j][2] == 0  && maze[i][j][3] == 1)             // 1001
          { 
            //image(img7, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            line(x + cell_width, y, x + cell_width, y + cell_height);
            line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 0  && maze[i][j][2] == 1  && maze[i][j][3] == 0)             // 1010
          { 
            //image(img9, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 0  && maze[i][j][2] == 1  && maze[i][j][3] == 1)             // 1011
          { 
            //image(img13, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 1  && maze[i][j][2] == 0  && maze[i][j][3] == 0)             // 1100
          { 
            //image(img10, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            line(x, y + cell_height, x + cell_width, y + cell_height);
            line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 1  && maze[i][j][2] == 0  && maze[i][j][3] == 1)             // 1101
          { 
            //image(img14, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 1  && maze[i][j][2] == 1  && maze[i][j][3] == 0)             // 1110
          { 
            //image(img12, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            line(x, y, x, y + cell_height);
          }

          if (maze[i][j][0] == 1 && maze[i][j][1] == 1  && maze[i][j][2] == 1  && maze[i][j][3] == 1)             // 1111
          { 
            //image(img15, cell_startx + j * cell_width, cell_starty + i * cell_height, cell_width, cell_height);
            x = cell_startx + j * cell_width;
            y = cell_starty + i * cell_height;
            //line(x, y, x + cell_width, y);
            //line(x + cell_width, y, x + cell_width, y + cell_height);
            //line(x, y + cell_height, x + cell_width, y + cell_height);
            //line(x, y, x, y + cell_height);
          }
        }
      }

      players.player1_block_handle(stuck_limit, frameRate); // everytime draw is called, increment the counter for number of frames player 1 has been blocked if player 1 is blocked
      players.player2_block_handle(stuck_limit, frameRate); // everytime draw is called, increment the counter for number of frames player 2 has been blocked if player 2 is blocked

      strokeWeight(0);
      for (int i = 0; i < enemy_numbers; i++) { // check if any of the enemies are killing player 1
        if (players.player1_x == enemies[i].enemy_x && players.player1_y == enemies[i].enemy_y) {
          dead.cue(0);       // When cue is used, it always measures from the beginning of the recording. 
                             // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                             // no matter where it currently is.
          dead.play();       // plays the death sound when player dies
          visitcount_1 = 0;  // set visit to key location back to zero
          players.player1_reset(level); // if this enemy is killing player 1, reset player 1
          target.key_lost_1(); // drop the key that player 1 has picked up
          for (int j = 0; j < enemy_numbers; j++) // for all enemies
          {
            if (enemies[j].mode == 3) // if this enemy was patrolling target for player 1 then
            {
              enemies[j].mode = 0; // change the enemy to patrolling his own territory
              enemies[j].corner = 4;
              enemies[j].guarding = false; // the enemy is now patrolling
            }
          }
        }
      }

      fill(255, 0, 0); // display player 1 after taking care of gap between cell wall and player
      rect(cell_startx + 1 + players.player1_x * cell_width, cell_starty + 1 + players.player1_y * cell_height, player_width, player_height);
      if (players.p1_blocks > 0) // if player 1 has put any blocks display them too
      {
        for (int i = 0; i < players.p1_blocks; i++)
        {
          rect(cell_startx + 1 + players.player1_blocks[i][0] * cell_width, cell_starty + 1 + players.player1_blocks[i][1] * cell_height, player_width, player_height);
        }
      }

      for (int i = 0; i < enemy_numbers; i++) { // check if any of the enemies are killing player 2
        if (players.player2_x == enemies[i].enemy_x && players.player2_y == enemies[i].enemy_y) {
          players.player2_reset(level);// if this enemy is killing player 2, reset player 2
          dead.cue(0);       // When cue is used, it always measures from the beginning of the recording. 
                             // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                             // no matter where it currently is.
          dead.play();       // plays the death sound when player dies
          visitcount_2 = 0;  // set visit to key location back to zero
          target.key_lost_2(); // drop the key that player 2 has picked up
          for (int j = 0; j < enemy_numbers; j++) // for all enemies
          {
            if (enemies[j].mode == 4) // if this enemy was patrolling target for player 2 then
            {
              enemies[j].mode = 0; // change the enemy to patrolling his own territory
              enemies[j].corner = 4;
              enemies[j].guarding = false; // the enemy is now patrolling
            }
          }
        }
      }

      fill(0, 255, 0); // display player 2 after taking care of gap between cell wall and player
      rect(cell_startx + 1 + players.player2_x * cell_width, cell_starty + 1 + players.player2_y * cell_height, player_width, player_height);
      if (players.p2_blocks > 0)
      {
        for (int i = 0; i < players.p2_blocks; i++) // if player 2 has put any blocks display them too
        {
          rect(cell_startx + 1 + players.player2_blocks[i][0] * cell_width, cell_starty + 1 + players.player2_blocks[i][1] * cell_height, player_width, player_height);
        }
      }

      if (!target.key1_taken) // if player 1 has lost his key, then
      {
        // fill(255, 0, 255);
        for (int i = 0; i < enemy_numbers; i++) // for all even enemies
        {
          if (enemies[i].mode == 3) {
            enemies[i].mode = 0; // change the enemy to patrolling his own territory
            enemies[i].corner = 4;
            enemies[i].guarding = false; // the enemy is now patrolling
          }
        }
      }

      if (!target.key2_taken) // if player 2 has lost his key, then
      {
        // fill(255, 0, 255);
        for (int i = 0; i < enemy_numbers; i++) // for all even enemies
        {
          if (enemies[i].mode == 4) {
            enemies[i].mode = 0; // change the enemy to patrolling his own territory
            enemies[i].corner = 4;
            enemies[i].guarding = false; // the enemy is now patrolling
          }
        }
      }

      if (target.key1_taken) // if player 1 has taken his key, then
      {
        // fill(255, 0, 255);
        for (int i = 0; i < enemy_numbers; i++) // for all even enemies
        {
          if (i % 2 == 0 && !enemies[i].guarding) {
            enemies[i].mode = 3; // make them patrol around the target for player 1
            enemies[i].guarding = true; // the enemy is now guarding
          }
        }
        if (!target.target1_placed) // if the target has not yet been shown on the maze
        {
          target.target1_placement(level, players.player1_x, players.player1_y, maze); // place the target for player 1 based on their location
        }
        image(player1_door, cell_startx + 1 + target.target1_x * cell_width, cell_starty + 1 + target.target1_y * cell_height); // place the image for the door
      } else if (players.p1_steps > 20) { // if the key has not been taken and if player 1 has moved more than 20 steps

        if (!target.key1_placed) { // if the key has not yet been displayed
          target.key1_placement(level, players.player1_x, players.player1_y, maze); // place the key for player 1 based on their location
          key_sound.cue(0);       // When cue is used, it always measures from the beginning of the recording. 
                                  // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                                  // no matter where it currently is.
          key_sound.play();       // plays the sound of key being displayed
        }

        image(player1_key, cell_startx + 1 + target.key1_x * cell_width, cell_starty + 1 + target.key1_y * cell_height); // place the image for the key
      }

      if (target.key2_taken) // if player 2 has taken his key, then
      {
        // fill(0, 255, 255);
        for (int i = 0; i < enemy_numbers; i++) // for all odd enemies
        {
          if (i % 2 == 1 && !enemies[i].guarding) {
            enemies[i].mode = 4; // make them patrol around the target for player 2
            enemies[i].guarding = true; // the enemy is now guarding
          }
        }
        if (!target.target2_placed) // if the target has not yet been shown on the maze
        {
          target.target2_placement(level, players.player2_x, players.player2_y, maze); // place the target for player 2 based on their location
        }
        image(player2_door, cell_startx + 1 + target.target2_x * cell_width, cell_starty + 1 + target.target2_y * cell_height); // place the image for the door
      } else if (players.p2_steps > 20) { // if the key has not been taken and if player 2 has moved more than 20 steps
        if (!target.key2_placed) { // if the key has not yet been displayed
          target.key2_placement(level, players.player2_x, players.player2_y, maze); // place the key for player 2 based on their location
          key_sound.cue(0);       // When cue is used, it always measures from the beginning of the recording. 
                                  // So cue(0) will set the "playhead" at 0 milliseconds from the beginning 
                                  // no matter where it currently is.
          key_sound.play();       // plays the sound of key being displayed
        }

        image(player2_key, cell_startx + 1 + target.key2_x * cell_width, cell_starty + 1 + target.key2_y * cell_height); // place the image for the key
      }


      fill(0, 0, 0); // enemies are displayed in balck
      for (int i = 0; i < enemy_numbers; i++) { // for all enemies, place them based of their location
        rect(cell_startx + 1 + enemies[i].enemy_x * cell_width, cell_starty + 1 + enemies[i].enemy_y * cell_height, player_width, player_height);
      }

      for (int i = 0; i < enemy_numbers; i++) // for every frame, call for the next move by the enemy
        players = enemies[i].getNextMove(maze, level, blk_limit, stuck_limit, players, frameRate, target);
    } else // if the game has been won by a player
    {
      if (players.p1_won) // if player 1 won the maze, display appropriate message
      {
        textFont(font, 30);
        fill(255, 255, 255);
        textAlign(CENTER);
        text("Player1 has won this maze!\nPress 'R' for new maze", displayHeight / 2, displayHeight / 2);
      }
      if (players.p2_won) // if player 2 won the maze, display appropriate message
      {
        textFont(font, 30);
        fill(255, 255, 255);
        textAlign(CENTER);
        text("Player2 has won this maze!\nPress 'R' for new maze", displayHeight / 2, displayHeight / 2);
      }
    }
  }
}

