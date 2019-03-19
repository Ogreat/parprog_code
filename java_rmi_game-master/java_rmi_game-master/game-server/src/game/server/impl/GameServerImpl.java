package game.server.impl;


import game.enigne.*;
import game.enigne.callbacks.*;
import game.server.*;

import java.awt.Color;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class GameServerImpl extends UnicastRemoteObject implements GameServer {

	private static final long serialVersionUID = -7361683859730230055L;
	private static GameBoard board;
	private static GameServerImpl obj;
	public static final int DEF_SIZE = 7;
	private static List<ServerCallbacks> callbacks = null;
	private List<Player> playersList;
	private static int playerCount = 0;
	private static Scanner sc;
	private static boolean gameOn = false;
	
	@Override
	public void addCallbackListener(ServerCallbacks listener) {
		callbacks.add(listener);
	}
	
	public GameServerImpl(int w, int h) throws RemoteException 
	{ 
		board = new GameBoard(w<1 ? DEF_SIZE : w, h<1 ? DEF_SIZE : h);
		callbacks = new ArrayList<ServerCallbacks>();
		playersList = new ArrayList<Player>();
		System.out.println("-> Gameboard created");
		System.out.println("\tWidth " + board.getWidht());
		System.out.println("\tHeigth " + board.getHeight());
		board.addCallbackListener(new BoardCallbacks() {
			@Override
			public void boardChanege() {
				if (callbacks != null)
					try {
						for(ServerCallbacks sc : callbacks) sc.boardChanege();
					} catch (RemoteException e) { e.printStackTrace(); }
			}
			@Override
			public void nextPlayer(Player next) 
			{ 
				System.out.println("-> Next: " + next.getName());
				
				if (callbacks != null)
					try {
						for(ServerCallbacks sc : callbacks) sc.nextPlayer(next);
					} catch (RemoteException e) { e.printStackTrace(); }
			}
			@Override
			public void updateScore(Player player) {
				System.out.println("-> Update score: ");
				printPlayersList();
				if (callbacks != null)
					try {
						for(ServerCallbacks sc : callbacks) sc.updateScore(player.getID(), player.getScore());
					} catch (RemoteException e) { e.printStackTrace(); }
			}
			@Override
			public void gameOver(Player winner) {
				Collections.sort(playersList, new PlayerComp());
				if (callbacks != null)
					try {
						for(ServerCallbacks sc : callbacks) sc.gameOver(winner.getID());
					} catch (RemoteException e) { e.printStackTrace(); }
				System.out.println("-> Game Over");
				System.out.println("-> Winner: " + winner.getName());
				printPlayersList();		
			}
		});
		System.out.println("-> Gameboard listener added");
	}

	public static void main(String[] args) {
		sc = new Scanner(System.in);
		System.out.println("-> Start server ...");
		try {
			if (args.length<2) obj = new GameServerImpl(-1,-1);
			else obj = new GameServerImpl(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
            Naming.rebind(RMI_NAME, obj);
            System.out.println("-> Server ready ...");
            System.out.println("-> Waiting for players ...");
            boolean flag = true;
            while(flag) {
            	if (playerCount>=2) {
            		System.out.println("-> There are " + playerCount + " players. Start game? [y/n]");
        			String out = sc.next();
        			if (out.equals("y") || out.equals("Y"))  
        			{ 
        				for(ServerCallbacks sc : callbacks) sc.startGame();
        				board.startGame(); flag = false; gameOn = true;  }
        			else System.out.println("-> Waiting for players ...");
            	}
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
	}

	@Override
	public int getWidht() throws RemoteException 
	{ return board.getWidht(); }
	@Override
	public int getHeight() throws RemoteException 
	{ return board.getHeight(); }
	@Override
	public Player getCellPlayer(int i, int j) throws RemoteException 
	{ return board.getCellPlayer(i, j); }
	@Override
	public BoardWall getCellWall(int i, int j, Side side) throws RemoteException 
	{ return board.getCellWall(i, j, side); }
	@Override
	public String getCellInfo(int i, int j) throws RemoteException 
	{ return board.getCellInfo(i, j); }
	@Override
	public String getCellPlayerInfo(int i, int j) throws RemoteException 
	{ return board.getCellPlayerInfo(i, j); }
	@Override
	public int getCurrentPlayerID() throws RemoteException 
	{ return board.getCurrentPlayer().getID(); }
	@Override
	public List<Player> getPlayerslist() throws RemoteException {
		return playersList;
	}
	@Override
	public void cellClick(int i, int j, Side side) throws RemoteException { board.cellClick(i, j, side); }

	private void printPlayersList() {
		for(Player p : playersList) 
			System.out.println("\t " + p.getID() + " " + p.getName() + " $" + p.getScore());
	}
	@Override
	public int addPlayer(String name, int figure, String colorS) throws RemoteException {
		if (!gameOn) {
		Color c = new Color(Integer.parseInt(colorS));
		Player p = new Player(name, figure, c);
		board.newPlayer(p);
		playersList.add(p);
		System.out.println("-> Player " + name + " joined ...");
		playerCount++;
		return p.getID();	
		} else return -1;
	}
	@Override
	public void newMessage(String name, String text) throws RemoteException {
		if (callbacks != null)
		try {
			for(ServerCallbacks sc : callbacks) sc.newMessageForChat(name, text);
		} catch (RemoteException e) { e.printStackTrace(); }
		
	}
}
