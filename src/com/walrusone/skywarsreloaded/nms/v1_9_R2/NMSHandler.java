package com.walrusone.skywarsreloaded.nms.v1_9_R2;

import java.util.List;
import java.util.Random;
import net.minecraft.server.v1_9_R2.EntityEnderDragon;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_9_R2.PlayerConnection;
import net.minecraft.server.v1_9_R2.TileEntityEnderChest;
import net.minecraft.server.v1_9_R2.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockIterator;

public class NMSHandler implements com.walrusone.skywarsreloaded.api.NMS
{
  public NMSHandler() {}
  
  public void respawnPlayer(Player player)
  {
    ((org.bukkit.craftbukkit.v1_9_R2.CraftServer)org.bukkit.Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);
  }
  
  public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
    net.minecraft.server.v1_9_R2.EnumParticle particle = net.minecraft.server.v1_9_R2.EnumParticle.valueOf(type);
    net.minecraft.server.v1_9_R2.PacketPlayOutWorldParticles particles = new net.minecraft.server.v1_9_R2.PacketPlayOutWorldParticles(particle, true, x, y, z, offsetX, offsetY, offsetZ, data, amount, new int[] { 1 });
    for (Player player : world.getPlayers()) {
      CraftPlayer start = (CraftPlayer)player;
      EntityPlayer target = start.getHandle();
      PlayerConnection connect = target.playerConnection;
      connect.sendPacket(particles);
    }
  }
  
  public org.bukkit.FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, org.bukkit.FireworkEffect.Type type) {
    return org.bukkit.FireworkEffect.builder().flicker(false).withColor(new Color[] { one, two, three, four }).withFade(five).with(type).trail(true).build();
  }
  
  public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
    PlayerConnection pConn = ((CraftPlayer)player).getHandle().playerConnection;
    PacketPlayOutTitle pTitleInfo = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadein, stay, fadeout);
    pConn.sendPacket(pTitleInfo);
    if (subtitle != null) {
      subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
      subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
      IChatBaseComponent iComp = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
      PacketPlayOutTitle pSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, iComp);
      pConn.sendPacket(pSubtitle);
    }
    if (title != null) {
      title = title.replaceAll("%player%", player.getDisplayName());
      title = ChatColor.translateAlternateColorCodes('&', title);
      IChatBaseComponent iComp = ChatSerializer.a("{\"text\": \"" + title + "\"}");
      PacketPlayOutTitle pTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, iComp);
      pConn.sendPacket(pTitle);
    }
  }
  
  public void sendActionBar(Player p, String msg) {
    String s = ChatColor.translateAlternateColorCodes('&', msg);
    IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + s + "\"}");
    net.minecraft.server.v1_9_R2.PacketPlayOutChat bar = new net.minecraft.server.v1_9_R2.PacketPlayOutChat(icbc, (byte)2);
      ((CraftPlayer)p).getHandle().playerConnection.sendPacket(bar);
  }
  
  public String getItemName(org.bukkit.inventory.ItemStack item) {
    return org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(item).getName();
  }
  
  public void playGameSound(Location loc, String sound, float volume, float pitch, boolean customSound) {
    if (customSound) {
      loc.getWorld().playSound(loc, sound, volume, pitch);
    } else {
      loc.getWorld().playSound(loc, org.bukkit.Sound.valueOf(sound), volume, pitch);
    }
  }
  
  public org.bukkit.inventory.ItemStack getMainHandItem(Player player) {
    return player.getInventory().getItemInMainHand();
  }
  
  public org.bukkit.inventory.ItemStack getOffHandItem(Player player) {
    return player.getInventory().getItemInOffHand();
  }
  
  public org.bukkit.inventory.ItemStack getItemStack(Material material, List<String> lore, String message) {
    org.bukkit.inventory.ItemStack addItem = new org.bukkit.inventory.ItemStack(material, 1);
    ItemMeta addItemMeta = addItem.getItemMeta();
    addItemMeta.setDisplayName(message);
    addItemMeta.setLore(lore);
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_DESTROYS });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_PLACED_ON });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
    addItem.setItemMeta(addItemMeta);
    return addItem;
  }
  
  public org.bukkit.inventory.ItemStack getItemStack(org.bukkit.inventory.ItemStack item, List<String> lore, String message)
  {
    org.bukkit.inventory.ItemStack addItem = item.clone();
    ItemMeta addItemMeta = addItem.getItemMeta();
    addItemMeta.setDisplayName(message);
    addItemMeta.setLore(lore);
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_DESTROYS });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_PLACED_ON });
    addItemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
    addItem.setItemMeta(addItemMeta);
    return addItem;
  }
  
  public boolean isValueParticle(String string)
  {
    try {
      org.bukkit.Particle.valueOf(string);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }
  
  public void updateSkull(Skull skull, java.util.UUID uuid)
  {
    skull.setSkullType(org.bukkit.SkullType.PLAYER);
    skull.setOwner(org.bukkit.Bukkit.getOfflinePlayer(uuid).getName());
  }
  
  public void setMaxHealth(Player player, int health) {
    player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
  }
  
  public void spawnDragon(World world, Location loc)
  {
    WorldServer w = ((CraftWorld)world).getHandle();
    EntityEnderDragon dragon = new EntityEnderDragon(w);
    dragon.getDragonControllerManager().setControllerPhase(net.minecraft.server.v1_9_R2.DragonControllerPhase.c);
    dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), w.random.nextFloat() * 360.0F, 0.0F);
    w.addEntity(dragon);
  }
  

  public Entity spawnFallingBlock(Location loc, Material mat, boolean damage)
  {
    FallingBlock block = loc.getWorld().spawnFallingBlock(loc, mat, (byte)0);
    block.setDropItem(false);
    net.minecraft.server.v1_9_R2.EntityFallingBlock fb = ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftFallingSand)block).getHandle();
    fb.a(damage);
    return block;
  }
  
  public void playEnderChestAction(Block block, boolean open)
  {
    Location location = block.getLocation();
    WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
    net.minecraft.server.v1_9_R2.BlockPosition position = new net.minecraft.server.v1_9_R2.BlockPosition(location.getX(), location.getY(), location.getZ());
    TileEntityEnderChest ec = (TileEntityEnderChest)world.getTileEntity(position);
    world.playBlockAction(position, ec.getBlock(), 1, open ? 1 : 0);
  }
  
  public void setEntityTarget(Entity ent, Player player)
  {
    net.minecraft.server.v1_9_R2.EntityCreature entity = (net.minecraft.server.v1_9_R2.EntityCreature)((org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity)ent).getHandle();
    entity.setGoalTarget(((CraftPlayer)player).getHandle(), null, false);
  }
  
  public void updateSkull(SkullMeta meta1, Player player)
  {
    meta1.setOwner(player.getName());
  }
  
  public org.bukkit.generator.ChunkGenerator getChunkGenerator()
  {
      return new org.bukkit.generator.ChunkGenerator()
      {
          public final ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid chunkGererator) {
              ChunkData chunkData = createChunkData(world);
              for (int i = 0; i < 16; i++) {
                  for (int j = 0; j < 16; j++) {
                      chunkGererator.setBiome(i, j, org.bukkit.block.Biome.VOID);
                  }
              }
              return chunkData;
          }
      };
  }
  
  public boolean checkMaterial(FallingBlock fb, Material mat)
  {
    if (fb.getMaterial().equals(mat)) {
      return true;
    }
    return false;
  }
  
  public org.bukkit.scoreboard.Objective getNewObjective(Scoreboard scoreboard, String criteria, String DisplayName)
  {
    return scoreboard.registerNewObjective(DisplayName, criteria);
  }
  
  public void setGameRule(World world, String rule, String bool)
  {
    world.setGameRuleValue(rule, bool);
  }
  
  public boolean headCheck(Block h1)
  {
    return h1.getType() == Material.SKULL;
  }
  
  public org.bukkit.inventory.ItemStack getBlankPlayerHead()
  {
    return new org.bukkit.inventory.ItemStack(Material.SKULL_ITEM, 1, (short)3);
  }
  
  public int getVersion()
  {
    return 9;
  }
  
  public org.bukkit.inventory.ItemStack getMaterial(String item)
  {
    if (item.equalsIgnoreCase("SKULL_ITEM")) {
      return new org.bukkit.inventory.ItemStack(Material.SKULL_ITEM, 1, (short)1);
    }
    return new org.bukkit.inventory.ItemStack(Material.valueOf(item), 1);
  }
  

  public org.bukkit.inventory.ItemStack getColorItem(String mat, byte color)
  {
    if (mat.equalsIgnoreCase("wool"))
      return new org.bukkit.inventory.ItemStack(Material.WOOL, 1, (short)color);
    if (mat.equalsIgnoreCase("glass"))
      return new org.bukkit.inventory.ItemStack(Material.STAINED_GLASS, 1, (short)color);
    if (mat.equalsIgnoreCase("banner")) {
      return new org.bukkit.inventory.ItemStack(Material.BANNER, 1, (short)color);
    }
    return new org.bukkit.inventory.ItemStack(Material.STAINED_GLASS, 1, (short)color);
  }
  

  public void setBlockWithColor(World world, int x, int y, int z, Material mat, byte cByte)
  {
    world.getBlockAt(x, y, z).setType(mat);
    world.getBlockAt(x, y, z).setData(cByte);
  }
  


  public void deleteCache() {}
  

  public Block getHitBlock(ProjectileHitEvent event)
  {
    BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(), event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0.0D, 4);
    Block hitBlock = null;
    while (iterator.hasNext()) {
      hitBlock = iterator.next();
      if (hitBlock.getType() != Material.AIR) {
        break;
      }
    }
    return hitBlock;
  }
}
