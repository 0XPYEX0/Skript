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
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.bukkitutil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import com.google.common.io.ByteStreams;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ch.njol.util.EnumTypeAdapter;
import ch.njol.skript.Skript;

/**
 * Contains helpers for Bukkit's not so safe stuff.
 */
@SuppressWarnings("deprecation")
public class BukkitUnsafe {
	
	/**
	 * Bukkit's UnsafeValues allows us to do stuff that would otherwise
	 * require NMS. It has existed for a long time, too, so 1.9 support is
	 * not particularly hard to achieve.
	 * 
	 * UnsafeValues' existence and behavior is not guaranteed across future versions.
	 */
	@Nullable
	private static final UnsafeValues unsafe = Bukkit.getUnsafe();

	static {
		if (unsafe == null)
			throw new Error("UnsafeValues are not available.");
	}

	/**
	 * Maps pre 1.12 ids to materials for variable conversions.
	 */
	@Nullable
	private static Map<Integer,Material> idMappings;

	/**
	 * @deprecated You should use {@link BukkitUnsafe#getMaterialFromNamespacedId(String)}
	 * Get a material from a minecraft id.
	 *
	 * @param id Namespaced id (just like 'minecraft:dirt'), or normally just a material name (just like 'dirt')
	 * @return Material or null
	 */
	@Nullable
	@Deprecated
	public static Material getMaterialFromMinecraftId(String id) {
		return getMaterialFromNamespacedId(id);
	}

	/**
	 * Get a material from a namespaced id.
	 * Such as, minecraft:iron_ingot -> IRON_INGOT; mod:an_item -> mod_an_item
	 *
	 * @param id Namespaced id (just like 'minecraft:dirt'), or normally just a material name (just like 'dirt')
	 * @return Material or null
	 */
	@Nullable
	public static Material getMaterialFromNamespacedId(String id) {
		return Material.matchMaterial(id.toLowerCase().startsWith(NamespacedKey.MINECRAFT + ":")
										  ? id
										  : id.replace(":", "_")  //For Hybrid Server
		);
	}

	public static void modifyItemStack(ItemStack stack, String arguments) {
		if (unsafe == null)
			throw new IllegalStateException("modifyItemStack could not be performed as UnsafeValues are not available.");
		unsafe.modifyItemStack(stack, arguments);
	}
	
	private static void initIdMappings() {
		try (InputStream is = Skript.getInstance().getResource("materials/ids.json")) {
			if (is == null) {
				throw new AssertionError("missing id mappings");
			}
			String data = new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
			
			Type type = new TypeToken<Map<Integer,String>>(){}.getType();
			Map<Integer, String> rawMappings = new GsonBuilder().
				registerTypeAdapterFactory(EnumTypeAdapter.factory)
				.create().fromJson(data, type);
			
			// Process raw mappings
			Map<Integer, Material> parsed = new HashMap<>(rawMappings.size());
			// Legacy material conversion API
			for (Map.Entry<Integer, String> entry : rawMappings.entrySet()) {
				parsed.put(entry.getKey(), Material.matchMaterial(entry.getValue(), true));
			}
			idMappings = parsed;
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	@Nullable
	public static Material getMaterialFromId(int id) {
		if (idMappings == null) {
			initIdMappings();
		}
		assert idMappings != null;
		return idMappings.get(id);
	}
}
