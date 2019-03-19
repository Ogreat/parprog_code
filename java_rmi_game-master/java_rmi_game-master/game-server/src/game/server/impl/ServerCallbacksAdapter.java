package game.server.impl;

import game.enigne.Player;
import game.server.ServerCallbacks;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class ServerCallbacksAdapter extends UnicastRemoteObject implements
		ServerCallbacks {

	private static final long serialVersionUID = 1079040465565540056L;

	protected ServerCallbacksAdapter() throws RemoteException {
		super();
	}

	public abstract void boardChanege() throws RemoteException;
	public abstract void nextPlayer(Player next) throws RemoteException;
	public abstract void updateScore(int playerID, int score) throws RemoteException;
	public abstract void startGame() throws RemoteException;
	public abstract void newMessageForChat(String name, String text) throws RemoteException;
}
