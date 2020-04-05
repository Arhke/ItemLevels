package me.crz.minecraft.itemsystem.utils;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;

public class Ray {
	 
    public double        startX, startY, startZ;
    public double        endX, endY, endZ;
    public Collection<LivingEntity> entitySet;
    public Ray(Location loc1, Location loc2) {
        this.startX = loc1.getX();
        this.startY = loc1.getY();
        this.startZ = loc1.getZ();
        this.endX = loc2.getX();
        this.endY = loc2.getY();
        this.endZ = loc2.getZ();
        entitySet = loc1.getWorld().getLivingEntities();
    }
 
    public void removeNonCollidingAlongXZPlane() {
        Iterator<LivingEntity> entityIterator = entitySet.iterator();
        while (entityIterator.hasNext()) {
            AxisAlignedBB aabb = ((CraftEntity) entityIterator.next()).getHandle().getBoundingBox();
 
            double rectX = aabb.a, rectXLength = aabb.d - rectX;
            double rectZ = aabb.c, rectZLength = aabb.f - rectZ;
            Rectangle2D.Double entityRectangle = new Rectangle2D.Double(rectX, rectZ, rectXLength, rectZLength);
 
            boolean collided = entityRectangle.intersectsLine(startX, startZ, endX, endZ);
 
            if (!collided) entityIterator.remove();
        }
    }
 
    public void removeNonCollidingAlongXYPlane() {
        Iterator<LivingEntity> entityIterator = entitySet.iterator();
        while (entityIterator.hasNext()) {
            AxisAlignedBB aabb = ((CraftEntity) entityIterator.next()).getHandle().getBoundingBox();
 
            double rectX = aabb.a, rectXLength = aabb.d - rectX;
            double rectY = aabb.b, rectYLength = aabb.e - rectY;
            Rectangle2D.Double entityRectangle = new Rectangle2D.Double(rectX, rectY, rectXLength, rectYLength);
 
            boolean collided = entityRectangle.intersectsLine(startX, startY, endX, endY);
 
            if (!collided) entityIterator.remove();
        }
    }
 
    public void removeNonCollidingAlongZYPlane() {
        Iterator<LivingEntity> entityIterator = entitySet.iterator();
        while (entityIterator.hasNext()) {
            AxisAlignedBB aabb = ((CraftEntity) entityIterator.next()).getHandle().getBoundingBox();
 
            double rectZ = aabb.c, rectZLength = aabb.f - rectZ;
            double rectY = aabb.b, rectYLength = aabb.e - rectY;
            Rectangle2D.Double entityRectangle = new Rectangle2D.Double(rectZ, rectY, rectZLength, rectYLength);
 
            boolean collided = entityRectangle.intersectsLine(startZ, startY, endZ, endY);
 
            if (!collided) entityIterator.remove();
        }
    }
 
    public Collection<LivingEntity> removeAllNonColliding() {
        removeNonCollidingAlongXZPlane();
        removeNonCollidingAlongXYPlane();
        removeNonCollidingAlongZYPlane();
        return entitySet;
    }
}
