package game.server;

import game.enigne.Player;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerCallbacks extends Remote, Serializable {

	public void boardChanege() throws RemoteException;
	public void nextPlayer(Player next) throws RemoteException;
	public void updateScore(int playerID, int score) throws RemoteException;
	public void startGame() throws RemoteException;
	public void newMessageForChat(String name, String text) throws RemoteException;
	public void gameOver(int idWinner) throws RemoteException;

}
