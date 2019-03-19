package game.server;

import game.enigne.BoardWall;
import game.enigne.Player;
import game.enigne.Side;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameServer extends Remote {
	
	public static String RMI_SERVER = "rmi://localhost/";
	public static String RMI_NAME = "GameServer";
	
	public int getWidht() throws RemoteException;
	public int getHeight() throws RemoteException;
	
	public Player getCellPlayer(int i, int j) throws RemoteException;
	public BoardWall getCellWall(int i, int j, Side side) throws RemoteException;
	public String getCellInfo(int i, int j) throws RemoteException;
	public String getCellPlayerInfo(int i, int j) throws RemoteException;
	public int getCurrentPlayerID() throws RemoteException;
	public List<Player> getPlayerslist() throws RemoteException;
	public void cellClick(int i, int j,Side side) throws RemoteException;
	public void addCallbackListener(ServerCallbacks listener) throws RemoteException;
	public int addPlayer(String name, int figure, String colorS) throws RemoteException;
	public void newMessage(String name, String text) throws RemoteException;	
}