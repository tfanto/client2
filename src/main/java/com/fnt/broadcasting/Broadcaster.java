package com.fnt.broadcasting;

import java.util.HashSet;
import java.util.Set;

public class Broadcaster {

	private static final Set<BroadcastListener> listeners = new HashSet<>();

	public static void register(BroadcastListener listener) {
			 listeners.add(listener);
	}

	public static void unregister(BroadcastListener listener) {
		listeners.remove(listener);
	}

	public static void broadcast(final String message) {
		for (BroadcastListener listener : listeners) {
			listener.receiveBroadcast(message);
		}
	}

	public interface BroadcastListener {
		public void receiveBroadcast(String message);
	}

}
