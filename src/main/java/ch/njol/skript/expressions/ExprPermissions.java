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

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.WeatherType;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.eclipse.jdt.annotation.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Name("Permissions")
@Description("Returns all permissions of the defined player(s). Note that the modifications to resulting list do not actually change permissions.")
@Examples("set {_permissions::*} to all permissions of the player")
@Since("2.2-dev33")
public class ExprPermissions extends PropertyExpression<Player, String> {
	
	static {
		Skript.registerExpression(ExprPermissions.class, String.class, ExpressionType.PROPERTY, "[(all [[of] the]|the)] permissions of %players%", "%players%['s] permissions");
	}

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		setExpr((Expression<? extends Player>) exprs[0]);
		return true;
	}
	
	@SuppressWarnings("null")
	@Override
	protected String[] get(final Event event, final Player[] source) {
		final Set<String> permissions = new HashSet<>();
		for (final Player player : getExpr().getArray(event)) 
			for (final PermissionAttachmentInfo permission : player.getEffectivePermissions())
				permissions.add(permission.getPermission());
		return permissions.toArray(new String[permissions.size()]);
	}
	
	@Override
	public Class<String> getReturnType() {
		return String.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "permissions" + (getExpr().isDefault() ? "" : " of " + getExpr().toString(e, debug));
	}

}
