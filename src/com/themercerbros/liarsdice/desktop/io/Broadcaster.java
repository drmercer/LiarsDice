package com.themercerbros.liarsdice.desktop.io;

import com.themercerbros.liarsdice.desktop.player.Player;

public interface Broadcaster {
	public void msg(Player speaker, String message);
}
