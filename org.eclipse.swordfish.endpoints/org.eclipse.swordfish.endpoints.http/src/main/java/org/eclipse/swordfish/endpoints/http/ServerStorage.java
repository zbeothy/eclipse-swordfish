package org.eclipse.swordfish.endpoints.http;

import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Server;

public class ServerStorage {
	private static ServerStorage serverStorage;
	private Map<Integer, Server> servers = new HashMap<Integer, Server>();

	public synchronized Server getServer(int portNumber) {
		Server ret = null;
		if (!servers.containsKey(portNumber)) {
			ret = new Server(portNumber);
			servers.put(portNumber, ret);
		}
		ret = servers.get(portNumber);
		return ret;
	}

	public static synchronized ServerStorage getServerStorage() {
		if (serverStorage == null) {
			serverStorage = new ServerStorage();
		}
		return serverStorage;
	}

	protected ServerStorage() {
		// TODO Auto-generated constructor stub
	}

	public static synchronized void setServerStorage(ServerStorage serverStorage) {
		ServerStorage.serverStorage = serverStorage;
	}
}
