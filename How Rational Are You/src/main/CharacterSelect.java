package main;

import conn.*;
import conn.Packet.Packet2JoinRequest;
import conn.Packet.*;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;

public class CharacterSelect extends BasicTWLGameState {

	private final int cancelled = -2;
	private final int player1char = 5;
	private final int player2char = 6;
	
	SpriteSheet gridSheet;
	public Client client;
	DialogLayout firstPanel;
	
	int clock;
	int player;
	boolean picking, selected, p1chosen, p2chosen;
	
	int gcw;
	int gch;
	
	int startx = 25;
	int starty = 75;
	
	int currentx, selectx;
	int currenty, selecty;

	boolean mouseDown;
	String mouse;
	
	private Image emptyGrid, hoverGrid, selectGrid, chosenGrid;
	private Image player_character_bg;
	
	private Character[] characters;
	private Character selectedCharacter;
	CharacterSheet characterSheet;
	
	int p1charid, p2charid;
	String p1name, p2name;
	
	Label lTitle, lStatus, lSelCharacter, lP1Name, lP2Name;
	Button btnSelect;
	
	
	public CharacterSelect(int main) {
		client = HRRUClient.conn.getClient();
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		rootPane.removeAllChildren();
		player = HRRUClient.cs.getPlayer();
		p1name = HRRUClient.cs.getP1Name();
		p2name = HRRUClient.cs.getP2Name();
		
        btnSelect.setVisible(false);
		lSelCharacter.setVisible(false);
		
		mouseDown = false;
		mouse = "no input";
		clock = 200;
		
		currentx = -1;
		selectx = -1;
		currenty = -1; 
		selecty = -1;
		
		p1chosen = false;
		p2chosen = false;
		picking = false;
		selected = false;
		
		selectedCharacter = null;
		
		if(player == 1) {
			picking = true;
			lStatus.setText(p1name + " you're first!");
		}
		else {
			player = 2;
			lStatus.setText("Waiting for " + p1name);
		}
		
		lP1Name.setText(p1name);
		lP2Name.setText(p2name);
		
		rootPane.add(btnSelect);
		rootPane.add(lTitle);
		rootPane.add(lStatus);
		rootPane.add(lSelCharacter);
		rootPane.add(lP1Name);
		rootPane.add(lP2Name);
		rootPane.setTheme("");		
	}

	public void chooseCharacter()
	{
		if(player == 1)
		{
			lStatus.setText("Waiting for " + p2name);
			picking = false;
			HRRUClient.cs.setP1Character(selectedCharacter.getId());
			HRRUClient.cs.setState(player1char);
			p1charid = HRRUClient.cs.getP1Character();
			
			Packet9CharacterSelect characterRequest = new Packet9CharacterSelect();
			characterRequest.sessionID = HRRUClient.cs.getSessionID();
			characterRequest.player = player;
			characterRequest.characterID = HRRUClient.cs.getP1Character();
			client.sendTCP(characterRequest);
			
			btnSelect.setEnabled(false);
			p1chosen = true;
		}
		else if(player == 2)
		{
			picking = false;
			HRRUClient.cs.setP2Character(selectedCharacter.getId());
			HRRUClient.cs.setState(player2char);
			p2charid = HRRUClient.cs.getP2Character();
			
			Packet9CharacterSelect characterRequest = new Packet9CharacterSelect();
			characterRequest.sessionID = HRRUClient.cs.getSessionID();
			characterRequest.player = player;
			characterRequest.characterID = HRRUClient.cs.getP2Character();
			client.sendTCP(characterRequest);
			
			btnSelect.setEnabled(false);
			p2chosen = true;
		}
	}
	
	@Override
	protected RootPane createRootPane() {
		assert rootPane == null : "RootPane already created";

		RootPane rp = new RootPane(this);
		rp.setTheme("");
		rp.getOrCreateActionMap().addMapping(this);
		return rp;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		gcw = gc.getWidth();
		gch = gc.getHeight();

		player_character_bg = new Image("res/simple/player_character_bg.png");
		
		gridSheet = new SpriteSheet("res/simple/character_select.png", 36,36);
		emptyGrid = gridSheet.getSprite(0,0);
		hoverGrid = gridSheet.getSprite(1,0);
		selectGrid = gridSheet.getSprite(2,0);
		chosenGrid = gridSheet.getSprite(3,0);
		
		characterSheet = new CharacterSheet();
		characters = characterSheet.getCharacters();
		
		lTitle = new Label("Choose Your Character!");
		lStatus = new Label("");
		lSelCharacter = new Label("");
		
		lP1Name = new Label("");
		lP2Name= new Label("");

		btnSelect = new Button("Select Character");
		
		btnSelect.setPosition(450, 225);
		btnSelect.setSize(300, 20);
        btnSelect.addCallback(new Runnable() {
            public void run() {
            	chooseCharacter();
            }
        });
		lTitle.setPosition(30,40);
		lTitle.setTheme("atarititle");
		
		lStatus.setPosition(50,80);
		lStatus.setTheme("atarisubtitle");
		
		lSelCharacter.setPosition(540, 195);
		lSelCharacter.setTheme("atarisubtitle");

		lP1Name.setPosition(540, 335);
		lP2Name.setPosition(540, 405);
		lP1Name.setTheme("atarisubtitle");
		lP2Name.setTheme("atarisubtitle");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawString(mouse, 650, 500);
		g.scale(2f, 2f);
		gridSheet.setFilter(Image.FILTER_NEAREST);
		player_character_bg.setFilter(Image.FILTER_NEAREST);
		g.drawImage(player_character_bg, 225, 150);
		
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++)
				g.drawImage(emptyGrid, startx+i*37, starty+j*37);
		if(picking)
		{
			if(currentx>=0 && currenty>=0)
				g.drawImage(hoverGrid, startx+currentx*37, starty+currenty*37);
		}
		
		if(selected)
		{
			g.drawImage(selectGrid, startx+selectx*37, starty+selecty*37);
			g.drawImage(selectedCharacter.getCharacterImage(), 225, 75);
		}
		
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++)
				g.drawImage(characters[(i)+(j*5)].getCharacterImage(), startx+i*37, starty+j*37);
		
		if(p1chosen)
		{
			g.drawImage(chosenGrid, startx+(characters[p1charid].getPositionx()*37), starty+(characters[p1charid].getPositiony()*37));
			g.drawImage(characters[p1charid].getCharacterImage(), startx+characters[p1charid].getPositionx()*37, starty+characters[p1charid].getPositiony()*37);
			g.drawImage(characters[p1charid].getCharacterImage(), 225, 150);
		}
		if(p2chosen)
		{
			g.drawImage(chosenGrid, startx+(characters[p2charid].getPositionx()*37), starty+(characters[p2charid].getPositiony()*37));
			g.drawImage(characters[p2charid].getCharacterImage(), startx+characters[p2charid].getPositionx()*37, starty+characters[p2charid].getPositiony()*37);
			g.drawImage(characters[p2charid].getCharacterImage(), 225, 187);
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		int xpos = Mouse.getX();
		int ypos= Mouse.getY();
		mouse = "xpos: " + xpos + "\nypos: " + ypos;
		
		if(player == 1) {
			if(HRRUClient.cs.getState() == player2char) {
				p2charid = HRRUClient.cs.getP2Character();
				p2chosen = true;
			}
		}
		else if(player == 2) {
			if(HRRUClient.cs.getState() == player1char) {
				p1charid = HRRUClient.cs.getP1Character();
				p1chosen = true;
				lStatus.setText(p2name + ", it's your turn!");
				picking = true;
			}
		}
		
		if(HRRUClient.cs.getState() == cancelled) {
			if(player == 1)
				sbg.enterState(1);
			else
				sbg.enterState(2);
		}
		
		if(picking) {
			btnSelect.setEnabled(true);
			if((xpos>startx*2 && xpos < startx*2+(74*5)) && (ypos>80 && ypos<gch-starty*2)) {
				currentx = (xpos-startx*2)/74;
				currenty = ((gch-ypos)-starty*2)/74;
				if(input.isMouseButtonDown(0)) {
					selectx = currentx;
					selecty = currenty;
					selected = true;
					selectedCharacter = characters[(selectx)+(selecty*5)];
					lSelCharacter.setVisible(true);
					lSelCharacter.setText(selectedCharacter.getName());
					btnSelect.setVisible(true);
				}
			}
			else {
				currentx = -1;
				currenty = -1;
			}
			if(player == 2 && selected) {
				if(selectedCharacter.getId() == HRRUClient.cs.getP1Character())
					btnSelect.setEnabled(false);
			}
		}
		
		if(HRRUClient.cs.getState() == player2char)
		{
			clock--;
			lStatus.setText("Game Starting in " + (clock/100+1) + "...");
			if(clock<0)
			{
				ConnectionState connectionobject = HRRUClient.cs;
				sbg.enterState(3);
			}
		}
	}

	@Override
	public int getID() {
		return 4;
	}

}