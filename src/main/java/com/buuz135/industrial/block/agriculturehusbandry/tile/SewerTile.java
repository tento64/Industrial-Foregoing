package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class SewerTile extends IndustrialAreaWorkingTile {

    @Save
    public PosFluidTank sewage;
    @Save
    public PosFluidTank essence;

    public SewerTile() {
        super(ModuleAgricultureHusbandry.SEWER, RangeManager.RangeType.TOP);
        this.addTank(sewage = new SidedFluidTank("sewage", 8000, 45, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(PosFluidTank.Action.DRAIN).
                setTile(this));
        this.addTank(essence = new SidedFluidTank("essence", 8000, 66, 20, 1).
                setColor(DyeColor.LIME).
                setTankAction(PosFluidTank.Action.DRAIN).
                setTile(this));
    }

    @Override
    public WorkAction work() {
        List<AnimalEntity> animals = this.world.getEntitiesWithinAABB(AnimalEntity.class, getWorkingArea().getBoundingBox());
        int amount = 0;
        for (AnimalEntity animalEntity : animals) {
            if (hasEnergy(10 * (amount + 1))) {
                sewage.fillForced(new FluidStack(ModuleCore.SEWAGE.getSourceFluid(), 50), IFluidHandler.FluidAction.EXECUTE);
                ++amount;
            } else {
                break;
            }
        }
        List<ExperienceOrbEntity> orb = this.world.getEntitiesWithinAABB(ExperienceOrbEntity.class, getWorkingArea().getBoundingBox());
        for (ExperienceOrbEntity experienceOrbEntity : orb) {
            if (experienceOrbEntity.isAlive() && essence.getFluidAmount() + experienceOrbEntity.xpValue * 20 <= essence.getCapacity()) {
                essence.fillForced(new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), experienceOrbEntity.xpValue * 20), IFluidHandler.FluidAction.EXECUTE);
                experienceOrbEntity.remove();
            }
        }
        return new WorkAction(1, 10 * amount);
    }
}
