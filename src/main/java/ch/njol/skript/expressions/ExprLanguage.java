/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package ch.njol.skript.expressions;

import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("Language")
@Description({"Currently selected game language of a player. The value of the language is not defined properly.",
			"The vanilla Minecraft client will use lowercase language / country pairs separated by an underscore, but custom resource packs may use any format they wish.",
			"This expression is Minecraft 1.12+."})
@Examples({"message \"%player's current hotbar slot%\"",
            "set player's selected hotbar slot to slot 4 of player"})
@Since("INSERT VERSION")
public class ExprLanguage extends SimplePropertyExpression<Player, String> {

	static {
		if (Skript.methodExists(Player.class, "getLocale")) {
			register(ExprLanguage.class, String.class, "[([currently] selected|current)] [game] (language|locale) [setting]", "players");
		}
	}
	
	@Override
	@Nullable
	public String convert(Player p) {
		return p.getLocale();
	}
	
	@Override
	protected String getPropertyName() {
		return "language";
	}
	
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}
	
}