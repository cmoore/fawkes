

package io.ivy.fawkes;

import org.bukkit.Material;

public class LootTables {

	public static Material get_random_material() {
		Material[] values = Material.values();
		return values[Utils.random_chance(1,values.length)];
	}
	
	public static Material[] basic_loot = {
		Material.IRON_HELMET,
		Material.IRON_BOOTS,
		Material.IRON_PICKAXE,
		Material.IRON_AXE,
		Material.COAL,
		Material.BAKED_POTATO,
		Material.ENDER_PEARL,
		Material.CAKE,
		Material.WHEAT,
		Material.IRON_LEGGINGS };
	
	public static Material[] premium_loot = {
		Material.GLASS_BOTTLE,
		Material.GLOWSTONE_DUST,
		Material.ACTIVATOR_RAIL,
		Material.ANVIL,
		Material.GOLD_AXE,
		Material.GOLD_HOE };
}
