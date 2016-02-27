package org.jurassicraft.server.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.client.animation.Animations;
import org.jurassicraft.server.entity.ai.animations.JCNonAutoAnimSoundBase;
import org.jurassicraft.server.entity.base.AggressiveDinosaurEntity;

public class IndominusEntity extends AggressiveDinosaurEntity // implements ICarnivore, IEntityAICreature
{
    private float[] newSkinColor = new float[3];
    private float[] skinColor = new float[3];

    private int stepCount = 0;

    private boolean isCamouflaging;

    public IndominusEntity(World world)
    {
        super(world);

        injuredSounds = new String[] { "indominus_hurt_1" };
        idleSounds = new String[] { "indominus_living_1", "indominus_breath" };
        dyingSounds = new String[] { "indominus_death_1" };

        tasks.addTask(2, new JCNonAutoAnimSoundBase(this, 75, Animations.IDLE.get(), 750, getIdleSound(), 1.5F));
        this.addAIForAttackTargets(EntityLivingBase.class, 0);
        this.defendFromAttacker(EntityLivingBase.class, 0);
    }

    @Override
    public int getTailBoxCount()
    {
        return 7;
    }

    @Override
    public void entityInit()
    {
        super.entityInit();

        this.dataWatcher.addObject(31, (byte) 0);
    }

    @Override
    public void applySettingsForActionFigure()
    {
        super.applySettingsForActionFigure();
        isCamouflaging = false;
        skinColor = new float[] { 255, 255, 255 };
        newSkinColor = new float[] { 255, 255, 255 };
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        /** Step Sound */
        if (this.moveForward > 0 && this.stepCount <= 0)
        {
            this.playSound("jurassicraft:stomp", (float) transitionFromAge(0.1F, 1.0F), this.getSoundPitch());
            stepCount = 65;
        }

        this.stepCount -= this.moveForward * 9.5;

        if (worldObj.isRemote)
        {
            isCamouflaging = this.dataWatcher.getWatchableObjectByte(31) == 1;
            changeSkinColor();
        }
        else
        {
            this.dataWatcher.updateObject(31, (byte) (isCamouflaging ? 1 : 0));
        }
        // if (getAnimation() == 0)
        // AnimationAPI.sendAnimPacket(this, 1);
    }

    @Override
    public float getSoundVolume()
    {
        return (float) transitionFromAge(0.9F, 1.6F) + ((rand.nextFloat() - 0.5F) * 0.125F);
    }

    public boolean isCamouflaging()
    {
        return isCamouflaging;
    }

    public void changeSkinColor()
    {
        BlockPos pos = new BlockPos(this).offset(EnumFacing.DOWN);
        IBlockState state = this.worldObj.getBlockState(pos);

        int color;

        if (isCamouflaging())
        {
            color = state.getBlock().colorMultiplier(this.worldObj, pos);

            if (color == 0xFFFFFF)
            {
                color = state.getBlock().getMapColor(state).colorValue;
            }
        }
        else
        {
            color = 0xFFFFFF;
        }

        if (color != 0)
        {
            this.newSkinColor[0] = color >> 16 & 255;
            this.newSkinColor[1] = color >> 8 & 255;
            this.newSkinColor[2] = color & 255;

            if (this.skinColor[0] == 0 && this.skinColor[1] == 0 && this.skinColor[2] == 0)
            {
                this.skinColor[0] = this.newSkinColor[0];
                this.skinColor[1] = this.newSkinColor[1];
                this.skinColor[2] = this.newSkinColor[2];
            }
        }

        for (int i = 0; i < 3; ++i)
        {
            if (this.skinColor[i] < this.newSkinColor[i])
            {
                ++this.skinColor[i];
            }

            if (this.skinColor[i] > this.newSkinColor[i])
            {
                --this.skinColor[i];
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public float[] getSkinColor()
    {
        return new float[] { this.skinColor[0] / 255.0F, this.skinColor[1] / 255.0F, this.skinColor[2] / 255.0F };
    }
}
